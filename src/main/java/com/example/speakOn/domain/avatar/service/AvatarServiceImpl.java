package com.example.speakOn.domain.avatar.service;

import com.example.speakOn.domain.avatar.converter.AvatarConverter;
import com.example.speakOn.domain.avatar.dto.AvatarResponse;
import com.example.speakOn.domain.avatar.repository.AvatarRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AvatarServiceImpl implements AvatarService {

    private final AvatarRepository avatarRepository;

    @Override
    public List<AvatarResponse.AvatarListItem> getAvatarList() {
        log.info("전체 아바타 목록 조회 시작");
        return AvatarConverter.toAvatarListItems(avatarRepository);
    }
}
