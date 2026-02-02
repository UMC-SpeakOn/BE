package com.example.speakOn.domain.avatar.converter;

import com.example.speakOn.domain.avatar.dto.AvatarResponse;
import com.example.speakOn.domain.avatar.entity.Avatar;
import com.example.speakOn.domain.avatar.repository.AvatarRepository;
import java.util.List;
import java.util.stream.Collectors;

public class AvatarConverter {

    public static AvatarResponse.AvatarListItem toAvatarListItem(Avatar avatar) {
        return AvatarResponse.AvatarListItem.builder()
                .id(avatar.getId())
                .name(avatar.getName())
                .imgUrl(avatar.getImgUrl())
                .nationality(avatar.getNationality())
                .age(avatar.getAge())
                .build();
    }

    /**
     * 모든 아바타를 조회하여 응답 DTO 리스트로 변환
     */
    public static List<AvatarResponse.AvatarListItem> toAvatarListItems(AvatarRepository avatarRepository) {
        return avatarRepository.findAll()
                .stream()
                .map(AvatarConverter::toAvatarListItem)
                .collect(Collectors.toList());
    }
}
