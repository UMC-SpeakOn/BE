package com.example.speakOn.domain.user.controller;

import com.example.speakOn.domain.user.converter.UserConverter;
import com.example.speakOn.domain.user.dto.UserRequest;
import com.example.speakOn.domain.user.dto.UserResponse;
import com.example.speakOn.domain.user.service.UserCommandService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "User API", description = "유저에 관한 API")
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;
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

    // 온보딩 완료
    @Operation(
            summary = "온보딩 완료 API",
            description = "사용자의 온보딩을 완료 처리합니다."
    )
    @ApiSuccessCodeExample(resultClass = Void.class)
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "USER_NOT_FOUND"),
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "_UNAUTHORIZED"),
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "_INTERNAL_SERVER_ERROR")
    })
    @PatchMapping("/onboarded")
    public ApiResponse<Void> completeOnboarding() {
        Long userId = authUtil.getCurrentUserId();

        userQueryService.completeOnboarding(userId);
        return ApiResponse.onSuccess(null);
    }

    // 프로필 수정
    @Operation(
            summary = "프로필 정보 수정 API",
            description = "닉네임과 프로필 이미지를 수정합니다. (이미지는 S3에 업로드됩니다)"
    )
    @ApiSuccessCodeExample(resultClass = UserResponse.UpdateProfileResponseDTO.class)
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "USER_NOT_FOUND"),
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "_UNAUTHORIZED"),
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "_BAD_REQUEST"),
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "_INTERNAL_SERVER_ERROR")
    })
    @PatchMapping(value = "/profile", consumes = {"multipart/form-data"})
    public ApiResponse<UserResponse.UpdateProfileResponseDTO> updateProfile(
            @RequestPart(value = "nickname", required = false) String nickname,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {

        Long userId = authUtil.getCurrentUserId();
        log.info("프로필 수정 요청 - userId: {}, nickname: {}, hasImage: {}",
                userId, nickname, profileImage != null);

        // Converter를 사용하여 Request DTO 생성
        UserRequest.UpdateProfileRequestDto request = UserConverter.toUpdateProfileRequestDto(nickname, profileImage);

        UserResponse.UpdateProfileResponseDTO response = userCommandService.updateProfile(userId, request);

        return ApiResponse.onSuccess(response);
    }

    // 회원 탈퇴
    @Operation(
            summary = "회원 탈퇴 API",
            description = "사용자 계정을 완전히 삭제합니다. (Hard Delete)"
    )
    @ApiSuccessCodeExample(resultClass = UserResponse.WithdrawResponseDTO.class)
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "USER_NOT_FOUND"),
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "_UNAUTHORIZED"),
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "_INTERNAL_SERVER_ERROR")
    })
    @DeleteMapping("/withdraw")
    public ApiResponse<UserResponse.WithdrawResponseDTO> withdrawUser() {

        Long userId = authUtil.getCurrentUserId();
        log.info("회원 탈퇴 요청 - userId: {}", userId);

        UserResponse.WithdrawResponseDTO response = userCommandService.withdrawUser(userId);

        return ApiResponse.onSuccess(response);
    }
}
