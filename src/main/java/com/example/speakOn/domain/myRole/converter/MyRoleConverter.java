package com.example.speakOn.domain.myRole.converter;

import com.example.speakOn.domain.myRole.dto.MyRoleResponse;
import com.example.speakOn.domain.myRole.entity.MyRole;

public class MyRoleConverter {

    public static MyRoleResponse.CreateMyRoleResultDTO toCreateMyRoleResultDTO(MyRole myRole) {
        return MyRoleResponse.CreateMyRoleResultDTO.builder()
                .myRoleId(myRole.getId())
                .avatarId(myRole.getAvatar().getId())
                .avatarName(myRole.getAvatar().getName())
                .job(myRole.getJob())
                .situation(myRole.getSituation())
                .build();
    }

    public static MyRoleResponse.DeleteMyRoleResultDTO toDeleteMyRoleResultDTO(Long myRoleId) {
        return MyRoleResponse.DeleteMyRoleResultDTO.builder()
                .myRoleId(myRoleId)
                .message("롤이 성공적으로 삭제되었습니다.")
                .build();
    }
}