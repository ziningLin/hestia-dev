package com.ispan.hestia.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ispan.hestia.dto.PromotionDTO;

@Service
public interface PromotionService {

	// 查找所有平台提供之 ALL_HOSTS 優惠
	List<PromotionDTO> getAllPlatformPromotions();
	
	// 查找所有優惠(單一roomId)
	List<PromotionDTO> getAllPlatformPromotionsWithStatus(Integer roomId);

	// 將優惠套用至房間
	void applyPromotionsToRoom(Integer roomId, List<Integer> promotionIds);

	// 查找房間適用優惠
	List<PromotionDTO> findPromotionsByRoomId(Integer roomId);

	// 計算優惠價格
	public Map<String, BigDecimal> calculateFinalPrice(Integer roomId, String promotionCode, Date checkInDate,
			Date checkOutDate);
}
