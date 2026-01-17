package com.example.speakOn.domain.mySpeak.service;

import com.example.speakOn.domain.myRole.entity.MyRole;
import com.example.speakOn.domain.myRole.repository.MyRoleRepository;
import com.example.speakOn.domain.mySpeak.dto.form.MyRoleFormDto;
import com.example.speakOn.domain.mySpeak.dto.request.CreateSessionRequest;
import com.example.speakOn.domain.mySpeak.dto.request.SttRequestDto;
import com.example.speakOn.domain.mySpeak.dto.request.TtsRequestDto;
import com.example.speakOn.domain.mySpeak.dto.response.SttResponseDto;
import com.example.speakOn.domain.mySpeak.dto.response.TtsResponseDto;
import com.example.speakOn.domain.mySpeak.dto.response.WaitScreenResponse;
import com.example.speakOn.domain.mySpeak.entity.ConversationMessage;
import com.example.speakOn.domain.mySpeak.entity.ConversationSession;
import com.example.speakOn.domain.mySpeak.enums.MessageType;
import com.example.speakOn.domain.mySpeak.enums.SenderRole;
import com.example.speakOn.domain.mySpeak.enums.SessionStatus;
import com.example.speakOn.domain.mySpeak.exception.MySpeakException;
import com.example.speakOn.domain.mySpeak.exception.code.MySpeakErrorCode;
import com.example.speakOn.domain.mySpeak.repository.ConversationMessageRepository;
import com.example.speakOn.domain.mySpeak.repository.ConversationSessionRepository;
import com.example.speakOn.domain.mySpeak.repository.MySpeakRepository;
import com.example.speakOn.global.apiPayload.exception.handler.ErrorHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MySpeakService {

    private final SpeechRecognitionService speechRecognitionService;
    private final TextSynthesisService textSynthesisService;
    private final MySpeakRepository mySpeakRepository;
    private final ConversationSessionRepository conversationSessionRepository;
    private final MyRoleRepository myRoleRepository;
    private final ConversationMessageRepository conversationMessageRepository;


    /**
     * 대기화면 데이터 조회
     * 사용자의 모든 MyRole(직무, 상황, AI) 정보 반환
     *
     * @param userId 사용자 ID
     * @return WaitScreenResponse (사용자의 MyRole 목록)
     * @throws ErrorHandler 예외 발생 시
     */
    public WaitScreenResponse getWaitScreenForm(Long userId) {
        try {
            // 사용자 ID 검증
            validateUserId(userId);

            // 사용자의 MyRole 조회
            List<MyRole> myRoles = mySpeakRepository.findAllWithUserAvartar(userId);
            validateMyRoles(myRoles);

            // MyRole → MyRoleFormDto 변환
            MyRoleFormDto myRoleForm = convertToMyRoleForm(myRoles);

            // 통합 응답 반환
            WaitScreenResponse response = new WaitScreenResponse(myRoleForm);

            return response;

        } catch (MySpeakException e) {
            log.warn("MySpeak 도메인 에러: {}", e.getErrorReason().getMessage());
            throw e;
        } catch (Exception e) {
            log.error("대기화면 조회 중 예상치 못한 오류 발생", e);
            throw new MySpeakException(MySpeakErrorCode.WAIT_SCREEN_LOAD_FAILED);
        }
    }

    /**
     * 대화 세션 생성
     * 대기화면에서 '대화 시작하기' 클릭 시 새 세션 생성
     *
     * @param request 세션 생성 요청 (myRoleId, targetQuestionCount, startedAt)
     * @return 생성된 sessionId
     * @throws ErrorHandler MyRole 없음(MS4002), 생성 실패(MS5004)
     */
    @Transactional
    public Long createSession(CreateSessionRequest request) {
        try {
            // MyRole 조회
            MyRole myRole = myRoleRepository.findById(request.getMyRoleId());
            if (myRole == null) {
                log.warn("MyRole not found - myRoleId: {}", request.getMyRoleId());
                throw new MySpeakException(MySpeakErrorCode.NO_MYROLES_AVAILABLE);
            }

            // 세션 생성
            ConversationSession session = ConversationSession.builder()
                    .myRole(myRole)
                    .status(SessionStatus.ONGOING)
                    .targetQuestionCount(request.getTargetQuestionCount())
                    .currentQuestionCount(0)
                    .sentenceCount(0)
                    .startedAt(request.getStartedAt())
                    .build();

            // 저장
            conversationSessionRepository.save(session);
            ConversationSession saved = conversationSessionRepository.findById(session.getId());

            log.info("대화 세션 생성 완료 - sessionId: {}, myRole: {}", saved.getId(), myRole.getJob());

            return saved.getId();

        }catch (MySpeakException e) {
            log.error("MySpeakException 발생 - myRoleId: {}", request.getMyRoleId(), e);
            throw e;
        }catch (Exception e) {
            log.error("대화 세션 생성 실패 - myRoleId: {}", request.getMyRoleId(), e);
            throw new MySpeakException(MySpeakErrorCode.SESSION_CREATION_FAILED);
        }
    }

    /**
     * 음성 파일을 텍스트로 변환(STT)하고 USER 메시지로 저장한다.
     *
     * @param audioFile 업로드된 음성 파일
     * @param request 세션 정보 및 언어 코드
     * @return 변환된 텍스트
     * @throws MySpeakException 세션 없음, 오디오 오류, STT 실패 시
     */
    @Transactional
    public SttResponseDto recognizeSpeech(MultipartFile audioFile, SttRequestDto request) {
        log.info("MySpeak: STT 요청 처리 - sessionId={}", request.getSessionId());

        validateAudioFile(audioFile);

        // STT 변환
        String transcript = speechRecognitionService.recognizeFromFile(audioFile, request.getLanguageCode());

        // 세션 조회
        ConversationSession session = conversationSessionRepository.findById(request.getSessionId());
        if (session == null) {
            throw new MySpeakException(MySpeakErrorCode.SESSION_NOT_FOUND);
        }

        // 사용자 메시지 저장
        ConversationMessage userMessage = ConversationMessage.builder()
                .session(session)
                .senderRole(SenderRole.USER)
                .content(transcript)
                .messageType(request.getMessageType())
                .build();

        conversationMessageRepository.save(userMessage);

        return new SttResponseDto(transcript);
    }

    /**
     * 텍스트를 음성으로 변환(TTS)하고 AI 메시지로 저장한다.
     *
     * @param request TTS 요청 정보
     * @return 생성된 음성 데이터(byte[])
     * @throws MySpeakException 세션 없음, TTS 실패 시
     */
    @Transactional
    public byte[] generateSpeech(TtsRequestDto request) {
        log.info("MySpeak: TTS 요청 처리 - sessionId={}", request.getSessionId());

        // 세션 조회
        ConversationSession session = conversationSessionRepository.findById(request.getSessionId());
        if (session == null) {
            throw new MySpeakException(MySpeakErrorCode.SESSION_NOT_FOUND);
        }

        // TTS 변환
        byte[] audioBytes = textSynthesisService.synthesize(
                request.getText(),
                request.getVoiceName(),
                request.getSpeakingRate()
        );

        //AI 메시지 저장
        ConversationMessage aiMessage = ConversationMessage.builder()
                .session(session)
                .senderRole(SenderRole.AI)
                .content(request.getText())
                .messageType(request.getMessageType())
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
    private void validateAudioFile(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null ||
                !(contentType.equals("audio/mpeg") ||
                        contentType.equals("audio/mp3") ||
                        contentType.equals("audio/wav") ||
                        contentType.equals("audio/webm") ||
                        contentType.equals("audio/ogg") ||
                        contentType.equals("audio/flac"))) {
            throw new MySpeakException(MySpeakErrorCode.INVALID_AUDIO_FORMAT);
        }
    }




    /**
     * MyRole 엔티티를 MyRoleFormDto로 변환
     *
     * @param myRoles MyRole 엔티티 리스트
     * @return MyRoleFormDto (응답 포맷)
     * @throws MySpeakException 변환 실패 시
     */
    private MyRoleFormDto convertToMyRoleForm(List<MyRole> myRoles) {
        log.debug("MyRole 변환 시작 - 개수: {}", myRoles.size());

        try {
            List<MyRoleFormDto.MyRoleDto> roleDtos = myRoles.stream()
                    .map(r -> new MyRoleFormDto.MyRoleDto(r))
                    .collect(Collectors.toList());

            MyRoleFormDto result = new MyRoleFormDto(roleDtos);

            return result;

        } catch (Exception e) {
            log.error("MyRole 변환 중 오류", e);
            throw new MySpeakException(MySpeakErrorCode.MYROLE_CONVERSION_FAILED);
        }
    }

    /**
     * 사용자 ID 검증
     *
     * @param userId 검증할 사용자 ID
     * @throws ErrorHandler userId가 null이거나 0 이하인 경우
     */
    private void validateUserId(Long userId) {
        if (userId == null || userId <= 0) {
            log.warn("유효하지 않은 사용자 ID: {}", userId);
            throw new MySpeakException(MySpeakErrorCode.INVALID_USER_ID);
        }
    }

    /**
     * MyRole 리스트 검증
     *
     * @param myRoles 검증할 MyRole 리스트
     * @throws ErrorHandler MyRole이 없는 경우
     */
    private void validateMyRoles(List<MyRole> myRoles) {
        if (myRoles == null || myRoles.isEmpty()) {
            log.warn("이용가능한 역할(MyRole)이 없습니다");
            throw new MySpeakException(MySpeakErrorCode.NO_MYROLES_AVAILABLE);
        }
    }

}
