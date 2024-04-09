package com.moriaty.vuitton.module.novel.downloader.instance;

import com.moriaty.vuitton.bean.novel.network.NovelNetworkChapter;
import com.moriaty.vuitton.bean.novel.network.NovelNetworkContent;
import com.moriaty.vuitton.bean.novel.network.NovelNetworkInfo;
import com.moriaty.vuitton.constant.Constant;
import com.moriaty.vuitton.module.novel.downloader.NovelDownloader;
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
 * 2 笔趣小说 Downloader
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/28 下午6:02
 */
@Component
@Slf4j
public class TwoBiQuNovelDownloader extends NovelDownloader {

    private final NovelDownloaderMeta meta = new NovelDownloaderMeta()
            .setWebName("2笔趣")
            .setMark("2BiQu")
            .setWebsite("https://www.2biqu.com")
            .setContentBaseUrl("https://www.2biqu.com/")
            .setSearchBaseUrl("https://www.2biqu.com/modules/article/search.php");

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
            Elements domTop = doc.getElementsByClass("top");
            if (domTop.isEmpty()) {
                return null;
            }
            Elements domTopH1 = domTop.getFirst().getElementsByTag("h1");
            if (domTopH1.isEmpty()) {
                return null;
            }
            info.setName(domTopH1.getFirst().text());
            Elements domTopP = domTop.getFirst().getElementsByTag("p");
            if (domTopP.isEmpty()) {
                return null;
            }
            info.setAuthor(domTopP.getFirst().text()
                    .replace(" ", "")
                    .replace("作者：", ""));

            Elements domDesc = doc.getElementsByClass("desc xs-hidden");
            if (domDesc.isEmpty()) {
                return null;
            }
            info.setIntro(domDesc.getFirst().text());
            Elements domImg = doc.getElementsByTag("img");
            if (domImg.isEmpty()) {
                return null;
            }
            String imgSrc = domImg.attr("src");
            if (imgSrc.startsWith("http")) {
                info.setImgUrl(domImg.attr("src"));
            } else {
                info.setImgUrl(meta.getWebsite() + domImg.attr("src"));
            }
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
            Element domList = doc.getElementById("section-list");
            if (domList == null) {
                return chapterList;
            }
            Elements domLiList = domList.getElementsByTag("li");
            return exploreChapterList(domLiList, catalogueUrl);
        } catch (IOException e) {
            log.error("获取章节列表异常", e);
            return Collections.emptyList();
        }
    }

    @Override
    public NovelNetworkContent findContent(String title, String contentUrl) {
        try {
            Document doc = NovelUtil.findDoc(meta.getContentBaseUrl() + contentUrl);
            return exploreContent(title, doc, "content");
        } catch (IOException e) {
            return new NovelNetworkContent()
                    .setErrorMsg("获取小说内容发生异常, " + e.getLocalizedMessage());
        }
    }

    @Override
    public String removeAbnormalContent(String content) {
        return content.replace(" ", "\n").trim()
                .replace("www.2biqu.com", "")
                .replace("笔趣阁", "");
    }
}
