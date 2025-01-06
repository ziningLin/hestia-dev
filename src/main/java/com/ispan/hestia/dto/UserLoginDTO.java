package com.ispan.hestia.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 用於登入
 */
@Getter
@Setter
public class UserLoginDTO {

	@NotBlank(message = "信箱不可為空")
	private String email;
	
	@NotBlank(message = "密碼不可為空")	
    private String password;

	private boolean rememberMe = false;
}
