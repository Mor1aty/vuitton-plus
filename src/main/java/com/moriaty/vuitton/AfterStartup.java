package com.moriaty.vuitton;

import com.moriaty.vuitton.module.Module;
import com.moriaty.vuitton.module.ModuleFactory;
import com.moriaty.vuitton.module.novel.downloader.NovelDownloader;
import com.moriaty.vuitton.module.novel.downloader.NovelDownloaderFactory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 启动后执行
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/28 上午11:03
 */
@Component
@Slf4j
public class AfterStartup implements CommandLineRunner {

    @Resource
    private ApplicationContext applicationContext;

    @Value("${server.port:#{null}}")
    private String port;

    @Value("${server.deploy-ip}")
    private String deployIp;

    @Value("${file-server.url}")
    private String fsUrl;

    @Value("${file-server.upload-url}")
    private String fsUploadUrl;

    @Override
    public void run(String... args) {
        loadBaseInfo();
        loadNetworkSetting();
        loadModule();
        loadNovelDownloader();
    }

    private void loadBaseInfo() {
        String portStr;
        if (port == null) {
            portStr = ":8000";
        } else {
            portStr = "80".equals(port) ? "" : ":" + port;
        }
        String serverUrl = "http://" + deployIp + portStr;
        log.info("网站地址: http://127.0.0.1{} {}", portStr, serverUrl);
        ServerInfo.INFO.setFileServerUrl(fsUrl).setFileServerUploadUrl(fsUploadUrl);
    }

    private void loadNetworkSetting() {
        try {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                    // Do nothing
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                    // Do nothing
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            log.error("加载网络设置异常", e);
        }
    }

    private void loadModule() {
        List<Module> moduleList = ModuleFactory.getAllModule();
        if (moduleList.isEmpty()) {
            log.info("无可用模块");
        } else {
            StringBuilder sb = new StringBuilder();
            for (Module module : moduleList) {
                sb.append(module.getName()).append("(").append(module.getId()).append(") ");
            }
            log.info("可用的模块[{}]: {}", moduleList.size(), sb);
        }
    }

    private void loadNovelDownloader() {
        Map<String, NovelDownloader> novelDownloaderBeanMap = applicationContext.getBeansOfType(NovelDownloader.class);
        if (novelDownloaderBeanMap.isEmpty()) {
            log.info("无可用小说下载器");
        } else {
            Map<String, NovelDownloader> novelDownloaderMap = HashMap.newHashMap(novelDownloaderBeanMap.size());
            StringBuilder sb = new StringBuilder();
            for (NovelDownloader novelDownloaderBean : novelDownloaderBeanMap.values()) {
                if (Boolean.TRUE.equals(novelDownloaderBean.getMeta().getDisable())) {
                    continue;
                }
                novelDownloaderMap.put(novelDownloaderBean.getMeta().getMark(), novelDownloaderBean);
                sb.append(novelDownloaderBean.getMeta().getWebName())
                        .append("(").append(novelDownloaderBean.getMeta().getMark())
                        .append("[").append(novelDownloaderBean.getMeta().getWebsite()).append("]")
                        .append(") ");
            }
            NovelDownloaderFactory.putDownloaderMap(novelDownloaderMap);
            log.info("可用的小说下载器[{}]: {}", novelDownloaderMap.size(), sb);
        }
    }
}
