package com.ispan.hestia.ws.dto;

import lombok.Data;

@Data
public class UnreadMessageCountDTO {
	private String id;
    private int unreadCount;
}
