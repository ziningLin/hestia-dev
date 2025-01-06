package com.ispan.hestia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ispan.hestia.model.City;

@Repository
public interface CityRepository extends JpaRepository<City, Integer> {

}
