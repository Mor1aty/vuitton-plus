package com.moriaty.vuitton;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 服务信息
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/28 上午11:04
 */
public class ServerInfo {

    private ServerInfo() {

    }

    public static final Info INFO = new Info();

    @Data
    @Accessors(chain = true)
    public static class Info {

        private String fileServerUrl;

        private String fileServerUploadUrl;
    }
}
