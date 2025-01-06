package com.ispan.hestia.controller;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ispan.hestia.dto.PromotionDTO;
import com.ispan.hestia.service.PromotionService;

/**
 * 優惠管理接口
 */
@RestController
@RequestMapping("/promotion")
public class PromotionController {

	@Autowired
	private PromotionService promotionService;

	/**
	 * 獲取平台所有優惠
	 * 
	 * @return ResponseEntity<List<PromotionDTO>>
	 */
	@GetMapping("/all")
	public ResponseEntity<List<PromotionDTO>> getAllPlatformPromotions() {
		List<PromotionDTO> promotions = promotionService.getAllPlatformPromotions();
		return ResponseEntity.ok(promotions);
	}
	
	/**
	 * 獲取平台所有優惠
	 * @param roomId
	 * @return
	 */
	@GetMapping("/all/room")
	public ResponseEntity<List<PromotionDTO>> getAllPlatformPromotions(@RequestParam(required = false) Integer roomId) {
	    List<PromotionDTO> promotions = promotionService.getAllPlatformPromotionsWithStatus(roomId);
	    return ResponseEntity.ok(promotions);
	}

	/**
	 * 將選定的優惠應用於特定房間
	 * 
	 * @param roomId       房間 ID
	 * @param promotionIds 優惠 ID 列表
	 * @return ResponseEntity<String>
	 */
	@PostMapping("/apply/{roomId}")
	public ResponseEntity<String> applyPromotionsToRoom(@PathVariable Integer roomId,
			@RequestBody List<Integer> promotionIds) {
		promotionService.applyPromotionsToRoom(roomId, promotionIds);
		return ResponseEntity.ok("優惠方案已成功應用至您的房間 !");
	}
	
	/**
	 * 獲取房間適用所有優惠
	 */
	@GetMapping("/room/{roomId}")
	public ResponseEntity<List<PromotionDTO>> getPromotionsByRoomId(@PathVariable Integer roomId) {
		List<PromotionDTO> promotions = promotionService.findPromotionsByRoomId(roomId);
		return ResponseEntity.ok(promotions);
	}

	/**
	 * 計算房間折扣後的最終價格
	 *
	 * @param roomId        房間的ID
	 * @param promotionCode 優惠代碼
	 * @param checkInDate   入住日期 (yyyy-MM-dd)
	 * @param checkOutDate  退房日期 (yyyy-MM-dd)
	 * @return Map<String, BigDecimal>
	 *         originalPrice原價錢、finalPrice折扣後價錢、discount省去多少錢
	 */
	@GetMapping("/calculate-price")
	public ResponseEntity<Map<String, BigDecimal>> calculateFinalPrice(
			@RequestParam Integer roomId, @RequestParam String promotionCode,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date checkInDate,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date checkOutDate) {

		Map<String, BigDecimal> priceDetails = promotionService.calculateFinalPrice(roomId, promotionCode, checkInDate,
				checkOutDate);
		return ResponseEntity.ok(priceDetails);
	}


}
