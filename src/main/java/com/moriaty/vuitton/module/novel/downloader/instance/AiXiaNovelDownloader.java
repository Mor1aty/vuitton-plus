package com.moriaty.vuitton.module.novel.downloader.instance;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.moriaty.vuitton.bean.novel.network.NovelNetworkChapter;
import com.moriaty.vuitton.bean.novel.network.NovelNetworkContent;
import com.moriaty.vuitton.bean.novel.network.NovelNetworkInfo;
import com.moriaty.vuitton.module.novel.downloader.BaseNovelDownloader;
import com.moriaty.vuitton.module.novel.downloader.NovelDownloaderMeta;
import com.moriaty.vuitton.module.novel.downloader.dom.*;
import com.moriaty.vuitton.util.NovelUtil;
import com.moriaty.vuitton.util.TimeUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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
 * 爱下小说 Downloader
 * </p>
 *
 * @author Moriaty
 * @since 2024/5/16 下午2:20
 */
@Component
@Slf4j
public class AiXiaNovelDownloader extends BaseNovelDownloader {

    private final NovelDownloaderMeta meta = new NovelDownloaderMeta()
            .setWebName("爱下")
            .setMark("AiXia")
            .setWebsite("https://ixdzs8.com/read/")
            .setContentBaseUrl("https://ixdzs8.com/read/")
            .setApiBaseUrl("https://ixdzs8.com/novel/")
            .setDisable(true);

    @Resource
    private OkHttpClient httpClient;

    @Override
    public NovelDownloaderMeta getMeta() {
        return meta;
    }

    @Override
    public NovelNetworkInfo findInfo(String catalogueUrl) {
        try {
            Document doc = NovelUtil.findDocWithCharset(meta.getWebsite() + catalogueUrl);
            DomActionParam nameParam = new DomActionParam(doc, List.of(
                    new ClassDomAction("n-text"), new TagDomAction("h1")),
                    Function.identity());
            DomActionParam authorParam = new DomActionParam(doc, List.of(
                    new ClassDomAction("n-text"), new TagDomAction("p")),
                    author -> author.replace("作者:", "").trim());
            DomActionParam introParam = new DomActionParam(doc,
                    List.of(new IdDomAction("intro")),
                    Function.identity());
            DomActionParam imgParam = new DomActionParam(doc,
                    List.of(new ClassDomAction("n-img")),
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
        catalogueUrl = catalogueUrl.replace("/", "");
        FormBody reqBody = new FormBody.Builder().add("bid", catalogueUrl).build();
        Request req = new Request.Builder()
                .url(meta.getApiBaseUrl() + "clist/")
                .post(reqBody)
                .build();
        try (Response response = httpClient.newCall(req).execute()) {
            if (!response.isSuccessful()) {
                log.error("获取章节列表不成功, {}", response.code());
                return List.of();
            }
            if (response.body() == null) {
                log.error("获取章节列表内容为空, {}", response.code());
                return List.of();
            }
            JSONObject resp = JSONObject.parseObject(response.body().string());
            JSONArray respData = resp.getJSONArray("data");
            if (respData.isEmpty()) {
                log.error("获取章节列表数据为空, {}", resp);
            }
            List<NovelNetworkChapter> chapterList = new ArrayList<>();
            for (int i = 0; i < respData.size(); i++) {
                JSONObject data = respData.getJSONObject(i);
                chapterList.add(new NovelNetworkChapter()
                        .setIndex(i)
                        .setTitle(data.getString("title"))
                        .setContentUrl("/" + catalogueUrl + "/p" + data.get("ordernum") + ".html"));
            }
            return chapterList;
        } catch (IOException e) {
            log.error("获取章节列表异常", e);
            return List.of();
        }
    }

    @Override
    public NovelNetworkContent findContent(String title, String contentUrl) {
        try {
            TimeUtil.sleepRandomSecond(0, 2);

            Document doc = NovelUtil.findDocWithCharset(meta.getContentBaseUrl() + contentUrl);
            Elements domSections = doc.getElementsByTag("section");
            if (domSections.isEmpty()) {
                return new NovelNetworkContent().setErrorMsg("正文不存在");
            }
            Elements domSectionPs = domSections.getFirst().getElementsByTag("p");
            if (domSectionPs.isEmpty()) {
                return new NovelNetworkContent().setErrorMsg("本章未更新");
            }
            StringBuilder contentSb = new StringBuilder();
            StringBuilder contentHtmlSb = new StringBuilder();
            for (Element domSectionP : domSectionPs) {
                if (!domSectionP.hasClass("abg")) {
                    contentSb.append(domSectionP.text()).append("\n");
                    contentHtmlSb.append(domSectionP.html()).append("<br/>");
                }
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

}
