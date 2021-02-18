package io.github.jasonkayzk.playersyncbackend.controller;

import com.google.gson.Gson;
import io.github.jasonkayzk.playersyncbackend.consts.PlayState;
import io.github.jasonkayzk.playersyncbackend.entity.PlayInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@ServerEndpoint("/room/{id}")
public class RoomController {

    private static final Map<String, Set<Session>> map = new ConcurrentHashMap<>();

    private static final Map<Session, String> sessionAndRoomIdMap = new ConcurrentHashMap<>();

    private static final Gson gson = new Gson();

    @OnOpen
    public void onOpen(Session session, @PathParam("id") String id) {
        if (map.containsKey(id)) {
            map.get(id).add(session);
        } else {
            Set<Session> sessionSet = new HashSet<>();
            sessionSet.add(session);
            map.put(id, sessionSet);
        }
        sessionAndRoomIdMap.put(session, id);
        try {
            sendMessage(PlayInfo.builder().type(PlayState.FIRST).message("已连接").build(), session);
        } catch (IOException e) {
            log.error("消息发送失败！", e);
        }
    }

    private void sendMessage(PlayInfo info, Session session) throws IOException {
        session.getBasicRemote().sendText(gson.toJson(info));
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        log.info(message);
        if (StringUtils.isEmpty(message) || !sessionAndRoomIdMap.containsKey(session)) {
            return;
        }
        String roomId = sessionAndRoomIdMap.get(session);
        if (!map.containsKey(roomId)) {
            return;
        }
        Set<Session> sessionSet = map.get(roomId);
        PlayInfo messageDTO = gson.fromJson(message, PlayInfo.class);
        for (Session tempSession : sessionSet) {
            if (session.equals(tempSession)) {
                continue;
            }
            try {
                sendMessage(messageDTO, tempSession);
            } catch (IOException e) {
                log.error("消息发送失败！messageDataDTO:{}, tempSession:{}", messageDTO, tempSession, e);
            }
        }
    }

    @OnClose
    public void onClose(Session session) {
        String roomId = sessionAndRoomIdMap.get(session);
        sessionAndRoomIdMap.remove(session);
        if (null != roomId && map.containsKey(roomId)) {
            map.get(roomId).remove(session);
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        String roomId = sessionAndRoomIdMap.get(session);
        sessionAndRoomIdMap.remove(session);
        if (null != roomId && map.containsKey(roomId)) {
            map.get(roomId).remove(session);
        }
        log.error("", error);
    }

}
