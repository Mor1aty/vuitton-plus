package com.moriaty.vuitton.constant;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * <p>
 * 常量
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/28 上午11:07
 */
public class Constant {

    private Constant() {

    }

    public static final String SERVER_NAME = "vuitton-plus";

    public static class Date {

        private Date() {

        }

        public static final DateTimeFormatter FORMAT_ID = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSS");

        public static final DateTimeFormatter FORMAT_RECORD_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    public static class Network {

        private Network() {

        }

        public static final int CONNECT_TIMEOUT = 60 * 1000;

        public static final Map<String, String> CHROME_HEADERS = Map.of(
                "user-agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) "
                              + "Chrome/120.0.0.0 Safari/537.36",
                "Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,"
                          + "image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
                "Accept-Encoding", "gzip, deflate, br",
                "Accept-Language", "zh-CN,zh;q=0.9",
                "Connection", "keep-alive",
                "Cookie", "fontFamily=null; fontColor=null; fontSize=null; bg=null; ",
                "sec-ch-ua", "\"Not_A Brand\";v=\"8\", \"Chromium\";v=\"120\", \"Google Chrome\";v=\"120\"",
                "sec-ch-ua-mobile", "?0",
                "sec-ch-ua-platform", "\"Linux\""
        );
        public static final int RESPONSE_SUCCESS_CODE = 200;
    }

    public static class Setting {

        private Setting() {

        }

        public static class GroupVideoPlayer {

            private GroupVideoPlayer() {

            }

            public static final int GROUP = 1;

            public static final String SKIP_OP_ED = "skip_op_ed";

            public static final String AUTO_PLAY_NEXT = "auto_play_next";

        }

    }

    public static class Video {

        private Video() {

        }

        public static final String REDIS_PREFIX_PLAY_HISTORY = SERVER_NAME + ":play-history:";

        public static final Duration REDIS_TTL_PLAY_HISTORY = Duration.ofDays(1L);

    }

}
