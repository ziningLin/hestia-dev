/**
 * 
 */
package com.ispan.hestia.service.impl;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ispan.hestia.enums.UserState;
import com.ispan.hestia.exception.BadRequestException;
import com.ispan.hestia.model.State;
import com.ispan.hestia.model.User;
import com.ispan.hestia.repository.StateRepository;
import com.ispan.hestia.repository.UserRepository;

/**
 * 處理子交易管理
 * 
 */
@Component
public class SubTransactionService {

	@Autowired
	private UserRepository userRepo;
	@Autowired
	private StateRepository stateRepo;

	/**
	 * 獨立的子交易，用來記錄用戶的錯誤登錄嘗試次數
	 * 
	 * @param dbUser
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void incrementLoginAttempts(Integer userId) {
		// 記錄錯誤登入資訊
		Optional<User> op = userRepo.findById(userId);
		if (op.isEmpty()) {
			throw new BadRequestException("非預期錯誤");
		}
		User user = op.get();
		user.setLoginAttempts(user.getLoginAttempts() + 1);
		user.setLoginAttemptsLast(new Date());

		// 錯誤次數達到限制，帳號狀態設為異常
		if (user.getLoginAttempts() >= AccountSecurityValidatorImpl.ERROR_LOGIN_NUMS) {
			State abnormalState = stateRepo.findByStateId(UserState.ABNORMAL.getStateId());
			user.setPreviousState(user.getState());
			user.setState(abnormalState);
		}
		userRepo.save(user);
	}

}
