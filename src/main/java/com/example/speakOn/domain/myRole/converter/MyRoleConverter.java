package com.example.speakOn.domain.myRole.converter;

import com.example.speakOn.domain.avatar.entity.Avatar;
import com.example.speakOn.domain.myRole.dto.MyRoleResponse;
import com.example.speakOn.domain.myRole.entity.MyRole;

import java.util.List;
import java.util.stream.Collectors;

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

    public static MyRoleResponse.MyRoleDetailDTO toMyRoleDetailDTO(MyRole myRole) {
        Avatar avatar = myRole.getAvatar();
        return MyRoleResponse.MyRoleDetailDTO.builder()
                .myRoleId(myRole.getId())
                .avatarImgUrl(avatar.getImgUrl())
                .avatarName(avatar.getName())
                .avatarAge(avatar.getAge())
                .avatarNationality(avatar.getNationality())
                .job(myRole.getJob())
                .situation(myRole.getSituation())
                .build();
    }

    public static MyRoleResponse.MyRoleListDTO toMyRoleListDTO(List<MyRole> myRoles) {
        List<MyRoleResponse.MyRoleDetailDTO> roleDetails = myRoles.stream()
                .map(MyRoleConverter::toMyRoleDetailDTO)
                .collect(Collectors.toList());

        return MyRoleResponse.MyRoleListDTO.builder()
                .roles(roleDetails)
                .totalCount(roleDetails.size())
                .build();
    }
}