package com.ispan.hestia.service;

import org.springframework.stereotype.Service;

import com.ispan.hestia.dto.ProviderInfoDTO;

@Service
public interface ProviderService {

	// 註冊房東資格
	ProviderInfoDTO register(Integer userId, ProviderInfoDTO dto);

	// 獲得房東資料
	ProviderInfoDTO getProviderInfo(Integer userId);

	// 修改房東資料
	ProviderInfoDTO updateProviderInfo(Integer userId, ProviderInfoDTO dto);

	public boolean exists(Integer id);
}
