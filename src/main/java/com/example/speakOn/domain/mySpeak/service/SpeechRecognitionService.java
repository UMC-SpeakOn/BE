package com.example.speakOn.domain.mySpeak.service;

import com.example.speakOn.domain.mySpeak.converter.AudioConverter;
import com.example.speakOn.domain.mySpeak.exception.MySpeakException;
import com.example.speakOn.domain.mySpeak.exception.code.MySpeakErrorCode;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.protobuf.ByteString;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpeechRecognitionService {

    private final SpeechClient speechClient;
    private final AudioConverter audioConverter;

    public String recognizeFromFile(MultipartFile file, String languageCode) {
        File wavFile = null;

        try {
            wavFile = audioConverter.convertToWav(file);
            byte[] bytes = Files.readAllBytes(wavFile.toPath());

            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setSampleRateHertz(16000)
                    .setLanguageCode(languageCode)
                    .setEnableAutomaticPunctuation(true)
                    .build();

            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(ByteString.copyFrom(bytes))
                    .build();

            RecognizeResponse response = speechClient.recognize(config, audio);

            if (response.getResultsList().isEmpty()) {
                return "";
            }

            var result = response.getResultsList().get(0);
            if (result.getAlternativesCount() == 0) {
                return "";
            }
            return result.getAlternatives(0).getTranscript();

        } catch (Exception e) {
            log.error("STT 실패", e);
            throw new MySpeakException(MySpeakErrorCode.STT_RECOGNITION_FAILED);
        } finally {
            if (wavFile != null && wavFile.exists()) {
                wavFile.delete();
            }
        }
    }
}

