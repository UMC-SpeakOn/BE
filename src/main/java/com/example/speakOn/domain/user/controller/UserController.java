package com.example.speakOn.domain.user.controller;

import com.example.speakOn.domain.user.dto.UserResponse;
import com.example.speakOn.domain.user.service.UserQueryService;
import com.example.speakOn.global.apiPayload.ApiResponse;
import com.example.speakOn.global.apiPayload.code.status.ErrorStatus;
import com.example.speakOn.global.util.AuthUtil;
import com.example.speakOn.global.validation.annotation.ApiErrorCodeExample;
import com.example.speakOn.global.validation.annotation.ApiErrorCodeExamples;
import com.example.speakOn.global.validation.annotation.ApiSuccessCodeExample;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User API", description = "유저에 관한 API")
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserQueryService userQueryService;
    private final AuthUtil authUtil;

    // 마이페이지
    @Operation(
            summary = "마이페이지 유저 정보 조회 API",
            description = "현재 로그인한 사용자의 마이페이지 정보를 조회하는 API입니다."
    )
    @ApiSuccessCodeExample(resultClass = UserResponse.MyPageResponseDTO.class)
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "USER_NOT_FOUND"),
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "_UNAUTHORIZED"),
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "_BAD_REQUEST"),
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "_INTERNAL_SERVER_ERROR")
    })
    @GetMapping("/mypage")
    public ApiResponse<UserResponse.MyPageResponseDTO> getMyPage() {
        Long userId = authUtil.getCurrentUserId();
        log.info("마이페이지 조회 요청 - userId: {}", userId);

        UserResponse.MyPageResponseDTO response = userQueryService.getMyPageInfo(userId);
        return ApiResponse.onSuccess(response);
    }
}



