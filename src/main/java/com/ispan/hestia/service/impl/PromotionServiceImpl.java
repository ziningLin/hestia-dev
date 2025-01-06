
package com.ispan.hestia.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ispan.hestia.dto.PromotionDTO;
import com.ispan.hestia.exception.BadRequestException;
import com.ispan.hestia.exception.EntityNotFoundException;
import com.ispan.hestia.model.Promotion;
import com.ispan.hestia.model.PromotionRooms;
import com.ispan.hestia.model.Room;
import com.ispan.hestia.model.RoomAvailableDate;
import com.ispan.hestia.repository.PromotionRepository;
import com.ispan.hestia.repository.PromotionRoomRepository;
import com.ispan.hestia.repository.RoomAvailableDateRepository;
import com.ispan.hestia.repository.RoomRepository;
import com.ispan.hestia.service.PromotionService;
import com.ispan.hestia.strategy.DiscountStrategy;

@Service
public class PromotionServiceImpl implements PromotionService {
	
	@Autowired
	private PromotionRoomRepository promotionRoomRepo;
	@Autowired
	private PromotionRepository promotionRepo;
	@Autowired
	private RoomAvailableDateRepository roomAvailableDateRepo;
	@Autowired
	private RoomRepository roomRepo;
	
	private final Map<String, DiscountStrategy> discountStrategies = new HashMap<>();

	public PromotionServiceImpl(List<DiscountStrategy> strategyList) {
		for (DiscountStrategy strategy : strategyList) {
			discountStrategies.put(strategy.getPromotionCode(), strategy);
		}
	}

	/**
	 * 查找所有平台提供的優惠
	 * 
	 * @return List<PromotionDTO>
	 */
	public List<PromotionDTO> getAllPlatformPromotions() {
		List<Promotion> platformPromotions = promotionRepo.findAllByScopeAllHosts();
		if (platformPromotions.isEmpty()) {
			throw new BadRequestException("目前沒有任何可用的優惠");
		}
		List<PromotionDTO> promotionDTOList = new ArrayList<>();
		for (Promotion promotion : platformPromotions) {
			promotionDTOList.add(PromotionDTO.convertToDTO(promotion));
		}
		return promotionDTOList;
	}
	
	/**
	 * 查找所有優惠
	 * 單一房間已選擇的優惠以 selected 標示
	 */
	public List<PromotionDTO> getAllPlatformPromotionsWithStatus(Integer roomId) {
	    List<PromotionDTO> promotionDTOList = getAllPlatformPromotions();
	    if (roomId != null) {
	        List<Integer> appliedPromotionIds = promotionRoomRepo.findPromotionIdsByRoomId(roomId);
	        Set<Integer> appliedSet = new HashSet<>(appliedPromotionIds);
	        promotionDTOList.forEach(dto -> {
	            dto.setSelected(appliedSet.contains(dto.getPromotionId()));
	        });
	    }
	    
	    return promotionDTOList;
	}

	/**
	 * 將選定的優惠應用於特定房間
	 * 
	 * @param roomId       房間 ID
	 * @param promotionIds 優惠 ID 列表
	 */
	@Transactional
	public void applyPromotionsToRoom(Integer roomId, List<Integer> promotionIds) {
		// 查找房間
		Optional<Room> op = roomRepo.findById(roomId);
		if (op.isEmpty()) {
			throw new EntityNotFoundException(Room.class, "ID", roomId.toString());
		}
		Room room = op.get();
		
		// 房間已套用的優惠ID清單
	    List<Integer> currentlyAppliedIds = promotionRoomRepo.findPromotionIdsByRoomId(roomId);

	    Set<Integer> newSet = new HashSet<>(promotionIds);
	    Set<Integer> oldSet = new HashSet<>(currentlyAppliedIds);
		
	    // 需要刪除的優惠(舊有但不在新列表中)
	    Set<Integer> toRemove = new HashSet<>(oldSet);
	    toRemove.removeAll(newSet);

	    // 需要新增的優惠(新列表中但舊有沒有)
	    Set<Integer> toAdd = new HashSet<>(newSet);
	    toAdd.removeAll(oldSet);
	    
	    // 刪除不需的優惠關係
	    if(!toRemove.isEmpty()) {
	        promotionRoomRepo.deleteAllByRoomIdAndPromotionIds(roomId, toRemove);
	    }

	    // 新增需要的優惠關係
	    for (Integer promotionId : toAdd) {
	    	Optional<Promotion> opPro = promotionRepo.findById(promotionId);
	    	if(op.isEmpty()) {
	    		throw new EntityNotFoundException(Promotion.class, "ID", promotionId.toString());
	    	}
	    	Promotion promotion = opPro.get();
	        // 建立新關聯
	        PromotionRooms promotionRooms = new PromotionRooms();
	        promotionRooms.setRoom(room);
	        promotionRooms.setPromotion(promotion);
	        promotionRoomRepo.save(promotionRooms);
	    }
	}

