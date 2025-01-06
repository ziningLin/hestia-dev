package com.ispan.hestia.ws.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ispan.hestia.ws.model.Conversation;

import lombok.Data;

@Data
public class ConversationDTO {

	private String id;
	private Integer otherUserId;
	private String otherUserNameOrEmail;
	private byte[] photo;
	private LocalDateTime createdTime;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd")
    private LocalDateTime lastMsgAt;  
    private String lastMsgContent; 
	private int unreadMsgCount;
	
	/**
	 * Conversation 轉換成 dto
	 * @param currentUserId 	自己的UserID
	 * @param otherUserName 	對方的名子
	 * @param unreadMsgCount	未讀消息數量
	 * @param conversation 		對話ID
	 * @return
	 */
    public static ConversationDTO fromConversation(Conversation conversation, Integer currentUserId, String otherUserNameOrEmail, int unreadMsgCount) {
        ConversationDTO dto = new ConversationDTO();
        
        dto.setId(conversation.getId());
        dto.setCreatedTime(conversation.getCreatedAt());
        dto.setLastMsgAt(conversation.getLastMsgAt());
        dto.setLastMsgContent(conversation.getLastMsgContent());
        
        // 設置對方的用戶 ID 和名稱
        Integer otherUserId = conversation.getUser1Id().equals(currentUserId) ? conversation.getUser2Id() : conversation.getUser1Id();
        dto.setOtherUserId(otherUserId);
        dto.setOtherUserNameOrEmail(otherUserNameOrEmail != null ? otherUserNameOrEmail : "未知用戶");

        // 設置未讀訊息數量
        dto.setUnreadMsgCount(unreadMsgCount);
        return dto;
    }
}
