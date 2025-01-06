package com.ispan.hestia.ws.endpoint;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.ispan.hestia.exception.WebSocketException;
import com.ispan.hestia.ws.config.GetHttpSessionConfig;

import jakarta.websocket.EndpointConfig;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@Component
@ServerEndpoint(value = "/ws/chat", configurator = GetHttpSessionConfig.class)
public class ChatEndpoint {

	public static final ConcurrentHashMap<Integer, Session> SESSIONS = new ConcurrentHashMap<>();
	
	@OnOpen
    public void onOpen(Session session, EndpointConfig config) {
		Integer userId = (Integer) session.getUserProperties().get("userId");
	    if (userId == null) {
	        throw new WebSocketException("未能獲取用戶身份，WebSocket 連接被拒絕。");
	    }
	    SESSIONS.put(userId, session);
	}
	
	@OnClose
    public void onClose(Session session) {
        Integer userId = getUserIdBySession(session);
        if (userId != null) {
            SESSIONS.remove(userId);
            System.out.println("WebSocket 連線已關閉，使用者ID: " + userId);
        }
    }
	
	@OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("WebSocket 錯誤: " + throwable.getMessage());
    }
	
	@OnMessage
    public void onMessage(String message, Session session) {

        System.out.println("收到消息: " + message);
    }
	
	/**
     * 從所有 Session 中找到對應的 userId
     * @param session 目標 WebSocket Session
     * @return 對應的 userId，若找不到則返回 null
     */
    private Integer getUserIdBySession(Session session) {
        return SESSIONS.entrySet().stream()
                .filter(entry -> entry.getValue().equals(session))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    /**
     * 靜態方法，用於向指定 userId 的用戶推送消息
     * @param userId 目標用戶 ID
     * @param message 推送的消息
     */
    public static void sendMessageToUser(Integer userId, String message) {
        Session session = SESSIONS.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                System.err.println("向用戶 ID " + userId + " 發送消息失敗: " + e.getMessage());
            }
        }
    }
}
