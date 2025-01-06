package com.ispan.hestia.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 接收註冊驗證碼
 */
@Getter
@Setter
public class VerificationDTO {
	
	@NotBlank(message = "信箱不可為空")
    private String email;
	
	@NotBlank(message = "驗證碼不可為空")
    private String verificationCode;

}
