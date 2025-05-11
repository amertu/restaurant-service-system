package com.spring.restaurant.backend.repository;

import com.spring.restaurant.backend.entity.FloorLayout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FloorLayoutRepository extends JpaRepository<FloorLayout, Long> {

}
