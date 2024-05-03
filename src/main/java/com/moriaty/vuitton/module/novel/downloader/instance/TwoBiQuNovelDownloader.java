package com.moriaty.vuitton.module.novel.downloader.instance;

import com.moriaty.vuitton.bean.novel.network.NovelNetworkChapter;
import com.moriaty.vuitton.bean.novel.network.NovelNetworkContent;
import com.moriaty.vuitton.bean.novel.network.NovelNetworkInfo;
import com.moriaty.vuitton.module.novel.downloader.BaseNovelDownloader;
import com.moriaty.vuitton.module.novel.downloader.NovelDownloaderMeta;
import com.moriaty.vuitton.module.novel.downloader.dom.*;
import com.moriaty.vuitton.util.NovelUtil;
import com.moriaty.vuitton.util.TimeUtil;
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
 * 2 笔趣小说 Downloader
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/28 下午6:02
 */
@Component
@Slf4j
public class TwoBiQuNovelDownloader extends BaseNovelDownloader {

    private final NovelDownloaderMeta meta = new NovelDownloaderMeta()
            .setWebName("2笔趣")
            .setMark("2BiQu")
            .setWebsite("https://www.22biqu.com/")
            .setContentBaseUrl("https://www.22biqu.com/");

    @Override
    public NovelDownloaderMeta getMeta() {
        return meta;
    }

    @Override
    public NovelNetworkInfo findInfo(String catalogueUrl) {
        try {
            Document doc = NovelUtil.findDocWithCharset(meta.getWebsite() + catalogueUrl);
            DomActionParam nameParam = new DomActionParam(doc, List.of(
                    new ClassDomAction("top"), new TagDomAction("h1")),
                    Function.identity());
            DomActionParam authorParam = new DomActionParam(doc, List.of(
                    new ClassDomAction("fix"), new TagDomAction("p")),
                    author -> author.replace("作 者：", "").trim());
            DomActionParam introParam = new DomActionParam(doc,
                    List.of(new ClassDomAction("desc xs-hidden")),
                    Function.identity());
            DomActionParam imgParam = new DomActionParam(doc,
                    List.of(new TagDomAction("img")),
                    Function.identity());
            NovelNetworkInfo info = DomActor.findInfo(nameParam, authorParam, introParam, imgParam);
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
            Element domIndex = doc.getElementById("indexselect");
            if (domIndex == null) {
                return Collections.emptyList();
            }
            Elements domIndexOption = domIndex.getElementsByTag("option");
            List<NovelNetworkChapter> chapterList = new ArrayList<>();
            int index = 0;
            for (int i = 0; i < domIndexOption.size(); i++) {
                Elements domUls = findChapterDomUls(i, doc,
                        meta.getWebsite() + catalogueUrl + "/" + (i + 1));
                if (domUls.size() < 2) {
                    return Collections.emptyList();
                }
                Element domUl1 = domUls.get(1);
                Elements domUl1Lis = domUl1.getElementsByTag("li");
                if (domUl1Lis.isEmpty()) {
                    return Collections.emptyList();
                }
                for (Element domUl1Li : domUl1Lis) {
                    Elements domUl1LiA = domUl1Li.getElementsByTag("a");
                    if (domUl1LiA.isEmpty()) {
                        log.warn("章节 {} 获取失败", domUl1Li);
                    } else {
                        chapterList.add(new NovelNetworkChapter()
                                .setIndex(index)
                                .setTitle(domUl1LiA.text())
                                .setContentUrl(domUl1LiA.attr("href")));
                    }
                    index++;
                }
            }
            return chapterList;
        } catch (URISyntaxException | IOException e) {
            log.error("获取章节列表异常", e);
            return Collections.emptyList();
        }
    }

    private Elements findChapterDomUls(int index, Document doc, String url)
            throws IOException, URISyntaxException {
        return index == 0 ? doc.getElementsByClass("section-list fix")
                : NovelUtil.findDocWithCharset(url).getElementsByClass("section-list fix");
    }

    @Override
    public NovelNetworkContent findContent(String title, String contentUrl) {
        try {
            TimeUtil.sleepRandomSecond(0, 2);

            Document doc = NovelUtil.findDocWithCharset(meta.getContentBaseUrl() + contentUrl);
            Element domContent = doc.getElementById("content");
            if (domContent == null) {
                return new NovelNetworkContent()
                        .setErrorMsg("正文不存在");
            }
            if (skipContent(domContent.text())) {
                return new NovelNetworkContent()
                        .setErrorMsg("本章未更新");
            }
            StringBuilder contentSb = new StringBuilder(domContent.text());
            StringBuilder contentHtmlSb = new StringBuilder(domContent.html());

            Element domNextUrl = doc.getElementById("next_url");
            int nextIndex = 2;
            String nextPageStr = "下一页";
            while (domNextUrl != null && domNextUrl.text().contains(nextPageStr)) {
                doc = NovelUtil.findDocWithCharset(meta.getContentBaseUrl() + contentUrl.replace(".html",
                        "_" + nextIndex + ".html"));
                domContent = doc.getElementById("content");
                if (domContent == null) {
                    return new NovelNetworkContent()
                            .setErrorMsg("正文不存在");
                }
                if (skipContent(domContent.text())) {
                    return new NovelNetworkContent()
                            .setErrorMsg("本章未更新");
                }
                contentSb.append(domContent.text());
                contentHtmlSb.append(domContent.html());
                domNextUrl = doc.getElementById("next_url");
                nextIndex++;
            }

            return new NovelNetworkContent()
                    .setTitle(title)
                    .setContent(removeAbnormalContent(contentSb.toString()))
                    .setContentHtml(removeAbnormalContent(contentHtmlSb.toString()));
        } catch (URISyntaxException | IOException e) {
            return new NovelNetworkContent()
                    .setErrorMsg("获取小说内容发生异常, " + e.getLocalizedMessage());
        }
    }


    @Override
    public String removeAbnormalContent(String content) {
        return content.replace(" ", "\n").trim();
    }
}
