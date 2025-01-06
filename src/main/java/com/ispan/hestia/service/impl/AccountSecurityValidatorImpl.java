package com.ispan.hestia.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ispan.hestia.enums.UserState;
import com.ispan.hestia.exception.BadRequestException;
import com.ispan.hestia.exception.JWTException;
import com.ispan.hestia.model.User;
import com.ispan.hestia.repository.UserRepository;
import com.ispan.hestia.service.AccountSecurityValidator;

/**
 * 處理帳號密碼相關驗證
 */
@Service
public class AccountSecurityValidatorImpl implements AccountSecurityValidator {


	@Autowired
	private PasswordEncoder pwdEncoder;
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private SubTransactionService subTransactionService;
	
	// 帳號凍結時間 => 10分鐘
	private long freezeTime = 1000 * 60 * 10;
	// 帳號錯誤登入次數上限 => 5次
	public static final int ERROR_LOGIN_NUMS = 5;

	/**
	 * 檢查帳號狀態
	 *@param
	 *@return
	 */
	@Override
    public void acountStatusValidate(User dbUser) {
		 long currentTime = System.currentTimeMillis();
		
		// 狀態 = 註銷
        if (dbUser.getState().getStateId() == UserState.CANCELLED.getStateId()) {
        	throw new BadRequestException("帳戶已被註銷，請聯絡管理員");
        }
        // 狀態 = 異常
        if (dbUser.getState().getStateId() == UserState.ABNORMAL.getStateId()) {

            // 異常(無最後錯誤登入紀錄) => 該異常狀態非錯誤登入導致
            if(dbUser.getLoginAttemptsLast() == null ) {
            	throw new BadRequestException("帳戶狀態異常，請聯絡管理員");
            }
            // 異常(有最後錯誤登入紀錄) => 該異常狀態為錯誤登入導致
            long lastAttemptTime = dbUser.getLoginAttemptsLast().getTime();
            if (currentTime - lastAttemptTime < freezeTime) {
            	throw new BadRequestException("帳戶已被鎖定，請稍後再試");
            } else {//重置嘗試次數
				dbUser.setState(dbUser.getPreviousState());
            	resetLoginAttempts(dbUser);
            }
        }
    }
	
	/**
	 * 驗證密碼 (使用子交易獨立管理錯誤登入資訊更新)
	 * 
	 * @param dbUser
	 * @param password
	 * @return
	 */
	@Override
	public void passwordValidate(User dbUser, String password) {
        if (!pwdEncoder.matches(password, dbUser.getPassword())) {
			subTransactionService.incrementLoginAttempts(dbUser.getUserId());
			throw new JWTException("密碼錯誤");
		} else {
			resetLoginAttempts(dbUser);
        }
    }

    /**
     * 檢查密碼強度
     * (一個大寫字母、一個小寫字母、一個數字和一個特殊字符，長度至少 8 位)
     * @param password
     * @return
     */
	@Override
    public boolean passwordStrengthValidate(String password) {
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        return password.matches(passwordPattern);
    }

    /**
     * 重置錯誤登入狀態
     * @param dbUser
     */
    private void resetLoginAttempts(User dbUser) {
        dbUser.setLoginAttempts(0);
        dbUser.setLoginAttemptsLast(null);
        userRepo.save(dbUser);
    }
}
