package com.example.palayo.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.palayo.domain.user.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByNickname(String nickname);


}
