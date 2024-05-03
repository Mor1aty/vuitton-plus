package com.moriaty.vuitton.util;

import com.moriaty.vuitton.bean.novel.local.NovelChapterWithContent;
import com.moriaty.vuitton.bean.novel.network.NovelNetworkContent;
import com.moriaty.vuitton.constant.Constant;
import com.moriaty.vuitton.module.novel.downloader.BaseNovelDownloader;
import com.moriaty.vuitton.module.novel.downloader.NovelDownloaderFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * <p>
 * 小说工具
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/29 上午11:35
 */
@Slf4j
public class NovelUtil {

    private NovelUtil() {

    }

    public static BaseNovelDownloader findNovelDownloader(String downloaderMark) {
        BaseNovelDownloader downloader = NovelDownloaderFactory.getDownloader(downloaderMark);
        if (downloader == null) {
            log.error("小说下载器 {} 不存在", downloaderMark);
            return null;
        }
        return downloader;
    }

    public static boolean writeContentToFile(FileWriter fileWriter, int index,
                                             NovelNetworkContent content) throws IOException {
        String chapterName = "第" + index + "折 ";
        fileWriter.write(chapterName);
        boolean isNormal = false;
        if (content == null) {
            chapterName += "本章不存在";
            fileWriter.write(chapterName);
            fileWriter.write("\n\n");
        } else if (StringUtils.hasText(content.getErrorMsg())) {
            chapterName += "异常章节";
            fileWriter.write(chapterName);
            fileWriter.write("\n\n");
            fileWriter.write(content.getErrorMsg());
            fileWriter.write("\n\n");
        } else {
            chapterName += content.getTitle();
            fileWriter.write(chapterName);
            fileWriter.write("\n\n");
            fileWriter.write(content.getContent());
            fileWriter.write("\n\n");
            isNormal = true;
        }
        log.info("写入小说章节 {}", chapterName);
        fileWriter.flush();
        return isNormal;
    }

    public static File writeInfoToFile(String name, String author, String intro) throws IOException {
        String filename = name + "-" + UuidUtil.genId() + ".txt";
        File file = FileServerUtil.createTempFile(filename, null);
        if (file == null) {
            throw new IOException("临时文件创建失败");
        }
        try (FileWriter fileWriter = new FileWriter(file, true)) {
            fileWriter.write(name);
            fileWriter.write("\n\n");
            fileWriter.write("作者: " + author);
            fileWriter.write("\n\n");
            fileWriter.write("简介: " + intro);
            fileWriter.write("\n\n");
            log.info("写入小说 {} 信息, {}", name, file.getAbsolutePath());
            return file;
        }
    }

    public static File writeToFile(String name, String author, String intro,
                                   List<NovelChapterWithContent> chapterContentList) {
        try {
            File file = NovelUtil.writeInfoToFile(name, author, intro);
            try (FileWriter fileWriter = new FileWriter(file, true)) {
                for (NovelChapterWithContent chapterContent : chapterContentList) {
                    NovelUtil.writeContentToFile(fileWriter, chapterContent.getChapter().getIndex(),
                            new NovelNetworkContent()
                                    .setTitle(chapterContent.getChapter().getTitle())
                                    .setContent(chapterContent.getContent().getContent()));
                }
            }
            log.info("{} 写入文件完成", name);
            return file;
        } catch (IOException e) {
            log.error("{} 写入文件异常", name, e);
            return null;
        }
    }

    public static String upload(String fileServerUploadUrl, File file, String name) {
        try {
            String fileUrl = FileServerUtil.uploadFile(fileServerUploadUrl, file, name, "novel");
            if (fileUrl == null) {
                log.error("上传小说失败");
                return null;
            }
            return fileUrl;
        } catch (Exception e) {
            log.error("上传小说异常", e);
            return null;
        }
    }

    public static String uploadImg(String fileServerUploadUrl, String imgUrl, String novelName) {
        try {
            File imgFile = FileServerUtil.createTempFile(null, null);
            if (imgFile == null) {
                log.error("上传小说图片失败");
                return null;
            }
            FileUtils.copyURLToFile(URI.create(imgUrl).toURL(), imgFile);
            String suffix = "";
            String[] imgNameArray = imgUrl.split("\\.");
            if (imgNameArray.length > 1) {
                suffix = "." + imgNameArray[imgNameArray.length - 1];
            }
            String fileUrl = FileServerUtil.uploadFile(fileServerUploadUrl, imgFile,
                    novelName + "-img" + suffix, "novel");
            if (fileUrl == null) {
                log.error("上传小说图片失败");
                return null;
            }
            return fileUrl;
        } catch (Exception e) {
            log.error("上传小说图片异常", e);
            return null;
        }
    }

    public static Document findDoc(String url) throws IOException {
        return Jsoup.connect(cleanUrl(url))
                .timeout(Constant.Network.CONNECT_TIMEOUT)
                .headers(Constant.Network.CHROME_HEADERS)
                .ignoreHttpErrors(true)
                .get();
    }

    public static Document findDocWithCharset(String url, String... charset) throws IOException, URISyntaxException {
        URLConnection urlConnection = new URI(cleanUrl(url)).toURL().openConnection();
        urlConnection.setConnectTimeout(Constant.Network.CONNECT_TIMEOUT);
        return Jsoup.parse(urlConnection.getInputStream(),
                charset.length > 0 ? charset[0] : StandardCharsets.UTF_8.name(), url);
    }

    public static String cleanUrl(String url) {
        String[] split = url.split("//");
        StringBuilder sb = new StringBuilder();
        for (String s : split) {
            sb.append(s).append(s.startsWith("http") ? "//" : "/");
        }
        return sb.substring(0, sb.length() - 1);
    }
}
