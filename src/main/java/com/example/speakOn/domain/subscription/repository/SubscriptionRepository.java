package com.example.speakOn.domain.subscription.repository;

import com.example.speakOn.domain.subscription.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    /**
     * 유저의 유효한 구독 정보 조회 (현재 시간 기준 만료되지 않음)
     * 해지 여부와 무관하게 expiredAt으로만 판단
     */
    @Query("SELECT s FROM Subscription s WHERE s.user.id = :userId AND s.expiredAt > :currentTime")
    Optional<Subscription> findActiveSubscriptionByUserId(@Param("userId") Long userId, @Param("currentTime") LocalDateTime currentTime);

    /**
     * 사용자의 모든 구독 벌크 삭제 (회원 탈퇴 시)
     */
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Subscription s WHERE s.user.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);
}
