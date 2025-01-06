package com.ispan.hestia.ws.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.ispan.hestia.ws.model.Conversation;

@Repository
public interface ConversationRepository extends MongoRepository<Conversation, String> {

	@Query("{ '$or': [ { 'user1Id': ?0 }, { 'user2Id': ?0 } ] }")
	List<Conversation> findConversationByUserId(Integer userId);

	@Query("{ $or: [ { user1Id: ?0, user2Id: ?1 }, { user1Id: ?1, user2Id: ?0 } ] }")
	Optional<Conversation> findByUserIds(Integer user1Id, Integer user2Id);
}
