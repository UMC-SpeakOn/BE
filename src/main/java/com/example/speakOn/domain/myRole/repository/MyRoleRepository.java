package com.example.speakOn.domain.myRole.repository;

import com.example.speakOn.domain.avatar.entity.Avatar;
import com.example.speakOn.domain.avatar.enums.SituationType;
import com.example.speakOn.domain.myRole.entity.MyRole;
import com.example.speakOn.domain.myRole.enums.JobType;
import com.example.speakOn.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MyRoleRepository extends JpaRepository<MyRole, Long>, MyRoleRepositoryCustom {

    // 활성화된 롤 중복 체크 (soft delete 지원)
    boolean existsByUserAndAvatarAndJobAndSituationAndIsActiveTrue(User user, Avatar avatar, JobType job, SituationType situation);

    // 비활성화된 롤 조회 (재활성화용)
    Optional<MyRole> findByUserAndAvatarAndJobAndSituationAndIsActiveFalse(User user, Avatar avatar, JobType job, SituationType situation);

    // 특정 사용자의 모든 활성화된 MyRole 조회 (최신순)
    List<MyRole> findByUserAndIsActiveTrueOrderByCreatedAtDesc(User user);

    // ID와 활성화 상태로 MyRole 조회 (soft delete 지원)
    Optional<MyRole> findByIdAndIsActiveTrue(Long id);

}