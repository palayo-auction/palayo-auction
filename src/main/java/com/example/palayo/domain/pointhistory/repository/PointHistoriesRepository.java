package com.example.palayo.domain.pointhistory.repository;

import com.example.palayo.domain.pointhistory.entity.PointHistories;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PointHistoriesRepository extends JpaRepository<PointHistories, Long> {
    Page<PointHistories> findByUserId(Long id, Pageable pageable);
}
