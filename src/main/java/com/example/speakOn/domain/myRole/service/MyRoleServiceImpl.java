package com.example.speakOn.domain.myRole.service;

import com.example.speakOn.domain.avatar.entity.Avatar;
import com.example.speakOn.domain.avatar.repository.AvatarRepository;
import com.example.speakOn.domain.myRole.converter.MyRoleConverter;
import com.example.speakOn.domain.myRole.dto.MyRoleRequest;
import com.example.speakOn.domain.myRole.dto.MyRoleResponse;
import com.example.speakOn.domain.myRole.entity.MyRole;
import com.example.speakOn.domain.myRole.repository.MyRoleRepository;
import com.example.speakOn.domain.user.entity.User;
import com.example.speakOn.domain.user.repository.UserRepository;
import com.example.speakOn.global.apiPayload.code.status.ErrorStatus;
import com.example.speakOn.global.apiPayload.exception.handler.ErrorHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyRoleServiceImpl implements MyRoleService {

    private final MyRoleRepository myRoleRepository;
    private final AvatarRepository avatarRepository;
    private final UserRepository userRepository;

    /**
     * 롤 추가 (사람/직무/상황 선택)
     *
     * @param userId  현재 로그인한 사용자 ID
     * @param request 롤 추가 요청 DTO (avatarId, job, situation)
     * @return 생성된 MyRole 정보
     */
    @Transactional
    @Override
    public MyRoleResponse.CreateMyRoleResultDTO createMyRole(Long userId, MyRoleRequest.CreateMyRoleDTO request) {

        // 1. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.USER_NOT_FOUND));

        // 2. 아바타 조회
        Avatar avatar = avatarRepository.findById(request.getAvatarId())
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.AVATAR_NOT_FOUND));

        // 3. 중복 체크 (같은 user, avatar, job, situation 조합이 이미 존재하는지)
        boolean exists = myRoleRepository.existsByUserAndAvatarAndJobAndSituation(
                user, avatar, request.getJob(), request.getSituation());
        if (exists) {
            throw new ErrorHandler(ErrorStatus.MY_ROLE_ALREADY_EXISTS);
        }

        // 4. MyRole 생성 및 저장
        MyRole myRole = MyRole.builder()
                .user(user)
                .avatar(avatar)
                .job(request.getJob())
                .situation(request.getSituation())
                .build();

        MyRole savedMyRole = myRoleRepository.save(myRole);

        // 5. 응답 변환
        return MyRoleConverter.toCreateMyRoleResultDTO(savedMyRole);
    }

    /**
     * 롤 삭제 (hard delete - DB에서 실제 삭제)
     *
     * @param userId   현재 로그인한 사용자 ID
     * @param myRoleId 삭제할 MyRole ID
     * @return 삭제된 롤 정보
     */
    @Transactional
    @Override
    public MyRoleResponse.DeleteMyRoleResultDTO deleteMyRole(Long userId, Long myRoleId) {

        // 1. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.USER_NOT_FOUND));

        // 2. MyRole 조회
        MyRole myRole = myRoleRepository.findById(myRoleId)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.MY_ROLE_NOT_FOUND));

        // 3. 권한 검증 - 본인의 롤인지 확인
        if (!myRole.getUser().getId().equals(user.getId())) {
            throw new ErrorHandler(ErrorStatus.MY_ROLE_FORBIDDEN);
        }

        // 4. Hard delete (DB에서 실제 삭제)
        myRoleRepository.delete(myRole);

        // 5. 응답 변환
        return MyRoleConverter.toDeleteMyRoleResultDTO(myRoleId);
    }

    /**
     * 롤 목록 조회
     *
     * @param userId 현재 로그인한 사용자 ID
     * @return 사용자의 모든 롤 목록
     */
    @Override
    public MyRoleResponse.MyRoleListDTO getMyRoles(Long userId) {

        // 1. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.USER_NOT_FOUND));

        // 2. 사용자의 모든 MyRole 조회 (최신순)
        List<MyRole> myRoles = myRoleRepository.findByUserOrderByCreatedAtDesc(user);

        // 3. 응답 변환
        return MyRoleConverter.toMyRoleListDTO(myRoles);
    }
}