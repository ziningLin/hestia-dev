package com.ispan.hestia.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.ispan.hestia.interceptors.JWTInterceptor;

@Configuration
public class AppConfig implements WebMvcConfigurer {

	@Autowired
	private JWTInterceptor jwtInterceptor;

	@Value("${cors.allowed.origins}") // 從設定檔讀取 CORS 允許的來源
	private String allowedOrigins;

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	SimpleMailMessage templateMessage() {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("zxz800123@gmail.com");
		return message;
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**") // 匹配所有的 API 路徑
						.allowedOrigins(allowedOrigins.split(",")) // 必須設置為具體的前端地址
						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允許的 HTTP 方法
						.allowCredentials(true) // 允許攜帶 Cookie
						.allowedHeaders("*") // 允許所有的 Header
						.exposedHeaders("Set-Cookie");
			}
		};
	}

	/**
	 * 攔截路徑等全部功能完成後再設置
	 */
	@Override
	public void addInterceptors(@NonNull InterceptorRegistry registry) {
		registry.addInterceptor(jwtInterceptor).addPathPatterns("/user/**")
				.addPathPatterns("/provider/**") // 攔截路徑
				.addPathPatterns("/order/comment/**") // 攔截路徑 新增評論，修改評論
				.addPathPatterns("/cart/**") // 攔截路徑 購物車相關
				.addPathPatterns("/favorite/**") // 攔截路徑 加入最愛相關
				.addPathPatterns("/chat/**")
				.addPathPatterns("/userOrders/**") // 攔截路徑 加入最愛相關
				.addPathPatterns("/providerOrders/**") // 攔截路徑 加入最愛相關
				.excludePathPatterns("/user/auth/**") // 例外路徑
				.excludePathPatterns("/order/comment/find") // 例外路徑 查評價
				.excludePathPatterns("/order/comment/avg/**") // 例外路徑 查總評分
				.excludePathPatterns("/cart/checkAvailableDateByRoomId/**")
		.excludePathPatterns("/userOrders/complete");// 例外路徑 查可用日期
	}

}

/**
 * 攔截路徑等全部功能完成後再設置
 */
// @Override
// public void addInterceptors(@NonNull InterceptorRegistry registry) {
// registry.addInterceptor(jwtInterceptor).addPathPatterns("/user/**").addPathPatterns("/provider/**")
// // 攔截路徑
// .excludePathPatterns("/user/auth/**"); // 例外路徑
// }
