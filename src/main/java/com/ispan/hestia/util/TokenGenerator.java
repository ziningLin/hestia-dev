package com.ispan.hestia.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import com.ispan.hestia.model.PasswordResetToken;
import com.ispan.hestia.model.User;
import com.ispan.hestia.model.VerificationToken;

/**
 * 驗證碼產生器
 */
public class TokenGenerator {

	/**
	 * 信箱驗證碼，範圍(100000~999999)
	 * @return
	 */
    public static String generateVerificationCode() {
    	Random random = new Random();
        int code = 100000 + random.nextInt(900000); 
        return String.valueOf(code);
    }
    
    /**
     * 產生 UUID 作為重設密碼的 Token
     * @return
     */
    public static String generateResetPasswordToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * 產生重設密碼的 URL，包含 Token
     * @param baseUrl
     * @param token
     * @return
     */
    public static String generateResetPasswordUrl(String baseUrl, String token) {
        return baseUrl + "?token=" + token;
    }
    
    /**
     * 建立新 Token 或更新現有 Token (更新密碼功能)
     * @param user
     * @param dbToken
     * @return
     */
    public static PasswordResetToken createOrUpdatePasswordResetToken(User user, PasswordResetToken dbToken) {
        String resetToken = generateResetPasswordToken();

        if (dbToken != null) {
            dbToken.setResetCode(resetToken);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
			calendar.add(Calendar.MINUTE, 30);

            dbToken.setExpiryTime(calendar.getTime());
            dbToken.setCreatedTime(new Date());
        } else {
            dbToken = new PasswordResetToken(user, resetToken);
        }
        return dbToken;
    }
    
    /**
     * 設置 Token 的過期時間
     * @param token 
     * @param minutes 有效時長(分鐘)
     */
    public static void setTokenExpiryTime(VerificationToken token, int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, minutes);
        token.setExpiryTime(calendar.getTime());
        token.setCreatedTime(new Date());
    }
    
}
