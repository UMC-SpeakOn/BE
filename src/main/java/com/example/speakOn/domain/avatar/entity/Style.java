package com.example.speakOn.domain.avatar.entity;

import com.example.speakOn.domain.avatar.enums.SituationType;
import com.example.speakOn.global.apiPayload.code.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(
        name = "style",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_style_avatar_situation",
                        columnNames = {"avatar_id", "situation"}
                )
        }
)
public class Style extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avatar_id", nullable = false)
    private Avatar avatar;

    @Enumerated(EnumType.STRING)
    @Column(name = "situation", nullable = false, length = 50)
    private SituationType situationType;

    @Column(name = "open_greeting", nullable = false, columnDefinition = "TEXT")
    private String openGreeting;
}
