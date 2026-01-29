package com.example.speakOn.domain.myRole.service;

import com.example.speakOn.domain.myRole.dto.MyRoleRequest;
import com.example.speakOn.domain.myRole.dto.MyRoleResponse;

public interface MyRoleService {

    // 마이롤 추가
    MyRoleResponse.CreateMyRoleResultDTO createMyRole(Long userId, MyRoleRequest.CreateMyRoleDTO request);

    // 마이롤 삭제
    MyRoleResponse.DeleteMyRoleResultDTO deleteMyRole(Long userId, Long myRoleId);

    // 마이롤 목록 조회
    MyRoleResponse.MyRoleListDTO getMyRoles(Long userId);

}