package com.example.speakOn.domain.avatar.controller;

import com.example.speakOn.domain.avatar.dto.AvatarResponse;
import com.example.speakOn.domain.avatar.service.AvatarService;
import com.example.speakOn.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/avatar")
@RequiredArgsConstructor
@Tag(name = "Avatar API", description = "아바타 관련 API")
public class AvatarController {

    private final AvatarService avatarService;

    @Operation(
            summary = "아바타 목록 조회",
            description = "모든 아바타 목록을 조회합니다. " +
                    "각 아바타의 ID, 이름, 프로필 이미지 URL, 국가, 나이 정보를 반환합니다."
    )
    @GetMapping("/list")
    public ApiResponse<List<AvatarResponse.AvatarListItem>> getAvatarList() {
        List<AvatarResponse.AvatarListItem> avatarList = avatarService.getAvatarList();
        return ApiResponse.onSuccess(avatarList);
    }
}
