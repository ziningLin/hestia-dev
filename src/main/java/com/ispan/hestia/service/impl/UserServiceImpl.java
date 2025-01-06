package com.ispan.hestia.service.impl;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ispan.hestia.dto.UserBasicInfoDTO;
import com.ispan.hestia.dto.UserChangePwdDTO;
import com.ispan.hestia.dto.UserLoginDTO;
import com.ispan.hestia.dto.UserRegistrationDTO;
import com.ispan.hestia.dto.UserResetPwdDTO;
import com.ispan.hestia.dto.UserUpdateInfoDTO;
import com.ispan.hestia.dto.VerificationDTO;
import com.ispan.hestia.enums.UserState;
import com.ispan.hestia.exception.BadRequestException;
import com.ispan.hestia.exception.EntityExistException;
import com.ispan.hestia.exception.EntityNotFoundException;
import com.ispan.hestia.exception.JWTException;
import com.ispan.hestia.mail.MailManager;
import com.ispan.hestia.model.PasswordResetToken;
import com.ispan.hestia.model.Provider;
import com.ispan.hestia.model.State;
import com.ispan.hestia.model.User;
import com.ispan.hestia.model.VerificationToken;
import com.ispan.hestia.repository.PasswordResetTokenRepository;
import com.ispan.hestia.repository.ProviderRepository;
import com.ispan.hestia.repository.StateRepository;
import com.ispan.hestia.repository.UserRepository;
import com.ispan.hestia.repository.VerificationTokenRepository;
import com.ispan.hestia.service.AccountSecurityValidator;
import com.ispan.hestia.service.UserService;
import com.ispan.hestia.util.JWTUtil;
import com.ispan.hestia.util.ProfileUtil;
import com.ispan.hestia.util.TokenGenerator;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private PasswordEncoder pwdEncoder;
	@Autowired
	private MailManager mailManager;
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private StateRepository stateRepo;
	@Autowired
	private VerificationTokenRepository verificationTokenRepo;
	@Autowired
	private PasswordResetTokenRepository passwordResetTokeRepo;
	@Autowired
	private AccountSecurityValidator accountSecurityVali;
	@Autowired
	private JWTUtil jwtUtil;
	@Autowired
	private ProviderRepository providerRepo;

	/**
	 * 登入
	 * 
	 * @param UserLoginDTO
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public String login(UserLoginDTO dto) {

		// db是否有對應信箱
		User dbUser = userRepo.findByUserEmail(dto.getEmail());
		if (dbUser == null) {
			throw new JWTException("找不到該信箱");
		}
		// 帳號狀態檢查
		accountSecurityVali.acountStatusValidate(dbUser);

		// 密碼驗證
		accountSecurityVali.passwordValidate(dbUser, dto.getPassword());

		// 根據 isRememberMe 決定 token 有效期限
		boolean rememberMe = dto.isRememberMe();

		// 生成 token
		String token = jwtUtil.createToken(
				"{\"userId\":\"" + dbUser.getUserId() + "\", \"isProvider\":\"" + dbUser.getIsProvider() + "\"}",
				rememberMe);
		return token;
	}

	/**
	 * 註冊
	 * 
	 * @param UserRegistrationDTO
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void register(UserRegistrationDTO dto) {

		// 密碼強度驗證
		if (!accountSecurityVali.passwordStrengthValidate(dto.getPassword())) {
			throw new BadRequestException("密碼強度不符");
		}
		// 檢查信箱是否已存在
		User dbUser = userRepo.findByUserEmail(dto.getEmail());
		if (dbUser != null) {
			throw new EntityExistException(User.class, "email", dto.getEmail());
		}
		// 密碼加密
		User user = new User();
		user.setEmail(dto.getEmail());
		user.setPassword(pwdEncoder.encode(dto.getPassword()));
		userRepo.save(user);
	}

	/**
	 * 發送信箱驗證碼 Email
	 * 
	 * @param userId
	 */
	public void sendVerifyCodeEmail(Integer userId) {
		Optional<User> op = userRepo.findById(userId);
		if (op.isEmpty()) {
			throw new EntityNotFoundException(User.class, "userId", userId.toString());
		}
		User user = op.get();
		// 帳號狀態 == 未驗證才繼續
		if (user.getState().getStateId() != UserState.UNVERIFIED.getStateId()) {
			throw new BadRequestException("無法發送驗證信，請確認帳號狀態");
		}
		// 產生驗證碼
		String verificationCode = TokenGenerator.generateVerificationCode();
		VerificationToken token = new VerificationToken(user, verificationCode);
		verificationTokenRepo.save(token);

		// 發送驗證碼
		mailManager.sendVerificationCode(user.getEmail(), verificationCode);
	}

	/**
	 * 信箱驗證碼驗證
	 * 
	 * @param verificationDTO
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void verifyCode(VerificationDTO verificationDTO) {

		// 根據 user 的 email 查找 user
		User user = userRepo.findByUserEmail(verificationDTO.getEmail());
		if (user == null) {
			throw new EntityNotFoundException(User.class, "email", verificationDTO.getEmail());
		}
		// 根據 user 查找驗證碼
		VerificationToken token = verificationTokenRepo.findByUserId(user.getUserId());
		if (token == null || !token.getVerificationCode().equals(verificationDTO.getVerificationCode())) {
			throw new BadRequestException("驗證碼不正確");
		}
		// 驗證是否過期
		if (token.getExpiryTime().before(new Date())) {
			throw new BadRequestException("驗證碼已過期");
		}
		// 驗證成功，更新user狀態為正常
		State normalState = stateRepo.findByStateId(UserState.NORMAL.getStateId());
		user.setState(normalState);
		userRepo.save(user);

		// 刪除驗證碼
		verificationTokenRepo.delete(token);
	}

	/**
	 * 重發信箱驗證碼Email
	 * 
	 * @param email
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void resendVerificationCode(String email) {

		User dbUser = userRepo.findByUserEmail(email);
		if (dbUser == null) {
			throw new EntityNotFoundException(User.class, "email", email);
		}
		// 帳號處與"未認證"狀態才繼續
		if (dbUser.getState().getStateId() != UserState.UNVERIFIED.getStateId()) {
			throw new BadRequestException("無法進行驗證，請確認帳號狀態");
		}

		VerificationToken token = verificationTokenRepo.findByUserId(dbUser.getUserId());

		// Token 存在且未過期，直接重新發送
		if (token != null && token.getExpiryTime().after(new Date())) {
			mailManager.sendVerificationCode(dbUser.getEmail(), token.getVerificationCode());
			return;
		}
		// 如果 Token 不存在或已過期，生成新的 Token
		String newToken = TokenGenerator.generateVerificationCode();
		if (token == null) {
			token = new VerificationToken(dbUser, newToken);
		} else {
			// 更新現有的 Token
			token.setVerificationCode(newToken);
			TokenGenerator.setTokenExpiryTime(token, 30);
		}
		verificationTokenRepo.save(token);
		mailManager.sendVerificationCode(dbUser.getEmail(), newToken);
	}

	/**
	 * 發送重置密碼Email
	 * 
	 * @param email
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void sendPasswordResetEmail(String email) {

		// 驗證網址根路徑(暫時放這)
		String baseUrl = "http://localhost:8080/hestia/user/auth/reset-password/verify";

		User user = userRepo.findByUserEmail(email);
		if (user == null) {
			throw new EntityNotFoundException(User.class, "email", email);
		}

		PasswordResetToken dbToken = passwordResetTokeRepo.findByUserId(user.getUserId());

		// 更新或建立新的 Token
		dbToken = TokenGenerator.createOrUpdatePasswordResetToken(user, dbToken);
		passwordResetTokeRepo.save(dbToken);

		// 生成重置密碼的 URL
		String url = TokenGenerator.generateResetPasswordUrl(baseUrl, dbToken.getResetCode());

		// 發送重置密碼連結
		mailManager.sendPasswordResetCode(email, url);
	}

	/**
	 * 接收重置密碼連結
	 * 
	 * @param token
	 */
	@Override
	public void verifyResetPasswordToken(String token) {

		PasswordResetToken resetToken = passwordResetTokeRepo.findByResetCode(token);

		if (resetToken == null) {
			throw new EntityNotFoundException(PasswordResetToken.class, "resetCode", token);
		}
		if (resetToken.getExpiryTime().before(new Date())) {
			throw new BadRequestException("驗證連結已過期");
		}
		// 通過驗證 => 允許進入重置密碼頁面
	}

	/**
	 * 重置密碼
	 * 
	 * @param UserResetPwdDTO
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void resetPassword(UserResetPwdDTO dto) {

		PasswordResetToken token = passwordResetTokeRepo.findByResetCode(dto.getToken());

		if (token == null) {
			throw new BadRequestException("驗證碼錯誤");
		}
		// 驗證是否過期
		if (token.getExpiryTime().before(new Date())) {
			throw new BadRequestException("驗證碼已過期");
		}
		// 密碼強度驗證
		if (!accountSecurityVali.passwordStrengthValidate(dto.getNewPwd())) {
			throw new BadRequestException("密碼強度不符");
		}
		User dbUser = token.getUser();
		if (dbUser == null) {
			throw new EntityNotFoundException(User.class, "Id", token.getUser().getUserId().toString());
		}
		// 更新狀態
		if (dbUser.getPreviousState() != null) {
			dbUser.setState(dbUser.getPreviousState());
			dbUser.setLoginAttempts(0);
		}
		// 更新密碼
		dbUser.setPassword(pwdEncoder.encode(dto.getNewPwd()));
		dbUser.setLastEditTime(new Date());
		userRepo.save(dbUser);

		passwordResetTokeRepo.delete(token);
	}

	/**
	 * 獲得user資料
	 * 
	 * @param userId
	 */
	@Override
	public UserBasicInfoDTO getUserInfo(Integer userId) {

		Optional<User> op = userRepo.findById(userId);

		if (op.isEmpty()) {
			throw new EntityNotFoundException(User.class, "userId", userId.toString());
		}
		User user = op.get();
		UserBasicInfoDTO dto = UserBasicInfoDTO.fromUser(user);
		
		if(user.getIsProvider()) {
			Provider provider = providerRepo.findProviderByUserId(userId);
			Integer providerId = provider.getProviderId();
			dto.setProviderId(providerId);
		}
		return dto;
	}

	/**
	 * 修改user資料
	 * 
	 * @param userId
	 * @param name      姓名
	 * @param birthdate 生日
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public UserBasicInfoDTO updateUserInfo(Integer userId, UserUpdateInfoDTO dto) {

		Optional<User> op = userRepo.findById(userId);

		if (op.isEmpty()) {
			throw new EntityNotFoundException(User.class, "Id", userId.toString());
		}
		User dbUser = op.get();
		if (dto.getName() != null && !dto.getName().isEmpty()) {
			dbUser.setName(dto.getName());
		}
		if (dto.getBirth() != null) {
			dbUser.setBirthdate(dto.getBirth());
			System.out.println(dbUser.getBirthdate());
		}
		dbUser.setLastEditTime(new Date());
		User save = userRepo.save(dbUser);

		return UserBasicInfoDTO.fromUser(save);
	}

	/**
	 * 修改user頭貼
	 * 
	 * @param
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public UserBasicInfoDTO updateUserPhoto(Integer userId, MultipartFile photo) {
		Optional<User> op = userRepo.findById(userId);

		// 驗證檔案格式
		ProfileUtil.validateFileFormat(photo);
		// 驗證 MIME 類型
		ProfileUtil.validateMimeType(photo);
		// 驗證檔案大小
		ProfileUtil.validateFileSize(photo);

		if (op.isEmpty()) {
			throw new EntityNotFoundException(User.class, "Id", userId.toString());
		}
		User dbUser = op.get();

		if (photo != null && !photo.isEmpty()) {
			try {
				dbUser.setPhoto(photo.getBytes());
			} catch (IOException e) {
				throw new BadRequestException("圖片上傳失敗，請稍後再試");
			}
		} else {
			throw new BadRequestException("圖片檔案錯誤，請重新上傳");
		}
		dbUser.setLastEditTime(new Date());
		User save = userRepo.save(dbUser);

		return UserBasicInfoDTO.fromUser(save);
	}

	/**
	 * 修改密碼 userId、UserChangePwdDTO
	 * 
	 * @param
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void changeUserPwd(Integer userId, UserChangePwdDTO dto) {

		Optional<User> op = userRepo.findById(userId);
		if (op.isEmpty()) {
			throw new EntityNotFoundException(User.class, "Id", userId.toString());
		}
		User dbUser = op.get();

		// 驗證密碼強度
		if (!accountSecurityVali.passwordStrengthValidate(dto.getNewPwd())) {
			throw new BadRequestException("密碼強度不符");
		}
		// 驗證舊密碼
		if (!pwdEncoder.matches(dto.getCurrentPwd(), dbUser.getPassword())) {
			throw new BadRequestException("舊密碼錯誤");
		}
		// 更新密碼
		dbUser.setPassword(pwdEncoder.encode(dto.getNewPwd()));
		dbUser.setLastEditTime(new Date());
		userRepo.save(dbUser);
	}

	/**
	 * 檢查信箱
	 * 
	 * @param email
	 */
	@Override
	public void checkUserEmail(String email) {

		User user = userRepo.findByUserEmail(email);

		if (user != null) {
			throw new EntityExistException(User.class, "email", email);
		}
	}

	/**
	 * 註銷帳號
	 * 
	 * @param userId
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void terminateUser(Integer userId) {

		Optional<User> op = userRepo.findById(userId);

		if (op.isEmpty()) {
			throw new EntityNotFoundException(User.class, "Id", userId.toString());
		}
		User dbUser = op.get();

		State cancelledState = stateRepo.findByStateId(UserState.CANCELLED.getStateId());
		dbUser.setState(cancelledState);
		dbUser.setLastEditTime(new Date());

		userRepo.save(dbUser);
	}

	/* 用 id 確認此 user 存在 */
	@Override
	public boolean exists(Integer id) {
		if (id != null) {
			return userRepo.existsById(id);
		}
		return false;
	}
}
