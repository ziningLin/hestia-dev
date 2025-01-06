package com.ispan.hestia.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ispan.hestia.model.State;

public interface StateRepository extends JpaRepository<State, Integer> {

	State findByStateId(Integer stateId);
}
