package com.ispan.hestia.strategy.discount;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.ispan.hestia.exception.BadRequestException;
import com.ispan.hestia.strategy.DiscountStrategy;

/**
 * 早鳥優惠
 */
@Component
public class EarlyBirdDiscountStrategy implements DiscountStrategy {

	public String getPromotionCode() {
		return "EARLYBIRD2024";
	}

	/**
	 * 適用條件 => 提早 30 天以上預定
	 */
	@Override
	public boolean isEligible(Date checkInDate, Date checkOutDate) {
		if (checkInDate == null || checkOutDate == null) {
			throw new BadRequestException("入住日期或退房日期不能為空");
		}
		long daysDifference = (checkInDate.getTime() - new Date().getTime()) / (1000 * 60 * 60 * 24);
		return daysDifference >= 30;
	}

	/**
	 * 
	 * 優惠 => 9折(總金額90%)
	 */
	@Override
	public BigDecimal calculateDiscount(BigDecimal originalPrice, long stayDays) {
		if (originalPrice == null || originalPrice.compareTo(BigDecimal.ZERO) <= 0) {
			throw new BadRequestException("原始價格無效");
		}
		return originalPrice.multiply(BigDecimal.valueOf(0.10));
	}

	@Override
	public BigDecimal calculateDiscountByDateRange(Integer roomId, Date checkInDate, Date checkOutDate) {
		return BigDecimal.ZERO;
	}

}
