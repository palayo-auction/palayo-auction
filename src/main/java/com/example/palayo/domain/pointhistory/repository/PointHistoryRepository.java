package com.example.palayo.domain.pointhistory.repository;

import com.example.palayo.domain.pointhistory.entity.PointHistories;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointHistoryRepository extends JpaRepository<PointHistories, Long> {
}
