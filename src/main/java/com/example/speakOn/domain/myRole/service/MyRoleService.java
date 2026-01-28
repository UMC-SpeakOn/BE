package com.example.speakOn.domain.myRole.service;

import com.example.speakOn.domain.myRole.dto.MyRoleRequest;
import com.example.speakOn.domain.myRole.dto.MyRoleResponse;

public interface MyRoleService {

    MyRoleResponse.CreateMyRoleResultDTO createMyRole(Long userId, MyRoleRequest.CreateMyRoleDTO request);

}