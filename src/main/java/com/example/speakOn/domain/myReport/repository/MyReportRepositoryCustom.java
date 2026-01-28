package com.example.speakOn.domain.myReport.repository;

import com.example.speakOn.domain.avatar.enums.SituationType;
import com.example.speakOn.domain.myReport.entity.MyReport;
import com.example.speakOn.domain.myRole.enums.JobType;
import com.example.speakOn.domain.user.entity.User;

import java.util.List;

public interface MyReportRepositoryCustom {
    // 필터링 적용하여 리포트 목록을 조회
    List<MyReport> findAllByUserAndFilters(User user, JobType job, SituationType situation);
}