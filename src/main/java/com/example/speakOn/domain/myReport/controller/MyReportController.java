package com.example.speakOn.domain.myReport.controller;

import com.example.speakOn.domain.avatar.enums.SituationType;
import com.example.speakOn.domain.myReport.docs.MyReportControllerDocs;
import com.example.speakOn.domain.myReport.dto.request.MyReportRequest;
import com.example.speakOn.domain.myReport.dto.response.MyReportResponseDTO;
import com.example.speakOn.domain.myReport.service.MyReportService;
import com.example.speakOn.domain.myRole.enums.JobType;
import com.example.speakOn.domain.user.entity.User;
import com.example.speakOn.global.apiPayload.ApiResponse;
import com.example.speakOn.global.util.AuthUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class MyReportController implements MyReportControllerDocs {

    private final MyReportService myReportService;
    private final AuthUtil authUtil;

    @Override
    @GetMapping("")
    public ApiResponse<MyReportResponseDTO.ReportSummaryListDTO> getReportList(
            @RequestParam(name = "job", required = false) JobType job,
            @RequestParam(name = "situation", required = false) SituationType situation) {

        Long userId = authUtil.getCurrentUserId();

        return ApiResponse.onSuccess(myReportService.getReportList(userId, job, situation));
    }

    @Override
    @GetMapping("/{reportId}")
    public ApiResponse<MyReportResponseDTO.ReportDetailDTO> getReportDetail(@PathVariable(name = "reportId") Long reportId) {

        Long userId = authUtil.getCurrentUserId();

        return ApiResponse.onSuccess(myReportService.getReportDetail(reportId, userId));
    }

    @Override
    @GetMapping("/{reportId}/logs")
    public ApiResponse<MyReportResponseDTO.MessageLogListDTO> getConversationLogs(
            @PathVariable(name = "reportId") Long reportId) {

        Long userId = authUtil.getCurrentUserId();

        return ApiResponse.onSuccess(myReportService.getConversationLogs(reportId, userId));
    }

    @Override
    @PatchMapping("/{reportId}/reflection")
    public ApiResponse<MyReportResponseDTO.WriteReflectionResultDTO> writeReflection(
            @PathVariable(name = "reportId") Long reportId,
            @RequestBody @Valid MyReportRequest.WriteReflectionDTO request) {

        Long userId = authUtil.getCurrentUserId();

        return ApiResponse.onSuccess(myReportService.writeReflection(reportId, request, userId));
    }
}