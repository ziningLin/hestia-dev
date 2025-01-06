package com.ispan.hestia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ispan.hestia.model.PasswordResetToken;


public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {

	PasswordResetToken findByResetCode(String token);
	
	@Query("FROM PasswordResetToken WHERE user.userId = :userId")
	PasswordResetToken findByUserId(@Param("userId")Integer userId);
}
