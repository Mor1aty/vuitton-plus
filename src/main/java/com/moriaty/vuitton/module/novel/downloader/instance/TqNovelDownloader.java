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
import com.moriaty.vuitton.util.TimeUtil;
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
 * 3Q 中文小说 Downloader
 * </p>
 *
 * @author Moriaty
 * @since 2024/5/15 下午3:54
 */
@Component
@Slf4j
public class TqNovelDownloader extends BaseNovelDownloader {

    private final NovelDownloaderMeta meta = new NovelDownloaderMeta()
            .setWebName("3Q 中文")
            .setMark("3Q")
            .setWebsite("https://sk.3qxsw.org/xiaoshuo/")
            .setContentBaseUrl("https://sk.3qxsw.org/");

    @Override
    public NovelDownloaderMeta getMeta() {
        return meta;
    }

    @Override
    public NovelNetworkInfo findInfo(String catalogueUrl) {
        try {
            Document doc = NovelUtil.findDocWithCharset(meta.getWebsite() + catalogueUrl);
            DomActionParam nameParam = new DomActionParam(doc, List.of(
                    new ClassDomAction("tab"), new ClassDomAction("p1")),
                    Function.identity());
            DomActionParam authorParam = new DomActionParam(doc, List.of(
                    new ClassDomAction("tab"), new ClassDomAction("p1", 1)),
                    author -> author.replace("作者：", "").trim());
            DomActionParam introParam = new DomActionParam(doc,
                    List.of(new ClassDomAction("jj"), new ClassDomAction("p2")),
                    intro -> "暂无");
            DomActionParam imgParam = new DomActionParam(doc,
                    List.of(new ClassDomAction("tu"), new TagDomAction("img")),
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
            String urlLast = "/";
            if (catalogueUrl.endsWith(urlLast)) {
                catalogueUrl = catalogueUrl.substring(0, catalogueUrl.length() - 1);
            }
            Document doc = NovelUtil.findDocWithCharset(meta.getWebsite() + catalogueUrl);
            Elements domIndexOption = findSelectOption(doc);
            List<NovelNetworkChapter> chapterList = new ArrayList<>();
            int index = 0;
            for (int i = 0; i < domIndexOption.size(); i++) {
                Elements domLis = findChapterDomLis(i, doc, meta.getWebsite() + catalogueUrl + "_" + (i + 1));
                if (domLis == null || domLis.isEmpty()) {
                    return List.of();
                }
                for (Element domLi : domLis) {
                    Elements domLiAs = domLi.getElementsByTag("a");
                    if (domLiAs.isEmpty()) {
                        log.warn("章节 {} 获取失败", domLi);
                    } else {
                        Element domLiA = domLiAs.getFirst();
                        chapterList.add(new NovelNetworkChapter()
                                .setIndex(index)
                                .setTitle(domLiA.text())
                                .setContentUrl(domLiA.attr("href")));
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

    private Elements findChapterDomLis(int index, Document doc, String url)
            throws IOException, URISyntaxException {
        if (index != 0) {
            doc = NovelUtil.findDocWithCharset(url);
        }
        Elements domUls = doc.getElementsByTag("ul");
        int needUrlNum = 2;
        if (domUls.isEmpty() || domUls.size() < needUrlNum) {
            return null;
        }
        Element domUl = domUls.get(1);
        return domUl.getElementsByTag("li");
    }

    @Override
    public NovelNetworkContent findContent(String title, String contentUrl) {
        try {
            TimeUtil.sleepRandomSecond(0, 2);

            Document doc = NovelUtil.findDocWithCharset(meta.getContentBaseUrl() + contentUrl);
            Element domContent = doc.getElementById("novelcontent");
            if (domContent == null) {
                return new NovelNetworkContent().setErrorMsg("正文不存在");
            }
            if (skipContent(domContent.text())) {
                return new NovelNetworkContent().setErrorMsg("本章未更新");
            }
            StringBuilder contentHtmlSb = new StringBuilder(domContent.html());
            StringBuilder contentSb = new StringBuilder(domContent.text());
            Elements domNextUrl = doc.getElementsByClass("p4");

            int nextIndex = 2;
            String nextPageStr = "下一页";
            while (!domNextUrl.isEmpty() && domNextUrl.text().contains(nextPageStr)) {
                doc = NovelUtil.findDocWithCharset(meta.getContentBaseUrl() + contentUrl.replace(".html",
                        "_" + nextIndex + ".html"));
                domContent = doc.getElementById("novelcontent");
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
                domNextUrl = doc.getElementsByClass("p4");
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
                .replace("本章未完", "")
                .replace("点击下一页继续阅读", "")
                .replace("本章已完", "")
                .replace("m.3qdu.com", "");
    }
}
