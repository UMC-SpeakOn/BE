package com.example.speakOn.domain.mySpeak.service;

import com.example.speakOn.domain.avatar.entity.Avatar;
import com.example.speakOn.domain.mySpeak.entity.ConversationMessage;
import com.example.speakOn.domain.mySpeak.entity.ConversationSession;
import com.example.speakOn.domain.mySpeak.enums.MessageType;
import com.example.speakOn.domain.mySpeak.enums.SenderRole;
import com.example.speakOn.domain.mySpeak.exception.MySpeakException;
import com.example.speakOn.domain.mySpeak.exception.code.MySpeakErrorCode;
import com.example.speakOn.domain.mySpeak.repository.ConversationMessageRepository;
import com.example.speakOn.domain.mySpeak.repository.MySpeakRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConversationTurnService {
    private final S3UploaderService s3UploaderService;
    private final ConversationMessageRepository conversationMessageRepository;
    private final SpeechRecognitionService speechRecognitionService;
    private final TextSynthesisService textSynthesisService;
    private final MySpeakRepository mySpeakRepository;


    // STT 공용로직
    String sttAndSaveUserMessage(
            MultipartFile audioFile,
            ConversationSession session,
            String languageCode,
            MessageType messageType
    ) {

        validateAudioFile(audioFile);

        //S3 업로드
        String audioUrl = s3UploaderService.uploadAudio(audioFile, session.getId());

        // STT 변환
        String transcript = speechRecognitionService.recognizeFromFile(audioFile, languageCode);

        // 사용자 메시지 저장
        ConversationMessage userMessage = ConversationMessage.builder()
                .session(session)
                .senderRole(SenderRole.USER)
                .content(transcript)
                .audioUrl(audioUrl)
                .messageType(messageType)
                .build();

        conversationMessageRepository.save(userMessage);

        //질문 카운트 증가
        if (messageType == MessageType.MAIN) {
            session.incrementQuestionCount();
        }

        return transcript;
    }

    // TTS 공용로직
    byte[] ttsAndSaveAiMessage(
            ConversationSession session,
            String text,
            MessageType messageType,
            String voicename,
            Double speakingRate
    ) {

        //TTS 변환
        byte[] audioBytes = textSynthesisService.synthesize(
                text,
                voicename,
                speakingRate
        );


        //AI 메시지 저장
        ConversationMessage aiMessage = ConversationMessage.builder()
                .session(session)
                .senderRole(SenderRole.AI)
                .content(text)
                .messageType(messageType)
                .build();

        conversationMessageRepository.save(aiMessage);

        return audioBytes;
    }

    /**
     * 업로드된 음성 파일 형식을 검증한다.
     *
     * @param file 업로드된 파일
     * @throws MySpeakException 비어있거나 지원하지 않는 포맷일 경우
     */
    void validateAudioFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new MySpeakException(MySpeakErrorCode.INVALID_AUDIO_FORMAT);
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !filename.matches("(?i).*\\.(m4a|wav|mp3|mp4|webm|ogg|flac|aac)$")) {
            throw new MySpeakException(MySpeakErrorCode.INVALID_AUDIO_FORMAT);
        }
    }
}
