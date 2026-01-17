package com.example.speakOn.global.config;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechSettings;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.TextToSpeechSettings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Google Cloud STT / TTS 설정 클래스
 *
 * 역할:
 * 1. 환경변수로부터 Google 서비스 계정 JSON 키 파일 경로를 읽는다.
 * 2. 해당 키 파일로 Credentials 객체를 생성한다.
 * 3. 이 Credentials를 이용해 STT, TTS Client를 Bean으로 등록한다.
 *
 * 이렇게 하면:
 * - 앱 시작 시 딱 한 번만 인증 설정 로드
 * - STT / TTS 전역에서 공통으로 사용
 * - 나중에 키 교체, 클라우드 변경도 여기만 수정하면 됨
 */
@Slf4j
@Configuration
public class GoogleCloudConfig {

    /**
     * application.yml 에서 읽어오는 값
     *
     * google:
     *   cloud:
     *     credentials:
     *       path: ${GOOGLE_CREDENTIALS_PATH}
     */
    @Value("${google.cloud.credentials.path}")
    private String credentialsPath;

    /**
     * Google 서비스 계정 인증 정보(Credentials) Bean
     *
     * - 실제 json 키 파일을 읽어서 Credentials 객체로 변환
     * - STT, TTS 클라이언트가 공통으로 사용
     */
    @Bean
    public Credentials googleCredentials() throws IOException {
        log.info("Google Credentials 로딩 시작");
        log.info("Credentials JSON 경로: {}", credentialsPath);

        Credentials credentials = GoogleCredentials.fromStream(
                new FileInputStream(credentialsPath)
        );

        log.info("Google Credentials 로딩 완료");
        return credentials;
    }

    /**
     * Google STT (Speech-to-Text) 클라이언트 Bean
     */
    @Bean
    public SpeechClient speechClient(Credentials credentials) throws IOException {
        log.info("Google SpeechClient(STT) 생성 시작");

        SpeechSettings settings = SpeechSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build();

        SpeechClient client = SpeechClient.create(settings);

        log.info("Google SpeechClient(STT) 생성 완료");
        return client;
    }

    /**
     * Google TTS (Text-to-Speech) 클라이언트 Bean
     */
    @Bean
    public TextToSpeechClient textToSpeechClient(Credentials credentials) throws IOException {
        log.info("Google TextToSpeechClient(TTS) 생성 시작");

        TextToSpeechSettings settings = TextToSpeechSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build();

        TextToSpeechClient client = TextToSpeechClient.create(settings);

        log.info("Google TextToSpeechClient(TTS) 생성 완료");
        return client;
    }
}

