package com.example.speakOn.domain.myReport.docs;

import com.example.speakOn.domain.avatar.enums.SituationType;
import com.example.speakOn.domain.myReport.dto.request.MyReportRequest;
import com.example.speakOn.domain.myReport.dto.response.MyReportResponseDTO;
import com.example.speakOn.domain.myRole.enums.JobType;
import com.example.speakOn.global.apiPayload.ApiResponse;
import com.example.speakOn.global.apiPayload.code.status.ErrorStatus;
import com.example.speakOn.domain.myReport.code.MyReportErrorCode;
import com.example.speakOn.global.validation.annotation.ApiErrorCodeExample;
import com.example.speakOn.global.validation.annotation.ApiErrorCodeExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Tag(name = "My Report API", description = "학습 결과 리포트 관련 API입니다.")
public interface MyReportControllerDocs {

    @Operation(summary = "리포트 목록 조회 API", description = "사용자의 전체 리포트 목록을 조회합니다. 직무별, 상황별, 기간별 필터링이 가능합니다.")
    @Parameters({
            @Parameter(name = "job", description = "직무 필터 (MARKETING, DEVELOPMENT 등)"),
            @Parameter(name = "situation", description = "상황 필터 (INTERVIEW, MEETING 등)"),
            @Parameter(name = "startDate", description = "조회 시작일 (yyyy-MM-dd)"),
            @Parameter(name = "endDate", description = "조회 종료일 (yyyy-MM-dd)"),
            @Parameter(name = "page", description = "페이지 번호 (0부터 시작)"),
            @Parameter(name = "size", description = "한 페이지당 개수")
    })
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "REPORT4003", description = "잘못된 직무 또는 상황 필터 조건입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH4001", description = "인증되지 않은 사용자입니다.")
    })
    ApiResponse<MyReportResponseDTO.ReportSummaryListDTO> getReportList(
            @RequestParam(name = "job", required = false) JobType job,
            @RequestParam(name = "situation", required = false) SituationType situation,
            @RequestParam(name = "startDate", required = false) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) LocalDate endDate,
            Pageable pageable);

    @Operation(summary = "리포트 상세 조회 API", description = "특정 리포트의 상세 데이터(AI 분석, 소감, 대화 로그)를 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "REPORT4041", description = "해당 ID의 리포트를 찾을 수 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "REPORT4031", description = "본인의 리포트만 조회할 수 있습니다.")
    })
    ApiResponse<MyReportResponseDTO.ReportDetailDTO> getReportDetail(@PathVariable(name = "reportId") Long reportId);

    @Operation(summary = "리포트 대화 로그 상세 조회 API", description = "특정 리포트의 전체 대화 내용을 시간순으로 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "REPORT4041", description = "존재하지 않는 리포트입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "REPORT4031", description = "해당 리포트에 접근할 권한이 없습니다.")
    })
    ApiResponse<MyReportResponseDTO.MessageLogListDTO> getConversationLogs(@PathVariable(name = "reportId") Long reportId);

    @Operation(summary = "사용자 소감 작성 및 난이도 수정 API", description = "리포트 상세 조회 후, 사용자가 소감을 작성하고 난이도를 수정할 때 사용합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "REPORT4041", description = "해당 ID의 리포트를 찾을 수 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "REPORT4031", description = "본인의 리포트만 수정할 수 있습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "REPORT4001", description = "사용자 소감은 최대 120자까지만 입력 가능합니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "REPORT4002", description = "소감 내용이 비어 있습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "REPORT4009", description = "난이도는 1점에서 5점 사이의 정수만 입력 가능합니다.")
    })
    ApiResponse<MyReportResponseDTO.WriteReflectionResultDTO> writeReflection(
            @PathVariable(name = "reportId") Long reportId,
            @RequestBody MyReportRequest.WriteReflectionDTO request
    );
}