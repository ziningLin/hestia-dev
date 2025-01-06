package com.ispan.hestia.strategy.discount;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ispan.hestia.exception.BadRequestException;
import com.ispan.hestia.exception.EntityNotFoundException;
import com.ispan.hestia.model.RoomAvailableDate;
import com.ispan.hestia.repository.RoomAvailableDateRepository;
import com.ispan.hestia.strategy.DiscountStrategy;

/**
 * 周末住宿優惠
 */
@Component
public class WeekendStayDiscountStrategy implements DiscountStrategy {

	@Autowired
	private RoomAvailableDateRepository roomAvailableDateRepo;

	public String getPromotionCode() {
		return "WEEKEND5";
	}

	/**
	 * 只要日期範圍中有任何一天是周末，就適用優惠
	 */
	@Override
	public boolean isEligible(Date checkInDate, Date checkOutDate) {
		if (checkInDate == null || checkOutDate == null) {
			throw new BadRequestException("入住日期或退房日期不能為空");
		}
		if (checkOutDate.before(checkInDate)) {
			throw new BadRequestException("退房日期不能早於入住日期");
		}

		Calendar calendar = Calendar.getInstance();
		Date currentDate = checkInDate;
		while (!currentDate.after(checkOutDate)) {
			calendar.setTime(currentDate);
			int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
			if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
				return true;
			}
			calendar.add(Calendar.DATE, 1);
			currentDate = calendar.getTime();
		}
		return false;
	}

	@Override
	public BigDecimal calculateDiscount(BigDecimal originalPrice, long stayDays) {
		return BigDecimal.ZERO;
	}

	/**
	 * 優惠 => 95折(總金額95%)
	 */
	@Override
	public BigDecimal calculateDiscountByDateRange(Integer roomId, Date checkInDate, Date checkOutDate) {
		if (roomId == null || checkInDate == null || checkOutDate == null) {
			throw new BadRequestException("房間 ID 或日期範圍不能為空");
		}
		Calendar calendar = Calendar.getInstance();
		BigDecimal discountAmount = BigDecimal.ZERO;

		Date currentDate = checkInDate;
		while (!currentDate.after(checkOutDate)) {
			calendar.setTime(currentDate);
			int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
			if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
				BigDecimal dailyPrice = getDailyPrice(roomId, currentDate);
				discountAmount = discountAmount.add(dailyPrice.multiply(BigDecimal.valueOf(0.05))); // 5% 折扣
			}
			calendar.add(Calendar.DATE, 1);
			currentDate = calendar.getTime();
		}
		return discountAmount;
	}

	/**
	 * 查找對應 room 的可用日期的價格
	 */
	private BigDecimal getDailyPrice(Integer roomId, Date date) {
		RoomAvailableDate roomAvailableDate = roomAvailableDateRepo.findAvailableDatesByRoomIdAndDate(roomId, date);
		if (roomAvailableDate == null) {
			throw new EntityNotFoundException(RoomAvailableDate.class, "Room ID and Date", roomId + " / " + date);
		}
		return BigDecimal.valueOf(roomAvailableDate.getPrice());
	}

}
