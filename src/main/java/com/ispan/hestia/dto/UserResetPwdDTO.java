package com.ispan.hestia.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 重置密碼用
 */
@Getter
@Setter
public class UserResetPwdDTO {
	
    @NotBlank(message = "驗證碼不能為空")
    private String token;

    @NotBlank(message = "新密碼不能為空")
    private String newPwd;

    @NotBlank(message = "確認密碼不能為空")
    private String confirmPwd;
}
