package com.ispan.hestia.mail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.ispan.hestia.exception.BadRequestException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class MailManagerImpl implements MailManager {

    @Autowired
    private JavaMailSender mailSender;
    
    /**
     * 發送通用通知Email
     * @param recipientEmail 收信者Email
     * @param subject 信件主旨
     * @param title Email標題
     * @param message 主要訊息內容
     * @param buttonUrl 按鈕連結(可為null)
     * @param buttonText 按鈕文字(可為null)
     */
    @Override
    public void sendNotificationEmail(String recipientEmail, String subject, String title, String message, String buttonUrl, String buttonText) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("title", title);
        placeholders.put("message", message);

        if (buttonUrl != null && !buttonUrl.isEmpty()) {
            String btnText = (buttonText != null && !buttonText.isEmpty()) ? buttonText : "查看詳情";
            String buttonHtml = "<a href=\"" + buttonUrl + "\" style=\"display: inline-block; margin-top: 20px; padding: 10px 20px; color: #F5F7FA; background-color: #2B6CB0; text-decoration: none; border-radius: 5px; font-weight: bold;\">" + btnText + "</a>";
            placeholders.put("buttonBlock", buttonHtml);
        } else {
            placeholders.put("buttonBlock", "");
        }

        String body = loadTemplate("templates/common_template.html", placeholders);
        sendEmail(recipientEmail, subject, body, true, null);
    }

    /**
     * 發送信箱驗證碼 Email
     */
    @Override
    public void sendVerificationCode(String recipientEmail, String verificationCode) {
        String subject = "Hestia - 驗證碼通知";

        // 加載 HTML 模板
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("verificationCode", verificationCode);
        String body = loadTemplate("templates/verification_code_template.html", placeholders);

        sendEmail(recipientEmail, subject, body, true, null);
    }
    
    /**
     * 發送重置密碼 Email
     */
    @Override
    public void sendPasswordResetCode(String recipientEmail, String url) {
        String subject = "Hestia - 重置密碼";

        // 加載 HTML 模板
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("url", url);
        String body = loadTemplate("templates/password_reset_template.html", placeholders);

        sendEmail(recipientEmail, subject, body, true, null);
    }

    /**
     * 發送HTML Email
     *
     * @param recipientEmail 接收方Email
     * @param subject        Email主旨
     * @param body           Email內容
     * @param isHtml         是否為HTML內容
     * @param attachmentPath 附件路徑（如果不需要可以為 null）
     */
    @Override
    public void sendEmail(String recipientEmail, String subject, String body, boolean isHtml, String attachmentPath) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(body, isHtml);

            // 如果有附件，附加到mail中
            if (attachmentPath != null) {
                FileSystemResource file = new FileSystemResource(new File(attachmentPath));
                helper.addAttachment(file.getFilename(), file);
            }
            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
			throw new BadRequestException("Email 發送失敗");
        }
    }
    
    /**
     * 加載模板並替換占位符
     */
    private String loadTemplate(String templatePath, Map<String, String> placeholders) {
        try {
            String template = new String(Files.readAllBytes(Paths.get(new ClassPathResource(templatePath).getURI())), "UTF-8");

            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                template = template.replace("{{" + entry.getKey() + "}}", entry.getValue());
            }
            return template;

        } catch (IOException e) {
			throw new BadRequestException("加載模板失敗: " + templatePath);
        }
    }
}

