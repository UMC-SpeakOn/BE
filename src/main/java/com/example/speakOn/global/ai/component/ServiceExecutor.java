package com.example.speakOn.global.ai.component;

import com.example.speakOn.global.ai.converter.AiErrorConverter;
import com.example.speakOn.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ServiceExecutor {

    private final AiErrorConverter aiErrorConverter;

    @FunctionalInterface
    public interface CheckedSupplier<T> {
        T get() throws Exception;
    }

    public <T> T executeSafe(CheckedSupplier<T> action) {
        try {
            return action.get();
        } catch (GeneralException e) {
            throw e;
        } catch (Exception e) {
            throw new GeneralException(aiErrorConverter.convert(e));
        }
    }
}