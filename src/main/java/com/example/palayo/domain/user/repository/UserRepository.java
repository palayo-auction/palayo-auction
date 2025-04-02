package com.example.palayo.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.palayo.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
