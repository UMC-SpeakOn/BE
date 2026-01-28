package com.example.speakOn.domain.myRole.repository;

import com.example.speakOn.domain.avatar.entity.Avatar;
import com.example.speakOn.domain.avatar.enums.SituationType;
import com.example.speakOn.domain.myRole.entity.MyRole;
import com.example.speakOn.domain.myRole.enums.JobType;
import com.example.speakOn.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MyRoleRepository extends JpaRepository<MyRole, Long>, MyRoleRepositoryCustom {

    boolean existsByUserAndAvatarAndJobAndSituation(User user, Avatar avatar, JobType job, SituationType situation);

    Optional<MyRole> findByIdAndUser(Long id, User user);

}