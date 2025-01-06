package com.ispan.hestia.ws.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ispan.hestia.exception.EntityNotFoundException;
import com.ispan.hestia.exception.WebSocketException;
import com.ispan.hestia.model.User;
import com.ispan.hestia.repository.UserRepository;
import com.ispan.hestia.util.DateUtil;
import com.ispan.hestia.ws.dto.ConversationDTO;
import com.ispan.hestia.ws.dto.ConversationExistsDTO;
import com.ispan.hestia.ws.dto.UnreadMessageCountDTO;
import com.ispan.hestia.ws.endpoint.ChatEndpoint;
import com.ispan.hestia.ws.model.Conversation;
import com.ispan.hestia.ws.model.Message;
import com.ispan.hestia.ws.repository.ConversationRepository;
import com.ispan.hestia.ws.repository.MessageRepository;
import com.ispan.hestia.ws.service.ChatService;

import jakarta.websocket.Session;

@Service
public class ChatServiceImpl implements ChatService{

	@Autowired
    private ConversationRepository conversationRepo;
    @Autowired
    private MessageRepository messageRepo;
    @Autowired
    private UserRepository userRepo;

    /**
     * 查詢對話清單
     */
    @Override
    public List<ConversationDTO> getAllConversations(Integer userId) {
        // 查詢對話清單，如果沒有找到對話，直接返回空清單
        List<Conversation> list = conversationRepo.findConversationByUserId(userId);
        if (list.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 收集對方 userId 並建立 DTO
        Set<Integer> otherUserIds = new HashSet<>();
        List<ConversationDTO> conversationDTOList = new ArrayList<>();
        List<String> conversationIds = new ArrayList<>();

        for (Conversation conversation : list) {
            Integer otherUserId = conversation.getUser1Id().equals(userId) ? conversation.getUser2Id() : conversation.getUser1Id();
            otherUserIds.add(otherUserId);
            conversationIds.add(conversation.getId());
            conversationDTOList.add(ConversationDTO.fromConversation(conversation, userId, null, 0));
        }
        
        // 批量查詢所有對方用戶資料
        List<User> otherUsers = userRepo.findAllByIds(otherUserIds);
        Map<Integer, User> userMap = new HashMap<>();
        for (User user : otherUsers) {
            userMap.put(user.getUserId(), user);
        }

        // 批量查詢未讀消息數量
        Map<String, Integer> unreadMsgMap = new HashMap<>();
        List<UnreadMessageCountDTO> unreadCounts = messageRepo.findUnreadMsgCountByConversationIds(userId, conversationIds);
        for(UnreadMessageCountDTO unreadCountdto :  unreadCounts) {
        	String conversationId = unreadCountdto.getId();
        	int unreadCount = unreadCountdto.getUnreadCount();
        	unreadMsgMap.put(conversationId, unreadCount);
        }
        
        // 填充對方用戶的名稱/未讀訊息數量/大頭貼
        for (ConversationDTO dto : conversationDTOList) {
            User user = userMap.get(dto.getOtherUserId());
            if (user == null) {
                throw new EntityNotFoundException(User.class,"ID",dto.getOtherUserId().toString());
            }
            String otherUserNameOrEmail = user.getName();
            if (otherUserNameOrEmail == null || "".equals(otherUserNameOrEmail)) {
                otherUserNameOrEmail = user.getEmail();
            }
            dto.setOtherUserNameOrEmail(otherUserNameOrEmail);
            int unreadCount = unreadMsgMap.getOrDefault(dto.getId(), 0);
            dto.setUnreadMsgCount(unreadCount);
            dto.setPhoto(user.getPhoto());
        }
        return conversationDTOList;
    }

    /**
     * 查詢對話所有訊息
     */
    @Override
    public List<Message> getMessagesByConversationId(String conversationId) {
    	List<Message> messages = messageRepo.findByConversationIdOrderByCreatedAtAsc(conversationId);
        return messages;
    }

    /**
     * 處理發送消息
     */
    @Override
    public void sendMessage(Message message) {
    	// 持久化
        message.setCreatedAt(LocalDateTime.now());
        message.setRead(false);
        messageRepo.save(message);
        
        // 構建推送給前端的消息格式
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("type", "CHAT_MESSAGE");
        messageMap.put("id", message.getId());
        messageMap.put("conversationId", message.getConversationId());
        messageMap.put("senderId", message.getSenderId());
        messageMap.put("receiverId", message.getReceiverId());
        messageMap.put("content", message.getContent());     
        messageMap.put("createdAt",DateUtil.formatLocalDateTime(message.getCreatedAt(),"HH:mm"));

        // 如果接收方在線上直接推送
        Session receiverSession = ChatEndpoint.SESSIONS.get(message.getReceiverId());
        if (receiverSession != null && receiverSession.isOpen()) {
            try {
            	String messageJson = objectMapper.writeValueAsString(messageMap);
                receiverSession.getBasicRemote().sendText(messageJson);
            } catch (IOException e) {
                throw new WebSocketException("推送消息失敗，接收方可能已斷開連線。");
            }
        }
        
        // 更新對應的對話狀態
        String conversationId = message.getConversationId();
        Optional<Conversation> op = conversationRepo.findById(conversationId);
        if(op.isEmpty()) {
        	throw new EntityNotFoundException(conversationId.getClass(),"ID",conversationId);
        }
        Conversation conversation = op.get();
        conversation.setLastMsgContent(message.getContent());
        conversation.setLastMsgAt(LocalDateTime.now());
        conversationRepo.save(conversation);
    }

    /**
     * 標示消息為已讀(對話)
     * @param conversationId
     * @param receiverId
     */
    public void markMessagesAsRead(String conversationId, Integer receiverId) {
        List<Message> messages = messageRepo.findByConversationIdAndReceiverIdAndIsReadFalse(conversationId, receiverId);
        for (Message message : messages) {
            message.setRead(true);
        }
        messageRepo.saveAll(messages);
    }
    
    /**
     * 標記消息為已讀(訊息)
     */
    @Override
    public void markMessageAsRead(String messageId) {
        Optional<Message> op = messageRepo.findById(messageId);
        if (op.isEmpty()) {
            throw new EntityNotFoundException(Message.class, "ID", messageId);
        }
        Message message = op.get();
        if (!message.isRead()) {
            message.setRead(true);
            messageRepo.save(message);
        }
    }

    /**
     * 檢查是否已存在對話，若不存在則創建
     */
    public ConversationExistsDTO createOrGetConversation(Integer user1Id, Integer user2Id) {
        // 檢查是否已存在對話
        Optional<Conversation> existingConversation = conversationRepo.findByUserIds(user1Id, user2Id);
        if (existingConversation.isPresent()) {
        	return new ConversationExistsDTO(existingConversation.get().getId(), false);
        }

        // 不存在，創建新對話
        Conversation newConversation = new Conversation();
        newConversation.setUser1Id(user1Id < user2Id ? user1Id : user2Id); // 始終以小的 ID 作為 user1
        newConversation.setUser2Id(user1Id > user2Id ? user1Id : user2Id);
        newConversation.setCreatedAt(LocalDateTime.now());
        newConversation.setLastMsgAt(LocalDateTime.now());
        newConversation.setLastMsgContent("");
        Conversation save = conversationRepo.save(newConversation);

        return new ConversationExistsDTO(save.getId(), true);
    }
    
}
