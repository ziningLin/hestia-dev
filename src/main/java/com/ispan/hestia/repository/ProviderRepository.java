package com.ispan.hestia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ispan.hestia.model.Provider;

public interface ProviderRepository extends JpaRepository<Provider, Integer> {

	@Query("FROM Provider WHERE user.userId = :userId")
	Provider findProviderByUserId(@Param("userId") Integer userId);
}
