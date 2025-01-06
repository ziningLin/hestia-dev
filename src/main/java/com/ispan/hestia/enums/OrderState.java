package com.ispan.hestia.enums;

import lombok.Getter;

/**
 * Order對應狀態
 */
@Getter
public enum OrderState {

	PENDING_PAYMENT(30, "未付款"),
    PAID(31, "已付款"),
    WAITING_PAYMENT_CONFIRMATION(32, "待付款"),
    PAYMENT_CANCELLED(33, "未付款取消"),
    REFUND_REQUESTED(34, "申請退款"),
    REFUND_IN_PROGRESS(35, "退款中"),
    REFUND_COMPLETED(36, "退款完成"),
    REFUND_FAILED(37, "退款失敗"),
    COMPLETED(38, "完成");
	
	private final Integer stateId;
	
	private final String stateContent;

	OrderState(Integer stateId, String stateContent) {
		this.stateId = stateId;
		this.stateContent = stateContent;
	}
	
	//根據stateId獲取對應物件
	public static OrderState fromId(Integer stateId) {
		
        for (OrderState orderState : OrderState.values()) {
            if (orderState.getStateId() == stateId) {
                return orderState;
            }
        }
        throw new IllegalArgumentException("未知的Order狀態 id: " + stateId);
    }

}
