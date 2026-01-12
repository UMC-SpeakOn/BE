package com.example.speakOn.domain.myRole.entity;

import com.example.speakOn.domain.avatar.entity.Avatar;
import com.example.speakOn.domain.avatar.enums.SituationType;
import com.example.speakOn.domain.myRole.enums.JobType;
import com.example.speakOn.domain.user.entity.User;
import com.example.speakOn.global.apiPayload.code.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(
        name = "my_role",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_my_role_unique_pick",
                        columnNames = {"user_id", "avatar_id", "job", "situation"}
                )
        }
)
public class MyRole extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avatar_id", nullable = false)
    private Avatar avatar;

    @Enumerated(EnumType.STRING)
    @Column(name = "job", nullable = false)
    private JobType job;

    @Enumerated(EnumType.STRING)
    @Column(name = "situation", nullable = false)
    private SituationType situation;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;
}
