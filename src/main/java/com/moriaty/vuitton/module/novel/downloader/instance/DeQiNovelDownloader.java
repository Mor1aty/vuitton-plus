package com.moriaty.vuitton.module.novel.downloader.instance;

import com.moriaty.vuitton.bean.novel.network.NovelNetworkChapter;
import com.moriaty.vuitton.bean.novel.network.NovelNetworkContent;
import com.moriaty.vuitton.bean.novel.network.NovelNetworkInfo;
import com.moriaty.vuitton.module.novel.downloader.BaseNovelDownloader;
import com.moriaty.vuitton.module.novel.downloader.NovelDownloaderMeta;
import com.moriaty.vuitton.module.novel.downloader.dom.ClassDomAction;
import com.moriaty.vuitton.module.novel.downloader.dom.DomActionParam;
import com.moriaty.vuitton.module.novel.downloader.dom.DomActor;
import com.moriaty.vuitton.module.novel.downloader.dom.TagDomAction;
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
 * 得奇小说 Downloader
 * </p>
 *
 * @author Moriaty
 * @since 2024/9/4 23:36
 */
@Component
@Slf4j
public class DeQiNovelDownloader extends BaseNovelDownloader {

    private final NovelDownloaderMeta meta = new NovelDownloaderMeta()
            .setWebName("得奇")
            .setMark("DeQi")
            .setWebsite("https://www.deqixs.com/xiaoshuo/")
            .setContentBaseUrl("https://www.deqixs.com/");

    @Override
    public NovelDownloaderMeta getMeta() {
        return meta;
    }

    @Override
    public NovelNetworkInfo findInfo(String catalogueUrl) {
        try {
            Document doc = NovelUtil.findDocWithCharset(meta.getWebsite() + catalogueUrl);
            DomActionParam nameParam = new DomActionParam(doc, List.of(
                    new ClassDomAction("itemtxt"), new TagDomAction("a")),
                    Function.identity());
            DomActionParam authorParam = new DomActionParam(doc, List.of(
                    new ClassDomAction("itemtxt"), new TagDomAction("p", 1)),
                    author -> author.replace("作者：", "").trim());
            DomActionParam introParam = new DomActionParam(doc, List.of(new ClassDomAction("des bb")),
                    Function.identity());
            DomActionParam imgParam = new DomActionParam(doc,
                    List.of(new ClassDomAction("item"), new TagDomAction("img")),
                    img -> "https:" + img);
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
            List<NovelNetworkChapter> chapterList = new ArrayList<>();
            Element domList = doc.getElementById("list");
            if (domList == null) {
                return Collections.emptyList();
            }
            Elements domLiList = domList.getElementsByTag("li");
            if (domLiList.isEmpty()) {
                return Collections.emptyList();
            }
            for (int i = 0; i < domLiList.size(); i++) {
                Element domLi = domLiList.get(i);
                Elements domLiA = domLi.getElementsByTag("a");
                if (domLiA.isEmpty()) {
                    log.warn("章节 {} 获取失败", domLiA);
                } else {
                    chapterList.add(new NovelNetworkChapter()
                            .setIndex(i)
                            .setTitle(domLiA.text())
                            .setContentUrl(domLiA.attr("href")));
                }
            }
            return chapterList;
        } catch (URISyntaxException | IOException e) {
            log.error("获取章节列表异常", e);
            return Collections.emptyList();
        }
    }

    @Override
    public NovelNetworkContent findContent(String title, String contentUrl) {
        try {
            Document doc = NovelUtil.findDocWithCharset(meta.getContentBaseUrl() + contentUrl);
            Elements domContentList = doc.getElementsByClass("con");
            if (domContentList.isEmpty()) {
                return new NovelNetworkContent()
                        .setErrorMsg("正文不存在");
            }
            Element domContent = domContentList.getFirst();
            if (skipContent(domContent.text())) {
                return new NovelNetworkContent()
                        .setErrorMsg("本章未更新");
            }
            StringBuilder contentSb = new StringBuilder(domContent.text());
            StringBuilder contentHtmlSb = new StringBuilder(domContent.html());

            String nextStr = findContentNext(doc);
            int nextIndex = 2;
            String nextPageStr = "下一页";
            while (nextStr != null && nextStr.contains(nextPageStr)) {
                doc = NovelUtil.findDocWithCharset(meta.getContentBaseUrl() + contentUrl.replace(".html",
                        "-" + nextIndex + ".html"));
                domContentList = doc.getElementsByClass("con");
                if (domContentList.isEmpty()) {
                    return new NovelNetworkContent()
                            .setErrorMsg("正文不存在");
                }
                domContent = domContentList.getFirst();
                if (skipContent(domContent.text())) {
                    return new NovelNetworkContent()
                            .setErrorMsg("本章未更新");
                }
                contentSb.append(domContent.text());
                contentHtmlSb.append(domContent.html());
                nextStr = findContentNext(doc);
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
        return super.removeAbnormalContent(content)
                .replace("为防止采集和被举报，请使用手机浏览器打开本站继续阅读。谢谢您的支持！", "")
                .replace("网址：deqixs.com", "")
                .replace("必应搜索：得奇小说网", "")
                .trim();
    }

    private String findContentNext(Document doc) {
        Elements domDivList = doc.getElementsByClass("prenext");
        if (domDivList.isEmpty()) {
            return null;
        }
        Element domDiv = domDivList.getFirst();
        Elements domDivSpanList = domDiv.getElementsByTag("span");
        if (domDivSpanList.size() != 2) {
            return null;
        }
        Element domDivSpanNext = domDivSpanList.get(1);
        return domDivSpanNext.text();
    }
}
