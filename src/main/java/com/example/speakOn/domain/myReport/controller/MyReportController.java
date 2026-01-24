package com.example.speakOn.domain.myReport.controller;

import com.example.speakOn.domain.myReport.docs.MyReportControllerDocs;
import com.example.speakOn.domain.myReport.dto.response.MyReportResponseDTO;
import com.example.speakOn.domain.myReport.service.MyReportService;
import com.example.speakOn.domain.avatar.enums.SituationType;
import com.example.speakOn.domain.myRole.enums.JobType;
import com.example.speakOn.domain.user.entity.User;
import com.example.speakOn.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.lang.reflect.Field;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class MyReportController implements MyReportControllerDocs {

    private final MyReportService myReportService;

    // FIXME: [임시 코드] Security 도입 전 테스트를 위한 임시 유저 생성 메서드입니다. Auth 구현 후 삭제 예정.
    private User getTempUser() {
        User user = User.builder()
                .socialId("0004")
                .name("김서희")
                .email("SH1@test.com")
                .build();

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

    @Override
    @GetMapping("/{reportId}/logs")
    public ApiResponse<MyReportResponseDTO.MessageLogListDTO> getConversationLogs(
            @PathVariable(name = "reportId") Long reportId,
            User user) {

        return ApiResponse.onSuccess(myReportService.getConversationLogs(reportId, getTempUser()));
    }
}