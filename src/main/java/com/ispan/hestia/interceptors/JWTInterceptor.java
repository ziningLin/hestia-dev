package com.ispan.hestia.interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.ispan.hestia.exception.JWTException;
import com.ispan.hestia.util.JWTUtil;
import com.nimbusds.jwt.JWTClaimsSet;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTInterceptor implements HandlerInterceptor {

	@Autowired
	private JWTUtil jwtUtil;

	/**
	 * 攔截驗證 JWT Token
	 */
	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
		if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
			return true;
		}

		Cookie[] cookies = req.getCookies();
		String token = null;
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("authToken".equals(cookie.getName())) {
					token = cookie.getValue();
					break;
				}
			}
		}
		if (token == null) {
			throw new JWTException("Missing or invalid authToken cookie.");
		}
		JWTClaimsSet data = jwtUtil.validateAndParseToken(token);
		Integer userId = jwtUtil.getUserIdFromToken(data);
		Boolean isProvider = jwtUtil.getIsProviderFromToken(data);
		req.setAttribute("userId", userId);
		req.setAttribute("isProvider", isProvider);
		return true;
	}

}
