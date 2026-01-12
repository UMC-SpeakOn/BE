package com.example.speakOn.domain.avatar.entity;

import com.example.speakOn.domain.avatar.enums.CadenceType;
import com.example.speakOn.domain.avatar.enums.Gender;
import com.example.speakOn.global.apiPayload.code.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "avatar")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Avatar extends BaseEntity {

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "nationality", nullable = false, length = 50)
    private String nationality;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Column(name = "img_url", nullable = false, columnDefinition = "TEXT")
    private String imgUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 50)
    private Gender gender;

    @Column(name = "locale", nullable = false, length = 10)
    private String locale;

    @Enumerated(EnumType.STRING)
    @Column(name = "cadence", nullable = false, length = 20)
    private CadenceType cadenceType;

    @Column(name = "tts_voice_id", nullable = false, length = 100)
    private String ttsVoiceId;

}

