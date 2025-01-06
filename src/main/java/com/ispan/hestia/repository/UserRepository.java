package com.ispan.hestia.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ispan.hestia.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	
	@Query("FROM User u WHERE u.email = :email")
	User findByUserEmail(@Param("email")String email);
	
	@Query("FROM User u WHERE u.id IN :ids")
	List<User> findAllByIds(@Param("ids") Collection<Integer> ids);
}
