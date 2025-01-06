package com.ispan.hestia.strategy;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 管理優惠所需之策略模式
 */
public interface DiscountStrategy {

	/**
	 * 返回策略對應的優惠代碼
	 */
	String getPromotionCode();

	/**
	 * 檢查是否符合優惠條件
	 * 
	 * @param checkInDate  入住日期
	 * @param checkOutDate 退房日期
	 * @return 是否符合條件
	 */
	boolean isEligible(Date checkInDate, Date checkOutDate);

	/**
	 * 根據單一金額計算折扣後的金額
	 * 
	 * @param originalPrice 原始金額
	 * @param stayDays      入住天數
	 * @return 折扣金額
	 */
	BigDecimal calculateDiscount(BigDecimal originalPrice, long stayDays);

	/**
	 * 根據日期範圍計算折扣後的金額
	 * 
	 * @param roomId       房間的ID
	 * @param checkInDate  入住日期
	 * @param checkOutDate 退房日期
	 * @return 計算後的折扣金額
	 */
	BigDecimal calculateDiscountByDateRange(Integer roomId, Date checkInDate, Date checkOutDate);
}
