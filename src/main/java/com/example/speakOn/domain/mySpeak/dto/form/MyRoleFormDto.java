package com.example.speakOn.domain.mySpeak.dto.form;

import java.util.List;

import com.example.speakOn.domain.avatar.enums.SituationType;
import com.example.speakOn.domain.myRole.entity.MyRole;
import com.example.speakOn.domain.myRole.enums.JobType;
import lombok.*;

@Data
public class MyRoleFormDto {
    private List<MyRoleDto> availablsRoles;

    public MyRoleFormDto(List<MyRoleDto> availablsRoles) {
        this.availablsRoles = availablsRoles;
    }

    @Data
    public static class MyRoleDto {
        private Long id;
        private JobType job;
        private SituationType situation;
        private String name;
        private String imgUrl;

        public MyRoleDto(MyRole role) {
            this.id = role.getId();
            this.job = role.getJob();
            this.situation = role.getSituation();
            this.name = role.getAvatar().getName();
            this.imgUrl = role.getAvatar().getImgUrl();
        }
    }
}

