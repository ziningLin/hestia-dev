package com.ispan.hestia.ws.service;

import java.util.List;

import com.ispan.hestia.ws.dto.ConversationDTO;
import com.ispan.hestia.ws.dto.ConversationExistsDTO;
import com.ispan.hestia.ws.model.Message;

public interface ChatService {

	List<ConversationDTO> getAllConversations(Integer userId);

	List<Message> getMessagesByConversationId(String conversationId);

	void sendMessage(Message message);
	
	void markMessagesAsRead(String conversationId, Integer receiverId);
	
	void markMessageAsRead(String messageId);
	
	ConversationExistsDTO createOrGetConversation(Integer user1Id, Integer user2Id);
	
}
