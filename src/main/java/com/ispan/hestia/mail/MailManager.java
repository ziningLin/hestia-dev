package com.ispan.hestia.mail;

/**
 * 管理email發送
 */
public interface MailManager {
	
	/**
     * 發送通用通知Email
     * @param recipientEmail 收信者Email
     * @param subject 信件主旨
     * @param title Email標題
     * @param message 主要訊息內容
     * @param buttonUrl 按鈕連結(可為null)
     * @param buttonText 按鈕文字(可為null)
     */
    void sendNotificationEmail(String recipientEmail, String subject, String title, String message, String buttonUrl, String buttonText);
	
	// 發送基礎 email
	void sendEmail(String recipientEmail, String subject, String body,boolean isHtml, String attachmentPath);
	
	// 發送信箱驗證碼 email
	void sendVerificationCode(String recipientEmail, String verificationCode);
	
	// 發送重設密碼 email
	void sendPasswordResetCode(String recipientEmail, String verificationCode);
}
