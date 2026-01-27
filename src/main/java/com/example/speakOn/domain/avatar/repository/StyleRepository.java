package com.example.speakOn.domain.avatar.repository;

import com.example.speakOn.domain.avatar.entity.Avatar;
import com.example.speakOn.domain.avatar.entity.Style;
import com.example.speakOn.domain.avatar.enums.SituationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StyleRepository extends JpaRepository<Style, Long> {
    // 아바타와 상황(Situation)으로 해당 아바타의 스타일(인사말 등)을 찾음
    Optional<Style> findByAvatarAndSituationType(Avatar avatar, SituationType situationType);
}
