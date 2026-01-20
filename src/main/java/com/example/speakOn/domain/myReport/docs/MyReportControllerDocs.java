package com.example.speakOn.domain.myReport.docs;

import com.example.speakOn.domain.myReport.dto.response.MyReportResponseDTO;
import com.example.speakOn.domain.myRole.enums.JobType;
import com.example.speakOn.domain.avatar.enums.SituationType;
import com.example.speakOn.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "My Report API", description = "학습 결과 리포트 관련 API입니다.")
public interface MyReportControllerDocs {

    @Operation(summary = "리포트 목록 조회 API", description = "사용자의 전체 리포트 목록을 조회합니다. 직무별, 상황별 필터링이 가능합니다.")
    @Parameters({
            @Parameter(name = "job", description = "직무 필터 (MARKETING, DEVELOPMENT, DESIGN, PLANNING, SALES, BUSINESS)"),
            @Parameter(name = "situation", description = "상황 필터 (INTERVIEW, MEETING, ONE_ON_ONE_MEETING, COFFEE_CHAT)")
    })
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "REPORT4003", description = "잘못된 직무 또는 상황 필터 조건입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH4001", description = "인증되지 않은 사용자입니다.")
    })
    ApiResponse<MyReportResponseDTO.ReportSummaryListDTO> getReportList(
            @RequestParam(name = "job", required = false) JobType job,
            @RequestParam(name = "situation", required = false) SituationType situation);

    @Operation(summary = "리포트 상세 조회 API", description = "특정 리포트의 상세 데이터(AI 분석, 소감, 대화 로그)를 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "REPORT4041", description = "해당 ID의 리포트를 찾을 수 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "REPORT4031", description = "본인의 리포트만 조회할 수 있습니다.")
    })
    ApiResponse<MyReportResponseDTO.ReportDetailDTO> getReportDetail(@PathVariable(name = "reportId") Long reportId);
}