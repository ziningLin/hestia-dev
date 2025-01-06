package com.ispan.hestia.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ispan.hestia.dto.UserBasicInfoDTO;
import com.ispan.hestia.dto.UserChangePwdDTO;
import com.ispan.hestia.dto.UserLoginDTO;
import com.ispan.hestia.dto.UserRegistrationDTO;
import com.ispan.hestia.dto.UserResetPwdDTO;
import com.ispan.hestia.dto.UserUpdateInfoDTO;
import com.ispan.hestia.dto.VerificationDTO;

@Service
public interface UserService {

	// 登入
	String login(UserLoginDTO user);

	// 發送信箱驗證碼
	void sendVerifyCodeEmail(Integer userId);

	// 驗證驗證碼
	void verifyCode(VerificationDTO verificationDTO);

	// 重發驗證碼 email
	void resendVerificationCode(String email);

	// 發送重置密碼 email
	void sendPasswordResetEmail(String email);

	// 接收重置密碼連結
	void verifyResetPasswordToken(String token);

	// 重置密碼
	void resetPassword(UserResetPwdDTO dto);

	// 註冊
	void register(UserRegistrationDTO userRegistrationDTO);

	// 獲得 user 資料
	UserBasicInfoDTO getUserInfo(Integer userId);

	// 修改 user 資料
	UserBasicInfoDTO updateUserInfo(Integer userId, UserUpdateInfoDTO dto);

	// 修改 user 頭貼
	UserBasicInfoDTO updateUserPhoto(Integer userId, MultipartFile photo);

	// 檢查信箱是否可用
	void checkUserEmail(String email);

	// 修改密碼
	void changeUserPwd(Integer userId, UserChangePwdDTO dto);

	// 註銷帳號
	void terminateUser(Integer userId);

	// 查 user 是否存在
	boolean exists(Integer userid);
}
