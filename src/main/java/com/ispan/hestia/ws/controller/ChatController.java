package com.ispan.hestia.ws.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ispan.hestia.exception.BadRequestException;
import com.ispan.hestia.ws.dto.ConversationDTO;
import com.ispan.hestia.ws.dto.ConversationExistsDTO;
import com.ispan.hestia.ws.model.Message;
import com.ispan.hestia.ws.service.ChatService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/chat")
public class ChatController {
	
	@Autowired
    private ChatService chatService;
	
	/**
	 * 獲取 User 所有對話清單
	 * @return
	 */
	@GetMapping("/conversations")
	public ResponseEntity<List<ConversationDTO>> getAllConversations(HttpServletRequest req) {
		Integer userId = (Integer) req.getAttribute("userId");
		List<ConversationDTO> conversations = chatService.getAllConversations(userId);
	    return ResponseEntity.ok(conversations);
	}
	
	/**\
	 * 獲取該對話所以訊息紀錄
	 * @param conversationId
	 * @return
	 */
	@GetMapping("/messages/{conversationId}")
	public ResponseEntity<List<Message>> getMessagesByConversationId(@PathVariable String conversationId,HttpServletRequest req) {
		List<Message> messages = chatService.getMessagesByConversationId(conversationId);
	    return ResponseEntity.ok(messages);
	}
	
	/**
	 * 發送消息
	 * @param message
	 * @return
	 */
	@PostMapping("/messages")
	public ResponseEntity<Void> sendMessage(@RequestBody Message message) {
		chatService.sendMessage(message);
	    return ResponseEntity.ok().build();
	}
	
	/**
	 * 標記消息為已讀(整個對話)
	 * @param conversationId
	 * @param receiverId
	 * @return
	 */
	@PutMapping("/messages/read/{conversationId}")
	public ResponseEntity<Void> markMessagesAsRead(@PathVariable String conversationId, @RequestBody Map<String, Object> payload) {
	    Integer receiverId = (Integer) payload.get("receiverId");
	    chatService.markMessagesAsRead(conversationId, receiverId);
	    return ResponseEntity.ok().build();
	}
	
	/**
	 * 標記消息為已讀(單一訊息)
	 * @param payload
	 * @return
	 */
	@PutMapping("/messages/read")
    public ResponseEntity<Void> markMessageAsRead(@RequestBody Map<String, String> payload) {
        String messageId = payload.get("messageId");
        if (messageId == null || messageId.isBlank()) {
            throw new BadRequestException("MessageId錯誤");
        }
        chatService.markMessageAsRead(messageId);
        return ResponseEntity.ok().build();
    }
	
	
	/**
     * 創建或檢查對話
     */
	@PostMapping("/conversation")
	public ResponseEntity<Map<String, String>> createConversation(HttpServletRequest req,@RequestBody Map<String, Object> request){
		Integer userId = (Integer) req.getAttribute("userId");
		Integer otherUserId = (Integer) request.get("otherUserId");	
        if (userId == null || otherUserId == null || userId.equals(otherUserId)) {
            throw new BadRequestException("ID 不得重複或為空");
        }
		
        ConversationExistsDTO result = chatService.createOrGetConversation(userId, otherUserId);
        String conversationId = result.getConversationId();
        boolean isNewConversation = result.isNew();
        
        return ResponseEntity.ok(Map.of(
                "status", isNewConversation ? "created" : "exists",
                "conversationId", conversationId
            ));
	}
}
