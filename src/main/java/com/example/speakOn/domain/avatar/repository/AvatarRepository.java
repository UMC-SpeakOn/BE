package com.example.speakOn.domain.avatar.repository;

import com.example.speakOn.domain.avatar.entity.Avatar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvatarRepository extends JpaRepository<Avatar, Long> {
}