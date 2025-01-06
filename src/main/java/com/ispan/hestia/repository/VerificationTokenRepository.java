package com.ispan.hestia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ispan.hestia.model.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Integer>{

	@Query("FROM VerificationToken WHERE user.userId = :userId")
	VerificationToken findByUserId(@Param("userId")Integer userId);
}
