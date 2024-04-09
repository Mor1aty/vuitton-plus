package com.moriaty.vuitton.module.novel.downloader.instance;

import com.moriaty.vuitton.bean.novel.network.NovelNetworkChapter;
import com.moriaty.vuitton.bean.novel.network.NovelNetworkContent;
import com.moriaty.vuitton.bean.novel.network.NovelNetworkInfo;
import com.moriaty.vuitton.bean.novel.network.resolve.DocResolveAction;
import com.moriaty.vuitton.bean.novel.network.resolve.DocResolveExecAction;
import com.moriaty.vuitton.module.novel.downloader.NovelDownloader;
import com.moriaty.vuitton.module.novel.downloader.NovelDownloaderMeta;
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
public class BiCuiNovelDownloader extends NovelDownloader {

    private final NovelDownloaderMeta meta = new NovelDownloaderMeta()
            .setWebName("笔翠")
            .setMark("BiCui")
            .setWebsite("https://www.bicuix.com/")
            .setContentBaseUrl("https://www.bicuix.com/")
            .setCharset("GBK");

    @Override
    public NovelDownloaderMeta getMeta() {
        return meta;
    }

    @Override
    public NovelNetworkInfo findInfo(String catalogueUrl) {
        try {
            Document doc = NovelUtil.findDocWithCharset(meta.getWebsite() + catalogueUrl, meta.getCharset());
            NovelNetworkInfo info = NovelUtil.findNovelInfoFromDoc(doc,
                    new DocResolveExecAction(List.of(
                            new DocResolveAction("info", null),
                            new DocResolveAction(null, "h1")),
                            Element::text),
                    new DocResolveExecAction(List.of(
                            new DocResolveAction("info", null),
                            new DocResolveAction(null, "p")),
                            dom -> dom.text().replace(" ", "")
                                    .replace("作    者：", "")),
                    new DocResolveExecAction(List.of(
                            new DocResolveAction("intro", null),
                            new DocResolveAction(null, "p")),
                            Element::text),
                    new DocResolveExecAction(List.of(), dom -> "")
            );
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
            Elements domDdList = domList.getElementsByTag("dd");
            if (domDdList.isEmpty()) {
                return chapterList;
            }
            if (domDdList.size() > 9) {
                domDdList = new Elements(domDdList.subList(9, domDdList.size()));
            }
            return exploreChapterList(domDdList, "");
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

    @Override
    public String removeAbnormalContent(String content) {
        return "";
    }
}
