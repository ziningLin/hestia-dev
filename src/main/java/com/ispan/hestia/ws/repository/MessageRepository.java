package com.ispan.hestia.ws.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.ispan.hestia.ws.dto.UnreadMessageCountDTO;
import com.ispan.hestia.ws.model.Message;

public interface MessageRepository extends MongoRepository<Message, String>{

	@Query(value = "{ 'conversationId' : ?0 , 'recipientUserId' : ?1 , 'isRead' : false }", count = true)
	int findUnreadMessagesForUser(String conversationId, Integer recipientUserId);
	
	@Aggregation(pipeline = {
	        "{ $match: { receiverId: ?0, isRead: false, conversationId: { $in: ?1 } } }",
	        "{ $group: { _id: '$conversationId', unreadCount: { $sum: 1 } } }"
	    })
    List<UnreadMessageCountDTO> findUnreadMsgCountByConversationIds(Integer userId, List<String> conversationIds);

	@Query("{ 'conversationId' : ?0 }")
    List<Message> findByConversationIdOrderByCreatedAtAsc(String conversationId);
	
	@Query("{ 'conversationId' : ?0 , 'receiverId' : ?1 }")
	List<Message> findByConversationIdAndReceiverIdAndIsReadFalse(String conversationId, Integer receiverId);
}
