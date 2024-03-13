package com.moriaty.vuitton.ctrl.ws;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 视频播放 WS
 * </p>
 *
 * @author Moriaty
 * @since 2024/2/8 下午12:24
 */
@Component
@ServerEndpoint("ws/video-play")
@Slf4j
public class VideoPlayWs {

    @OnOpen
    public void onOpen(Session session) {

    }

    @OnClose
    public void onClose(Session session) {

    }

    @OnMessage
    public void onMessage(String message, Session session) {

    }

    @OnError
    public void onError(Session session, Throwable error) {

    }
}
