package com.example.speakOn.global.ai.converter;

import com.example.speakOn.global.ai.exception.AiErrorCode;
import com.example.speakOn.global.apiPayload.exception.GeneralException;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.*;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.InvalidResultSetAccessException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
public class AiErrorConverter {

    public AiErrorCode convert(Throwable t) {
        Throwable cause = unwrap(t);

        // 이미 정의된 GeneralException(예: AI_STYLE_NOT_FOUND)은 그대로 반환
        if (cause instanceof GeneralException ge && ge.getCode() instanceof AiErrorCode) {
            return (AiErrorCode) ge.getCode();
        }

        AiErrorCode errorCode = mapToErrorCode(cause);

        if (errorCode == AiErrorCode.AI_DATA_NOT_FOUND) {
            log.warn("[AI_DATA_MISSING] {}", cause.getMessage());
        } else if (errorCode.name().contains("DB")) {
            log.error("[AI_DB_FAIL] Class: {}, Code: {}", cause.getClass().getSimpleName(), errorCode);
        } else {
            log.error("[AI_FAIL] Code: {}, Msg: {}", errorCode, cause.getMessage());
        }

        return errorCode;
    }

    private Throwable unwrap(Throwable t) {
        if (t instanceof ExecutionException || t instanceof CompletionException) {
            return t.getCause() != null ? t.getCause() : t;
        }
        return t;
    }

    private AiErrorCode mapToErrorCode(Throwable t) {
        // [1] Data Not Found (404)
        if (t instanceof EntityNotFoundException ||
                t instanceof NoSuchElementException ||
                t instanceof EmptyResultDataAccessException) {
            return AiErrorCode.AI_DATA_NOT_FOUND;
        }

        // [2] Network & Timeout (504, 503)
        if (t instanceof SocketTimeoutException || t instanceof TimeoutException) {
            return AiErrorCode.AI_RESPONSE_TIMEOUT;
        }
        if (t instanceof ConnectException || t instanceof ResourceAccessException || t instanceof RestClientException) {
            return AiErrorCode.AI_CONNECTION_ERROR;
        }

        // [3] Database Infrastructure (503, 500)
        if (t instanceof DataAccessException) {
            if (t instanceof DataAccessResourceFailureException ||
                    (t.getMessage() != null && t.getMessage().contains("Connection"))) {
                return AiErrorCode.AI_DB_CONNECTION_FAIL;
            }
            if (t instanceof PessimisticLockingFailureException || t instanceof CannotAcquireLockException) {
                return AiErrorCode.AI_DB_DEADLOCK;
            }
            if (t instanceof QueryTimeoutException) {
                return AiErrorCode.AI_DB_TRANSACTION_TIMEOUT;
            }
            if (t instanceof BadSqlGrammarException || t instanceof InvalidResultSetAccessException) {
                return AiErrorCode.AI_DB_SCHEMA_ERROR;
            }
            return AiErrorCode.AI_DB_PROCESSING_ERROR;
        }

        // [4] Internal Logic (400, 500)
        if (t instanceof IllegalArgumentException) return AiErrorCode.AI_INVALID_REQUEST;
        if (t instanceof JsonProcessingException) return AiErrorCode.AI_PARSE_ERROR;

        // [5] LLM Provider Specifics
        String msg = t.getMessage() != null ? t.getMessage().toLowerCase() : "";
        if (msg.contains("401") || msg.contains("api_key")) return AiErrorCode.AI_INVALID_API_KEY;
        if (msg.contains("429") || msg.contains("quota")) return AiErrorCode.AI_QUOTA_EXCEEDED;
        if (msg.contains("policy") || msg.contains("safety")) return AiErrorCode.AI_CONTENT_POLICY_VIOLATION;
        if (msg.contains("context_length")) return AiErrorCode.AI_PROMPT_TOO_LONG;
        if (msg.contains("empty response")) return AiErrorCode.AI_NO_RESPONSE;

        return AiErrorCode.AI_UNKNOWN_ERROR;
    }
}