package com.ispan.hestia.enums;

import lombok.Getter;

/**
 * User對應狀態
 */
@Getter
public enum UserState {
	
	NORMAL(10, "正常"),
    UNVERIFIED(11, "未認證"),
    UNDER_REVIEW(12, "審核中"),
    ABNORMAL(13, "異常"),
    CANCELLED(14, "註銷");
	
	private final Integer stateId;
	
	private final String stateContent;
	
	UserState(Integer stateId , String stateContent){
		this.stateId = stateId;
		this.stateContent = stateContent;
	}
	
	//根據stateId獲取對應物件
	public static UserState fromStateId(Integer stateId) {
		
		for(UserState userState : UserState.values()) {
			if(userState.getStateId() == stateId) {
				return userState;
			}
		}
		throw new IllegalArgumentException("未知的User狀態 : "+stateId);
	}

}
