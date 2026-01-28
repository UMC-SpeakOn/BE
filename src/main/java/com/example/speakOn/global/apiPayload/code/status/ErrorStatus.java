package com.example.speakOn.global.apiPayload.code.status;

import com.example.speakOn.global.apiPayload.code.BaseCode;
import com.example.speakOn.global.apiPayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseCode {

    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // 인증 관련 에러
    INVALID_SOCIAL_TYPE(HttpStatus.BAD_REQUEST, "AUTH400", "지원하지 않는 소셜 로그인 타입입니다."),
    INVALID_SOCIAL_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH401", "유효하지 않은 소셜 로그인 액세스 토큰입니다."),
    KAKAO_USER_INFO_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH500", "카카오 사용자 정보 조회에 실패했습니다."),

    // 유저 관련 에러
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "USER401", "아이디와 일치하는 사용자가 없습니다."),

    // MyRole 관련 에러
    AVATAR_NOT_FOUND(HttpStatus.NOT_FOUND, "ROLE404", "존재하지 않는 아바타입니다."),
    MY_ROLE_ALREADY_EXISTS(HttpStatus.CONFLICT, "ROLE409", "이미 동일한 역할이 존재합니다."),
    MY_ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "ROLE404_1", "존재하지 않는 MyRole 입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDTO getReason() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ReasonDTO getReasonHttpStatus() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}