package com.ispan.hestia.enums;

import lombok.Getter;

/**
 *  Room對應狀態
 */
@Getter
public enum RoomState {

	UNREVIEWED(20, "未審核"),
    APPROVED(21, "審核通過"),
    ONLINE(22, "已上架"),
    OFFLINE(23, "下架"),
    TEMPORARILY_OFFLINE(24, "暫時下架"),
    UNDER_REVIEW(25, "審核中");
	
	private final Integer stateId;
	
	private final String stateContent;

	RoomState(Integer stateId, String stateContent) {
		this.stateId = stateId;
		this.stateContent = stateContent;
	}
	
	//根據stateId獲取對應物件
	public static RoomState fromId(Integer stateId) {
		
        for (RoomState roomState : RoomState.values()) {
            if (roomState.getStateId() == stateId) {
                return roomState;
            }
        }
        throw new IllegalArgumentException("未知的Room狀態 id: " + stateId);
    }
	
}
