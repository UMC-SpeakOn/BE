package com.example.speakOn.domain.user.entity;

import com.example.speakOn.domain.user.enums.SocialType;
import com.example.speakOn.global.apiPayload.code.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column(name = "name", nullable = false, length = 10)
    private String name;

    @Column(name = "nickname", length = 20)
    private String nickname;

    @Column(name = "profile_img_url")
    private String profileImgUrl;

    @Column(name = "is_onboarded", nullable = false)
    @Builder.Default
    private Boolean isOnboarded = false;

}
