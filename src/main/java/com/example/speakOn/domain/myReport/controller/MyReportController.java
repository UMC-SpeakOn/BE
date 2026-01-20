package com.example.speakOn.domain.myReport.controller;

import com.example.speakOn.domain.myReport.docs.MyReportControllerDocs;
import com.example.speakOn.domain.myReport.dto.response.MyReportResponseDTO;
import com.example.speakOn.domain.myReport.service.MyReportService;
import com.example.speakOn.domain.avatar.enums.SituationType;
import com.example.speakOn.domain.myRole.enums.JobType;
import com.example.speakOn.domain.user.entity.User;
import com.example.speakOn.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.lang.reflect.Field; // ID 주입을 위한 라이브러리

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class MyReportController implements MyReportControllerDocs {

    private final MyReportService myReportService;

    // 임시 유저 생성을 위한 공통 메서드 (팀장님 가이드 반영)
    private User getTempUser() {
        User user = User.builder()
                .socialId("temp_123")
                .name("임시유저")
                .email("test@test.com")
                .build();

        // Reflection을 이용해 ID 강제 주입 (BaseEntity의 ID가 private이므로)
        try {
            Field field = user.getClass().getSuperclass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(user, 1L);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    @GetMapping("")
    public ApiResponse<MyReportResponseDTO.ReportSummaryListDTO> getReportList(
            @RequestParam(name = "job", required = false) JobType job,
            @RequestParam(name = "situation", required = false) SituationType situation) {

        return ApiResponse.onSuccess(myReportService.getReportList(getTempUser(), job, situation));
    }

    @Override
    @GetMapping("/{reportId}")
    public ApiResponse<MyReportResponseDTO.ReportDetailDTO> getReportDetail(@PathVariable(name = "reportId") Long reportId) {
        return ApiResponse.onSuccess(myReportService.getReportDetail(reportId, getTempUser()));
    }
}