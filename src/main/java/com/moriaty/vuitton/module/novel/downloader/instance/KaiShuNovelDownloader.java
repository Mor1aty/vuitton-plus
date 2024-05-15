package com.moriaty.vuitton.module.novel.downloader.instance;

import com.moriaty.vuitton.bean.novel.network.NovelNetworkChapter;
import com.moriaty.vuitton.bean.novel.network.NovelNetworkContent;
import com.moriaty.vuitton.bean.novel.network.NovelNetworkInfo;
import com.moriaty.vuitton.module.novel.downloader.BaseNovelDownloader;
import com.moriaty.vuitton.module.novel.downloader.NovelDownloaderMeta;
import com.moriaty.vuitton.module.novel.downloader.dom.*;
import com.moriaty.vuitton.util.NovelUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * <p>
 * 快书小说 Downloader
 * </p>
 *
 * @author Moriaty
 * @since 2024/5/15 下午8:08
 */
@Component
@Slf4j
public class KaiShuNovelDownloader extends BaseNovelDownloader {

    private final NovelDownloaderMeta meta = new NovelDownloaderMeta()
            .setWebName("快书网")
            .setMark("KaiShu")
            .setWebsite("https://www.kuaishu5.com/")
            .setContentBaseUrl("https://www.kuaishu5.com/");

    @Override
    public NovelDownloaderMeta getMeta() {
        return meta;
    }

    @Override
    public NovelNetworkInfo findInfo(String catalogueUrl) {
        try {
            Document doc = NovelUtil.findDocWithCharset(meta.getWebsite() + catalogueUrl);
            DomActionParam nameParam = new DomActionParam(doc, List.of(
                    new IdDomAction("info"), new TagDomAction("h1")),
                    Function.identity());
            DomActionParam authorParam = new DomActionParam(doc, List.of(
                    new IdDomAction("info"), new TagDomAction("p")),
                    author -> author.replace("作者：", "").trim());
            DomActionParam introParam = new DomActionParam(doc,
                    List.of(new IdDomAction("intro")),
                    Function.identity());
            NovelNetworkInfo info = DomActor.findInfo(nameParam, authorParam, introParam, null);
            if (info == null) {
                return null;
            }
            info.setDownloaderMark(meta.getMark()).setDownloaderCatalogueUrl(catalogueUrl);
            return info;
        } catch (URISyntaxException | IOException e) {
            log.error("获取信息异常", e);
            return null;
        }
    }

    @Override
    public List<NovelNetworkChapter> findChapterList(String catalogueUrl) {
        try {
            Document doc = NovelUtil.findDocWithCharset(meta.getWebsite() + catalogueUrl);
            Elements domOptions = findSelectOption(doc);
            if (domOptions == null) {
                return List.of();
            }
            List<NovelNetworkChapter> chapterList = new ArrayList<>();
            int index = 0;
            for (int i = 0; i < domOptions.size(); i++) {
                Elements domAs = findChapterDomAs(i, doc, meta.getWebsite() + catalogueUrl
                                                          + "/p" + (i + 1) + ".html");
                if (domAs == null || domAs.isEmpty()) {
                    return List.of();
                }
                for (Element domA : domAs) {
                    Elements domAdds = domA.getElementsByTag("dd");
                    if (domAdds.isEmpty()) {
                        log.warn("章节 {} 获取失败", domA);
                    } else {
                        Element domAdd = domAdds.getFirst();
                        chapterList.add(new NovelNetworkChapter()
                                .setIndex(index)
                                .setTitle(domAdd.text())
                                .setContentUrl(domA.attr("href")));
                    }
                    index++;
                }
            }
            return chapterList;
        } catch (URISyntaxException | IOException e) {
            log.error("获取章节列表异常", e);
            return List.of();
        }
    }

    @Override
    public NovelNetworkContent findContent(String title, String contentUrl) {
        try {
            return exploreContent(title, meta.getContentBaseUrl() + contentUrl, "booktxt");
        } catch (IOException e) {
            return new NovelNetworkContent()
                    .setErrorMsg("获取小说内容发生异常, " + e.getLocalizedMessage());
        }
    }

    @Override
    public String removeAbnormalContent(String content) {
        return super.removeAbnormalContent(content)
                .replace("(本章完)", "")
                .replace("笔趣亭为广大书友们提供好看的网络小说全文免费在线阅读，如果您喜欢本站，请分享给更多的书友们！",
                        "")
                .replace("如果您觉得《冰与火之铁王座》小说很精彩的话，请粘贴以下网址分享给您的好友，谢谢支持！", "")
                .replace("（ 本书网址：https://m.biquting.net/book_18370261.html ）", "");
    }

    private Elements findChapterDomAs(int index, Document doc, String url) throws IOException, URISyntaxException {
        if (index != 0) {
            doc = NovelUtil.findDocWithCharset(url);
        }
        Element domChapter = doc.getElementById("content_1");
        if (domChapter == null) {
            return null;
        }
        return domChapter.getElementsByTag("a");
    }

}