	/**
	 * 查找單一 roomId 可用之優惠
	 * 
	 * @param roomId
	 * @return PromotionDTO集合
	 */
	public List<PromotionDTO> findPromotionsByRoomId(Integer roomId) {
		// 查找該房間已選擇的優惠
		List<Promotion> applicablePromotions = promotionRoomRepo.findPromotionsByRoomId(roomId);
		if (applicablePromotions.isEmpty()) {
			throw new BadRequestException("該房間不存在或房間目前無可用優惠");
		}

		// 將查找到的 Promotion 轉換為 DTO
		List<PromotionDTO> dtoList = new ArrayList<>();
		for (Promotion promotion : applicablePromotions) {
			dtoList.add(PromotionDTO.convertToDTO(promotion));
		}
		return dtoList;
	}

	/**
	 * 根據 計算折扣金額
	 * 
	 * @param roomId
	 * @param promotionCode 優惠代碼
	 * @param checkInDate   入住日期
	 * @param checkOutDate  退房日期
	 * @return Map<String,BigDecimal> originalPrice原價錢、finalPrice折扣後價錢、discount省去多少錢
	 */
	public Map<String, BigDecimal> calculateFinalPrice(Integer roomId, String promotionCode, Date checkInDate,
			Date checkOutDate) {
		// 根據優惠代碼選擇策略
		DiscountStrategy discountStrategy = discountStrategies.get(promotionCode);
		if (discountStrategy == null) {
			throw new BadRequestException("找不到對應的優惠：" + promotionCode);
		}
		// 計算入住天數
		long stayDays = (checkOutDate.getTime() - checkInDate.getTime()) / (1000 * 60 * 60 * 24) + 1;
		if (stayDays < 0) {
			throw new BadRequestException("請確認入住日期");
		}
		// 判斷優惠是否適用
		if (!discountStrategy.isEligible(checkInDate, checkOutDate)) {
			throw new BadRequestException(promotionCode + "優惠不適用");
		}
		// 查詢確認符合日期範圍的房間可用日期
		List<RoomAvailableDate> roomAvailableDates = roomAvailableDateRepo
				.findAvailableDatesByRoomIdAndDateRange(roomId, checkInDate, checkOutDate);
		if (roomAvailableDates.size() != stayDays) {
			throw new BadRequestException("日期出現錯誤，請重新選擇");
		}
		// 計算原始金額
		BigDecimal originalPrice = calculateOriginalPrice(roomId, checkInDate, checkOutDate);

		// 計算折扣後的金額
		BigDecimal discount = discountStrategy.calculateDiscount(originalPrice, stayDays);
		discount = discount.compareTo(BigDecimal.ZERO) > 0 ? discount
				: discountStrategy.calculateDiscountByDateRange(roomId, checkInDate, checkOutDate);

		// 折扣的金額(省了多少)
		BigDecimal finalPrice = originalPrice.subtract(discount);

		Map<String, BigDecimal> map = new HashMap<>();
		map.put("originalPrice", originalPrice);
		map.put("finalPrice", finalPrice);
		map.put("discount", discount);
		return map;
	}

	/**
	 * 計算原金額
	 * 
	 * @param roomId
	 * @param checkInDate
	 * @param checkOutDate
	 * @return
	 */
	private BigDecimal calculateOriginalPrice(Integer roomId, Date checkInDate, Date checkOutDate) {
		// 查詢符合日期範圍的所有房間可用日期
		List<RoomAvailableDate> roomAvailableDates = roomAvailableDateRepo
				.findAvailableDatesByRoomIdAndDateRange(roomId, checkInDate, checkOutDate);
		if (roomAvailableDates.isEmpty()) {
			throw new BadRequestException("找不到符合日期範圍的房間可用日期");
		}

		BigDecimal totalOriginalPrice = BigDecimal.ZERO;
		for (RoomAvailableDate roomAvailableDate : roomAvailableDates) {
			totalOriginalPrice = totalOriginalPrice.add(BigDecimal.valueOf(roomAvailableDate.getPrice()));
		}
		return totalOriginalPrice;
	}
}
