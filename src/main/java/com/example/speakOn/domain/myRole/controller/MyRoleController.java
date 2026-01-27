package com.example.speakOn.domain.myRole.controller;

import com.example.speakOn.domain.myRole.dto.MyRoleRequest;
import com.example.speakOn.domain.myRole.dto.MyRoleResponse;
import com.example.speakOn.domain.myRole.service.MyRoleServiceImpl;
import com.example.speakOn.global.apiPayload.ApiResponse;
import com.example.speakOn.global.apiPayload.code.status.ErrorStatus;
import com.example.speakOn.global.util.AuthUtil;
import com.example.speakOn.global.validation.annotation.ApiErrorCodeExample;
import com.example.speakOn.global.validation.annotation.ApiErrorCodeExamples;
import com.example.speakOn.global.validation.annotation.ApiSuccessCodeExample;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "My Role API", description = "마이롤 관련 API")
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/my-role")
public class MyRoleController {

    private final MyRoleServiceImpl myRoleService;
    private final AuthUtil authUtil;

    @Operation(
            summary = "롤 추가 API",
            description = "사람(아바타), 직무, 상황을 선택하여 새로운 롤을 추가합니다." +
                    "중복된 아바타 + 직무 + 상황 조합은 'MY_ROLE_ALREADY_EXISTS'를 반환합니다."
    )
    @ApiSuccessCodeExample(resultClass = MyRoleResponse.CreateMyRoleResultDTO.class)
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "AVATAR_NOT_FOUND"),
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "MY_ROLE_ALREADY_EXISTS"),
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "USER_NOT_FOUND"),
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "_BAD_REQUEST"),
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "_INTERNAL_SERVER_ERROR")
    })
    @PostMapping
    public ApiResponse<MyRoleResponse.CreateMyRoleResultDTO> createMyRole(
            @Valid @RequestBody MyRoleRequest.CreateMyRoleDTO request) {

        Long userId = authUtil.getCurrentUserId();

        MyRoleResponse.CreateMyRoleResultDTO response = myRoleService.createMyRole(userId, request);

        return ApiResponse.onSuccess(response);
    }

    @Operation(
            summary = "롤 삭제 API",
            description = "특정 롤을 삭제합니다. 본인의 롤만 삭제할 수 있습니다."
    )
    @ApiSuccessCodeExample(resultClass = MyRoleResponse.DeleteMyRoleResultDTO.class)
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "MY_ROLE_NOT_FOUND"),
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "MY_ROLE_FORBIDDEN"),
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "USER_NOT_FOUND"),
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "_UNAUTHORIZED"),
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "_INTERNAL_SERVER_ERROR")
    })
    @DeleteMapping("/{myRoleId}")
    public ApiResponse<MyRoleResponse.DeleteMyRoleResultDTO> deleteMyRole(@PathVariable Long myRoleId) {

        Long userId = authUtil.getCurrentUserId();

        MyRoleResponse.DeleteMyRoleResultDTO response = myRoleService.deleteMyRole(userId, myRoleId);

        return ApiResponse.onSuccess(response);
    }
}