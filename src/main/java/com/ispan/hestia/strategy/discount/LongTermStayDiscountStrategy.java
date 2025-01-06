package com.ispan.hestia.strategy.discount;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.ispan.hestia.exception.BadRequestException;
import com.ispan.hestia.strategy.DiscountStrategy;

/**
 * 長期入住優惠
 */
@Component
public class LongTermStayDiscountStrategy implements DiscountStrategy {

	public String getPromotionCode() {
		return "LONGTERM15";
	}

	/**
	 * 適用條件 => 入住天數 >= 15
	 */
	@Override
	public boolean isEligible(Date checkInDate, Date checkOutDate) {
		if (checkInDate == null || checkOutDate == null) {
			throw new BadRequestException("入住日期或退房日期不能為空");
		}
		if (checkOutDate.before(checkInDate)) {
			throw new BadRequestException("退房日期不能早於入住日期");
		}
		long stayDays = (checkOutDate.getTime() - checkInDate.getTime()) / (1000 * 60 * 60 * 24) + 1;
		return stayDays >= 15;
	}

	/**
	 * 優惠 => 85折(總金額85%)
	 */
	@Override
	public BigDecimal calculateDiscount(BigDecimal originalPrice, long stayDays) {
		if (originalPrice == null || originalPrice.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("原始價格無效");
		}
		return originalPrice.multiply(BigDecimal.valueOf(0.15));
	}

	@Override
	public BigDecimal calculateDiscountByDateRange(Integer roomId, Date checkInDate, Date checkOutDate) {
		return BigDecimal.ZERO;
	}
}
