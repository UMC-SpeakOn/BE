package com.example.speakOn.domain.myRole.repository;

import com.example.speakOn.domain.avatar.entity.Avatar;
import com.example.speakOn.domain.avatar.enums.SituationType;
import com.example.speakOn.domain.myRole.entity.MyRole;
import com.example.speakOn.domain.myRole.enums.JobType;
import com.example.speakOn.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MyRoleRepository extends JpaRepository<MyRole, Long>, MyRoleRepositoryCustom {

    boolean existsByUserAndAvatarAndJobAndSituation(User user, Avatar avatar, JobType job, SituationType situation);

    // 특정 사용자의 모든 MyRole 조회 (최신순)
    List<MyRole> findByUserOrderByCreatedAtDesc(User user);

}