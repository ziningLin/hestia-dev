package com.ispan.hestia.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.ispan.hestia.model.Promotion;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 */
@Getter
@Setter
@NoArgsConstructor
public class PromotionDTO {

	private Integer promotionId;
	private String name;
	private String promotionContent;
	private String promotionCode;
	private BigDecimal percentageDiscount;
	private BigDecimal amountDiscount;
	private Date startAt;
	private Date endAt;
	private boolean selected;

	public static PromotionDTO convertToDTO(Promotion promotion) {
		PromotionDTO dto = new PromotionDTO();
		dto.setPromotionId(promotion.getPromotionId());;
		dto.setName(promotion.getName());
		dto.setPromotionContent(promotion.getPromotionContent());
		dto.setPromotionCode(promotion.getPromotionCode());
		dto.setPercentageDiscount(promotion.getPercentageDiscount());
		dto.setAmountDiscount(promotion.getAmountDiscount());
		dto.setStartAt(promotion.getStartAt());
		dto.setEndAt(promotion.getEndAt());
		return dto;
	}
}
