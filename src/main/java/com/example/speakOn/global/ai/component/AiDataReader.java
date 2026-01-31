package com.example.speakOn.global.ai.component;

import com.example.speakOn.domain.avatar.entity.Avatar;
import com.example.speakOn.domain.avatar.entity.Style;
import com.example.speakOn.domain.avatar.repository.StyleRepository;
import com.example.speakOn.domain.myRole.entity.MyRole;
import com.example.speakOn.domain.myRole.repository.MyRoleRepository;
import com.example.speakOn.domain.avatar.enums.SituationType;
import com.example.speakOn.global.ai.exception.AiErrorCode;
import com.example.speakOn.global.apiPayload.code.status.ErrorStatus;
import com.example.speakOn.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AiDataReader {

    private final MyRoleRepository myRoleRepository;
    private final StyleRepository styleRepository;

    public MyRole getMyRoleOrThrow(Long myRoleId) {
        return myRoleRepository.findById(myRoleId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MY_ROLE_NOT_FOUND));
    }

    public Style getStyleOrThrow(Avatar avatar, SituationType situation) {
        return styleRepository.findByAvatarAndSituationType(avatar, situation)
                .orElseThrow(() -> new GeneralException(AiErrorCode.AI_STYLE_NOT_FOUND));
    }

    public String getPersonalGreeting(MyRole myRole) {
        return styleRepository.findByAvatarAndSituationType(myRole.getAvatar(), myRole.getSituation())
                .map(Style::getOpenGreeting)
                .orElse("");
    }
}