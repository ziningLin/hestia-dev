package com.ispan.hestia.dto;


import com.ispan.hestia.model.Provider;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 用於傳輸房東資料
 * 查詢、修改
 */
@Getter
@Setter
@NoArgsConstructor
public class ProviderInfoDTO {
	
	@NotBlank(message="帳戶不得為空")
	private String bankAccount;
	
	@NotBlank(message="地址不得為空")
	private String address;
	
	private Integer stateId;
	
	public static ProviderInfoDTO fromProvider(Provider provider) {
		ProviderInfoDTO dto = new ProviderInfoDTO();
		dto.setBankAccount(provider.getBankAccount());
		dto.setAddress(provider.getAddress());
		dto.setStateId(provider.getState() != null ? provider.getState().getStateId() : null);
		return dto;
	}

	public static Provider fromProviderInfoDTO(ProviderInfoDTO dto) {
		Provider provider = new Provider();
		provider.setBankAccount(dto.getBankAccount());
		provider.setAddress(dto.getAddress());
		return provider;
	}
}
