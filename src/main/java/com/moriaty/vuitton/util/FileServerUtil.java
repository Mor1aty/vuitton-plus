package com.moriaty.vuitton.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.moriaty.vuitton.constant.Constant;
import com.moriaty.vuitton.library.wrap.Wrapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 文件服务工具
 * </p>
 *
 * @author Moriaty
 * @since 2024/4/8 下午11:59
 */
@Slf4j
public class FileServerUtil {

    private FileServerUtil() {

    }

    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder()
            .connectTimeout(Duration.ofMinutes(5))
            .writeTimeout(Duration.ofMinutes(5))
            .readTimeout(Duration.ofMinutes(5))
            .callTimeout(Duration.ofMinutes(5))
            .build();

    public static File createTempFile(String prefix, String suffix) {
        try {
            return File.createTempFile("vuitton-plus-" + (prefix != null ? prefix : ""), suffix);
        } catch (IOException e) {
            log.error("创建临时文件异常", e);
            return null;
        }
    }

    public static String uploadFile(String fileServerUploadUrl, File file, String filename, String location)
            throws IOException {
        MultipartBody reqBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("location", location != null ? location : "")
                .addFormDataPart("file", filename,
                        RequestBody.create(file, MediaType.parse("application/octet-stream")))
                .build();
        Request req = new Request.Builder().url(fileServerUploadUrl).method("POST", reqBody).build();
        log.info("上传文件开始: {}[{}] 到 {} {}", filename, file.getName(), fileServerUploadUrl, location);
        try (Response response = HTTP_CLIENT.newCall(req).execute()) {
            if (response.body() == null) {
                log.error("请求失败, response 为空");
                return null;
            }
            String respBodyStr = response.body().string();
            log.info("上传文件完成: {}", respBodyStr);
            Wrapper<String> respBody = JSON.parseObject(respBodyStr, new TypeReference<>() {
            });
            if (Constant.Network.RESPONSE_SUCCESS_CODE != respBody.code()) {
                log.error("文件上传失败, {}", respBody.msg());
                return null;
            }
            return respBody.data();
        }
    }

    public static List<String> findFileFromFolder(String folderUrl) {
        try {
            Document doc = NovelUtil.findDoc(folderUrl);
            Elements domPre = doc.getElementsByTag("pre");
            if (domPre.isEmpty()) {
                log.error("获取文件失败");
                return Collections.emptyList();
            }
            Elements domA = domPre.getFirst().getElementsByTag("a");
            if (domA.isEmpty() || domA.size() == 1) {
                log.error("文件不存在");
                return Collections.emptyList();
            }
            List<String> fileList = new ArrayList<>();
            for (Element a : domA.subList(1, domA.size())) {
                fileList.add(a.text());
            }
            return fileList;
        } catch (IOException e) {
            log.error("获取文件异常", e);
            return Collections.emptyList();
        }
    }
}
