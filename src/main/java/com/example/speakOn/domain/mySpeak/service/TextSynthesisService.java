package com.example.speakOn.domain.mySpeak.service;

import com.example.speakOn.domain.mySpeak.exception.MySpeakException;
import com.example.speakOn.domain.mySpeak.exception.code.MySpeakErrorCode;
import com.google.cloud.texttospeech.v1.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TextSynthesisService {

    private final TextToSpeechClient textToSpeechClient;

    public byte[] synthesize(String text, String voiceName, Double speakingRate) {
        try {
            SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();

            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode("en-US")
                    .setName(voiceName)
                    .build();

            AudioConfig config = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.MP3)
                    .setSpeakingRate(speakingRate)
                    .build();

            SynthesizeSpeechResponse response =
                    textToSpeechClient.synthesizeSpeech(input, voice, config);

            return response.getAudioContent().toByteArray();

        } catch (Exception e) {
            log.error("TTS 실패", e);
            throw new MySpeakException(MySpeakErrorCode.TTS_SYNTHESIS_FAILED);
        }
    }
}
