package com.moriaty.vuitton.module.novel.downloader.instance;

import com.moriaty.vuitton.bean.novel.network.NovelNetworkContent;
import com.moriaty.vuitton.bean.novel.network.NovelNetworkChapter;
import com.moriaty.vuitton.bean.novel.network.NovelNetworkInfo;
import com.moriaty.vuitton.constant.Constant;
import com.moriaty.vuitton.module.novel.downloader.BaseNovelDownloader;
import com.moriaty.vuitton.module.novel.downloader.NovelDownloaderMeta;
import com.moriaty.vuitton.util.NovelUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 笔趣阁小说 Downloader
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/28 下午12:37
 */
@Component
@Slf4j
public class BiQuGeNovelDownloader extends BaseNovelDownloader {

    private final NovelDownloaderMeta meta = new NovelDownloaderMeta()
            .setWebName("笔奇部")
            .setMark("BiQuGe")
            .setWebsite("https://www.biqubu1.com")
            .setContentBaseUrl("https://www.biqubu1.com/")
            .setSearchBaseUrl("https://www.1q1m.com/s?q=");

    @Override
    public NovelDownloaderMeta getMeta() {
        return meta;
    }

    @Override
    public NovelNetworkInfo findInfo(String catalogueUrl) {
        try {
            NovelNetworkInfo info = new NovelNetworkInfo()
                    .setDownloaderMark(meta.getMark())
                    .setDownloaderCatalogueUrl(catalogueUrl);
            Document doc = Jsoup.connect(meta.getWebsite() + catalogueUrl)
                    .timeout(Constant.Network.CONNECT_TIMEOUT)
                    .headers(Constant.Network.CHROME_HEADERS)
                    .get();
            Element domInfo = doc.getElementById("info");
            if (domInfo == null) {
                return null;
            }
            Elements domInfoH1 = domInfo.getElementsByTag("h1");
            if (domInfoH1.isEmpty()) {
                return null;
            }
            info.setName(domInfoH1.getFirst().text());
            Elements domInfoP = domInfo.getElementsByTag("p");
            if (domInfoP.isEmpty()) {
                return null;
            }
            info.setAuthor(domInfoP.getFirst().text()
                    .replace(" ", "")
                    .replace("作者：", ""));
            Element domIntro = doc.getElementById("intro");
            if (domIntro == null) {
                return null;
            }
            Elements domIntroP = domIntro.getElementsByTag("p");
            if (domIntroP.isEmpty()) {
                return null;
            }
            info.setIntro(domIntroP.getFirst().text());
            Element domImg = doc.getElementById("fmimg");
            if (domImg == null) {
                return null;
            }
            Elements domImgI = domImg.getElementsByTag("img");
            if (domImgI.isEmpty()) {
                return null;
            }
            info.setImgUrl(meta.getWebsite() + "/" + domImgI.attr("src"));
            return info;
        } catch (IOException e) {
            log.error("获取信息异常", e);
            return null;
        }
    }

    @Override
    public List<NovelNetworkChapter> findChapterList(String catalogueUrl) {
        try {
            List<NovelNetworkChapter> chapterList = new ArrayList<>();
            Document doc = Jsoup.connect(meta.getWebsite() + catalogueUrl)
                    .timeout(Constant.Network.CONNECT_TIMEOUT)
                    .headers(Constant.Network.CHROME_HEADERS)
                    .get();
            Element domList = doc.getElementById("list");
            if (domList == null) {
                return chapterList;
            }
            Elements domDdList = domList.getElementsByTag("dd");
            return exploreChapterList(domDdList, "");
        } catch (IOException e) {
            log.error("获取章节列表异常", e);
            return Collections.emptyList();
        }
    }

    @Override
    public NovelNetworkContent findContent(String title, String contentUrl) {
        try {
            Document doc = NovelUtil.findDoc(meta.getContentBaseUrl() + contentUrl);
            return exploreContent(title, doc, "content1");
        } catch (IOException e) {
            return new NovelNetworkContent()
                    .setErrorMsg("获取小说内容发生异常, " + e.getLocalizedMessage());
        }
    }

    @Override
    public String removeAbnormalContent(String content) {
        return content.substring(0, content.lastIndexOf("网页版章节内容慢，请下载爱阅小说app阅读最新内容")).trim();
    }

}
