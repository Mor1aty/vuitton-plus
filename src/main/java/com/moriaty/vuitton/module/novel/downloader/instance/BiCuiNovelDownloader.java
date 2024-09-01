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
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * <p>
 * 笔翠小说 Downloader
 * </p>
 *
 * @author Moriaty
 * @since 2024/4/10 上午2:14
 */
@Component
@Slf4j
public class BiCuiNovelDownloader extends BaseNovelDownloader {

    private final NovelDownloaderMeta meta = new NovelDownloaderMeta()
            .setWebName("笔翠")
            .setMark("BiCui")
            .setWebsite("https://www.bicui6.com/")
            .setContentBaseUrl("https://www.bicui6.com/")
            .setCharset("GBK");

    @Override
    public NovelDownloaderMeta getMeta() {
        return meta;
    }

    @Override
    public NovelNetworkInfo findInfo(String catalogueUrl) {
        try {
            Document doc = NovelUtil.findDocWithCharset(meta.getWebsite() + catalogueUrl, meta.getCharset());
            DomActionParam nameParam = new DomActionParam(doc, List.of(
                    new IdDomAction("info"), new TagDomAction("h1")),
                    Function.identity());
            DomActionParam authorParam = new DomActionParam(doc, List.of(
                    new IdDomAction("info"), new TagDomAction("p")),
                    author -> author.replace("作    者：", "").trim());
            DomActionParam introParam = new DomActionParam(doc,
                    List.of(new IdDomAction("intro"), new TagDomAction("p")),
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
            List<NovelNetworkChapter> chapterList = new ArrayList<>();
            Document doc = NovelUtil.findDocWithCharset(meta.getWebsite() + catalogueUrl, meta.getCharset());
            Element domList = doc.getElementById("list");
            if (domList == null) {
                return chapterList;
            }
            Elements domListDls = domList.getElementsByTag("dl");
            if (domListDls.isEmpty()) {
                return chapterList;
            }
            List<Element> domDdList = new ArrayList<>();
            Element domListDl = domListDls.getFirst();
            Elements domListDlChildren = domListDl.children();
            int skipDtNum = 2;
            int skipDt = 0;
            for (Element domListDlChild : domListDlChildren) {
                if ("dt".equals(domListDlChild.tagName())) {
                    skipDt++;
                    continue;
                }
                if (skipDt >= skipDtNum && "dd".equals(domListDlChild.tagName())) {
                    domDdList.add(domListDlChild);
                }
            }
            return exploreChapterList(new Elements(domDdList), "");
        } catch (URISyntaxException | IOException e) {
            log.error("获取章节列表异常", e);
            return Collections.emptyList();
        }
    }

    @Override
    public NovelNetworkContent findContent(String title, String contentUrl) {
        try {
            Document doc = NovelUtil.findDocWithCharset(meta.getContentBaseUrl() + contentUrl, meta.getCharset());
            return exploreContent(title, doc, "content");
        } catch (URISyntaxException | IOException e) {
            return new NovelNetworkContent()
                    .setErrorMsg("获取小说内容发生异常, " + e.getLocalizedMessage());
        }
    }
}
