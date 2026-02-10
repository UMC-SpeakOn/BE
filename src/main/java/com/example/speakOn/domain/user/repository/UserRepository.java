package com.example.speakOn.domain.user.repository;

import com.example.speakOn.domain.user.entity.User;
import com.example.speakOn.domain.user.enums.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findBySocialTypeAndSocialId(SocialType socialType, String socialId);

    Optional<User> findByEmail(String email);

    /**
     * 탈퇴하지 않은 사용자 조회 (로그인 시 사용)
     */
    @Query("SELECT u FROM User u WHERE u.socialType = :socialType AND u.socialId = :socialId AND u.isDeleted = false")
    Optional<User> findActiveBySocialTypeAndSocialId(
            @Param("socialType") SocialType socialType,
            @Param("socialId") String socialId
    );

    /**
     * 탈퇴하지 않은 사용자 ID 조회 (일반 조회 시 사용)
     */
    @Query("SELECT u FROM User u WHERE u.id = :id AND u.isDeleted = false")
    Optional<User> findActiveById(@Param("id") Long id);

    /**
     * 탈퇴한 사용자 조회 (복구 시 사용)
     */
    @Query("SELECT u FROM User u WHERE u.socialType = :socialType AND u.socialId = :socialId AND u.isDeleted = true")
    Optional<User> findDeletedBySocialTypeAndSocialId(
            @Param("socialType") SocialType socialType,
            @Param("socialId") String socialId
    );
}
