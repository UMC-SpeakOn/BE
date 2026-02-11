package com.example.speakOn.domain.user.entity;

import com.example.speakOn.domain.myReport.entity.ReportViewHistory;
import com.example.speakOn.domain.myRole.entity.MyRole;
import com.example.speakOn.domain.subscription.entity.Subscription;
import com.example.speakOn.domain.user.enums.Role;
import com.example.speakOn.domain.user.enums.SocialType;
import com.example.speakOn.global.apiPayload.code.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(name = "uk_social_type_social_id", columnNames = {"social_type", "social_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Column(name = "social_id", nullable = false)
    private String socialId;

    @Enumerated(EnumType.STRING)
    @Column(name = "social_type", nullable = false, length = 50)
    private SocialType socialType;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "name", nullable = false, length = 30)
    private String name;

    @Column(name = "nickname", length = 20)
    private String nickname;

    @Column(name = "profile_img_url")
    private String profileImgUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    @Builder.Default
    private Role role = Role.USER;

    @Column(name = "is_onboarded", nullable = false)
    @Builder.Default
    private Boolean isOnboarded = false;

    // 리포트 대화 로그 조회 횟수
    @Column(name = "total_log_view_count", nullable = false, columnDefinition = "integer default 0")
    @Builder.Default
    private Integer totalLogViewCount = 0;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<MyRole> myRoles = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Subscription> subscriptions = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ReportViewHistory> reportViewHistories = new ArrayList<>();

    /**
     * 닉네임 수정
     */
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * 프로필 이미지 URL 수정
     */
    public void updateProfileImage(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }

    // 온보딩 완료
    public void completeOnboarding() {
        this.isOnboarded = true;
    }

    /**
     * [비즈니스 로직] 대화 로그 조회 가능 여부 확인
     * 구독자이거나, 비구독자일 경우 총 조회수가 5회 미만인지 확인
     */
    public boolean canViewLog(boolean isSubscribed) {
        if (isSubscribed) {
            return true; // 구독자 경우 무제한
        }
        return this.totalLogViewCount < 5; // 5회 미만일 때만 true
    }

    /**
     * [비즈니스 로직] 조회수 증가
     */
    public void incrementLogViewCount() {
        this.totalLogViewCount++;
    }

}
