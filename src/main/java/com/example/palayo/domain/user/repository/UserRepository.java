package com.example.palayo.domain.user.repository;

import com.example.palayo.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    //임시로 사용하기 위한 repository
    Optional<User> findById(Long userId);
}
