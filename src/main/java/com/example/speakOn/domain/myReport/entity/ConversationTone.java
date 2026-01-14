package com.example.speakOn.domain.myReport.entity;

import com.example.speakOn.domain.myReport.enums.ToneType;
import com.example.speakOn.global.apiPayload.code.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "conversation_tone")
public class ConversationTone extends BaseEntity {

    // 리포트 (1:1, 필수)
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "report_id", nullable = false, unique = true)
    private MyReport report;

    // 사용자 톤
    @Enumerated(EnumType.STRING)
    @Column(name = "user_tone", length = 20)
    private ToneType userTone;

    // 기대 톤
    @Enumerated(EnumType.STRING)
    @Column(name = "expected_tone", length = 20)
    private ToneType expectedTone;
}
