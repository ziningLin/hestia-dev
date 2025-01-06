package com.ispan.hestia.service;

import com.ispan.hestia.model.User;


public interface AccountSecurityValidator {
	
	// 檢查帳號狀態
	void acountStatusValidate(User dbUser);
	
	// 驗證密碼
	void passwordValidate(User dbUser, String password);
	
	// 驗證密碼強度
	boolean passwordStrengthValidate(String password);

}
