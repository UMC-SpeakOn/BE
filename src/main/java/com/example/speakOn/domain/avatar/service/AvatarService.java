package com.example.speakOn.domain.avatar.service;

import com.example.speakOn.domain.avatar.dto.AvatarResponse;
import java.util.List;

public interface AvatarService {

    /**
     * 모든 아바타 목록 조회
     * @return 아바타 목록
     */
    List<AvatarResponse.AvatarListItem> getAvatarList();
}
