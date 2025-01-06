package com.ispan.hestia.ws.config;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ispan.hestia.exception.WebSocketException;
import com.ispan.hestia.util.JWTUtil;
import com.ispan.hestia.ws.util.SpringContextHolder;
import com.nimbusds.jwt.JWTClaimsSet;

import jakarta.servlet.http.HttpSession;
import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;

@Component
public class GetHttpSessionConfig extends ServerEndpointConfig.Configurator{

	private JWTUtil jwtUtil;
	
	/**
	 * 修改 WebSocket 握手過程以獲取 HttpSession。
	 * 握手過程中被呼叫，將 HttpSession 與 WebSocket 建立關聯。
	 */
	@Override
	public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        if (jwtUtil == null) {
            jwtUtil = SpringContextHolder.getBean(JWTUtil.class);
        }
		
		// 獲取 HttpSession
        HttpSession httpSession = (HttpSession) request.getHttpSession();
        if (httpSession != null) {
            sec.getUserProperties().put(HttpSession.class.getName(), httpSession);
        }
        
        // 從 HandshakeRequest 的 headers 中提取 Cookie 並驗證 JWT Token
        Map<String, List<String>> headers = request.getHeaders();
        List<String> cookieHeaders = headers.get("cookie");
        String token = null;

        if (cookieHeaders != null) {
            for (String cookieHeader : cookieHeaders) {
                String[] cookies = cookieHeader.split(";");
                for (String cookie : cookies) {
                    String trimmedCookie = cookie.trim();
                    if (trimmedCookie.startsWith("authToken=")) {
                        token = trimmedCookie.substring("authToken=".length());
                        break;
                    }
                }
                if (token != null) {
                    break;
                }
            }
        }
        if (token != null) {
            try {
                JWTClaimsSet data = jwtUtil.validateAndParseToken(token);
                Integer userId = jwtUtil.getUserIdFromToken(data);
                sec.getUserProperties().put("userId", userId);
            } catch (Exception e) {
                throw new WebSocketException("JWT 驗證失敗：" + e.getMessage());
            }
        } else {
            throw new WebSocketException("缺少 authToken Cookie 或 Cookie 無效");
        }
        
        response.getHeaders().put("Access-Control-Allow-Origin", List.of("http://localhost:5173"));
        response.getHeaders().put("Access-Control-Allow-Credentials", List.of("true"));
    }
}
