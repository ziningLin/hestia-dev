/**
 * 
 */
package com.ispan.hestia.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 用於傳輸用戶註冊資料
 */
@Getter
@Setter
public class UserRegistrationDTO {
	
	@NotBlank(message = "信箱不可為空")
    @Email(message = "信箱格式不正確")
	private String email;
	
	@NotBlank(message = "密碼不可為空")
    private String password;
	
	@NotBlank(message = "確認密碼不可為空")
    private String confirmPwd;
    
}
