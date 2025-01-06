/**
 * 
 */
package com.ispan.hestia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ispan.hestia.model.Promotion;

public interface PromotionRepository extends JpaRepository<Promotion, Integer> {

	@Query("SELECT p FROM Promotion p WHERE p.promotionScope = 'ALL_HOSTS'")
	List<Promotion> findAllByScopeAllHosts();

	@Query("SELECT p FROM Promotion p WHERE p.promotionCode = :promotionCode")
	Promotion findByPromotionCode(@Param("promotionCode") String promotionCode);

}
