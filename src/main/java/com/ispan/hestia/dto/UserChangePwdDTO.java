package com.ispan.hestia.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 用於傳輸用戶修改密碼資料
 */
@Getter
@Setter
public class UserChangePwdDTO {

	@NotBlank(message = "當前密碼不能為空")
	private String currentPwd;
	
	@NotBlank(message = "新密碼不能為空")
    private String newPwd;
	
	@NotBlank(message = "確認密碼不能為空")
    private String confirmPwd;
	     
}
