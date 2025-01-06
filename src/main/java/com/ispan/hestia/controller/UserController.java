package com.ispan.hestia.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ispan.hestia.dto.UserBasicInfoDTO;
import com.ispan.hestia.dto.UserChangePwdDTO;
import com.ispan.hestia.dto.UserLoginDTO;
import com.ispan.hestia.dto.UserRegistrationDTO;
import com.ispan.hestia.dto.UserResetPwdDTO;
import com.ispan.hestia.dto.UserUpdateInfoDTO;
import com.ispan.hestia.dto.VerificationDTO;
import com.ispan.hestia.exception.BadRequestException;
import com.ispan.hestia.exception.EntityNotFoundException;
import com.ispan.hestia.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

/**
 * 使用者接口
 * 
 * /user根路徑需要登入、/user/auth例外放行
 * 
 * 可擴充: (三方登入)、登錄日誌和安全記錄查詢 、(當有異常登錄或個人資料被修改時，向用戶發送通知或警告。)
 */
@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

	@Value("${frontend.base.url}")
	private String frontendBaseUrlConfig;
	@Autowired
	private UserService userService;

	/**
	 * 登入接口
	 * 
	 * @param UserLoginDTO
	 */
	@PostMapping("/auth/login")
	public ResponseEntity<String> login(@Valid @RequestBody UserLoginDTO dto, HttpServletResponse resp) {
		String token = userService.login(dto);
		String cookieValue = "authToken=" + token + "; HttpOnly; Path=/; Max-Age="
				+ (dto.isRememberMe() ? 60 * 60 * 24 * 30 : 60 * 60) + "; SameSite=Lax";
		resp.setHeader("Set-Cookie", cookieValue);
		return ResponseEntity.ok("登入成功");
	}

	/**
	 * 登出接口
	 */
	@PostMapping("/auth/logout")
	public ResponseEntity<?> logout(HttpServletResponse resp) {
		String expiredCookieValue = "authToken=; HttpOnly; Path=/; Max-Age=0; SameSite=Lax";
		resp.setHeader("Set-Cookie", expiredCookieValue);
		return ResponseEntity.ok("登出成功");
	}

	/**
	 * 註冊接口
	 * 
	 * @param UserRegistrationDTO
	 */
	@PostMapping("/auth/register")
	public ResponseEntity<String> register(@Valid @RequestBody UserRegistrationDTO dto) {
		if (!dto.getPassword().equals(dto.getConfirmPwd())) {
			throw new BadRequestException("密碼和確認密碼不一致");
		}
		userService.register(dto);
		return ResponseEntity.ok("註冊成功 !");
	}

	/**
	 * (信箱驗證)發送信箱驗證 Email
	 * 
	 * @param req
	 * @return
	 */
	@PostMapping("/verification-email")
	public ResponseEntity<String> sendVerifyCodeEmail(HttpServletRequest req) {
		Integer userId = (Integer) req.getAttribute("userId");
		userService.sendVerifyCodeEmail(userId);
		return ResponseEntity.ok("驗證信已發送，請在30分鐘內完成驗證 !");
	}

	/**
	 * (信箱驗證)驗證信箱驗證碼
	 * 
	 * @param verificationDTO
	 */
	@PostMapping("/verify")
	public ResponseEntity<String> verifyCode(@Valid @RequestBody VerificationDTO verificationDTO) {
		userService.verifyCode(verificationDTO);
		return ResponseEntity.ok("驗證成功！");
	}

	/**
	 * (信箱驗證)重發信箱驗證碼
	 * 
	 * @param email
	 */
	@PostMapping("/resend")
	public ResponseEntity<String> resendVerificationCode(@RequestParam String email) {
		userService.resendVerificationCode(email);
		return ResponseEntity.ok("新驗證碼已發送至您的 Email");
	}

	/**
	 * (重置密碼)重置密碼請求接口
	 * 
	 * @param email
	 */
	@PostMapping("/auth/reset-password")
	public ResponseEntity<String> resetPassword(@RequestParam String email) {
		userService.sendPasswordResetEmail(email);
		return ResponseEntity.ok("重置密碼 Email 已發送，請檢查您的信箱");
	}

	/**
	 * (重置密碼)接收重置密碼連結
	 * 
	 * @param token
	 */
	@GetMapping("/auth/reset-password/verify")
	public void verifyResetPasswordToken(@RequestParam String token, HttpServletResponse resp) {
		String frontendBaseUrl = frontendBaseUrlConfig;
		String redirectUrl;
		try {
			userService.verifyResetPasswordToken(token);
			redirectUrl = frontendBaseUrl + "/auth/resetPassword?token=" + token;
		} catch (EntityNotFoundException e) {
			String message = "無效的驗證碼，請重新申請重置密碼";
			redirectUrl = frontendBaseUrl + "/auth/resetPassword?error="
					+ URLEncoder.encode(message, StandardCharsets.UTF_8);
		} catch (BadRequestException e) {
			String message = "驗證連結已過期，請重新申請重置密碼";
			redirectUrl = frontendBaseUrl + "/auth/resetPassword/error="
					+ URLEncoder.encode(message, StandardCharsets.UTF_8);
		}
		try {
			resp.sendRedirect(redirectUrl);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * (重置密碼)處理重置密碼接口
	 * 
	 * @param UserResetPwdDTO
	 */
	@PutMapping("/auth/reset-password")
	public ResponseEntity<String> resetPassword(@Valid @RequestBody UserResetPwdDTO dto) {
		if (!dto.getNewPwd().equals(dto.getConfirmPwd())) {
			throw new BadRequestException("新密碼需與確認密碼一致");
		}
		userService.resetPassword(dto);
		return ResponseEntity.ok("密碼重置成功，請以新密碼登入");
	}

	/**
	 * user資料接口
	 * 
	 * @param
	 */
	@GetMapping("/info")
	public ResponseEntity<UserBasicInfoDTO> getUserInfo(HttpServletRequest req) {
		Integer userId = (Integer) req.getAttribute("userId");
		return ResponseEntity.ok(userService.getUserInfo(userId));
	}

	/**
	 * 修改user資料
	 * 
	 * @param birth、photo
	 */
	@PutMapping(value = "/info")
	public ResponseEntity<UserBasicInfoDTO> updateUserInfo(@Valid @RequestBody UserUpdateInfoDTO dto,
			HttpServletRequest req) {
		Integer userId = (Integer) req.getAttribute("userId");
		return ResponseEntity.ok(userService.updateUserInfo(userId, dto));
	}

	/**
	 * user上傳圖片(頭貼)
	 * 
	 * @param photo
	 * @param
	 * @return
	 */
	@PutMapping(value = "/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<UserBasicInfoDTO> updateUserPhoto(MultipartFile photo, HttpServletRequest req) {
		Integer userId = (Integer) req.getAttribute("userId");
		return ResponseEntity.ok(userService.updateUserPhoto(userId, photo));
	}

	/**
	 * 檢查信箱接口
	 * 
	 * @param email
	 */
	@GetMapping("/auth/check-email")
	public ResponseEntity<String> checkUserEmail(@RequestParam String email) {
		userService.checkUserEmail(email);
		return ResponseEntity.ok("該信箱可以註冊");
	}

	/**
	 * 修改密碼接口
	 * 
	 * @param dto
	 * @param UserChangePwdDTO
	 */
	@PutMapping("/password")
	public ResponseEntity<String> changeUserPwd(@Valid @RequestBody UserChangePwdDTO dto, HttpServletRequest req) {
		if (!dto.getNewPwd().equals(dto.getConfirmPwd())) {
			throw new BadRequestException("新密碼需與確認密碼一致");
		}
		Integer userId = (Integer) req.getAttribute("userId");
		userService.changeUserPwd(userId, dto);
		return ResponseEntity.ok("密碼修改成功");
	}

	/**
	 * 帳號註銷接口
	 * 
	 * @param
	 */
	@DeleteMapping("/terminate")
	public ResponseEntity<String> terminateUser(HttpServletRequest req) {
		Integer userId = (Integer) req.getAttribute("userId");
		userService.terminateUser(userId);
		return ResponseEntity.ok("帳號註銷成功");
	}
}
