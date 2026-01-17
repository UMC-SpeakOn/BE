package com.example.speakOn.domain.mySpeak.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Component
@Slf4j
public class AudioConverter {

    //음성 파일 -> wav 파일로 변환
    public File convertToWav(MultipartFile multipartFile) {
        try {
            File inputFile = File.createTempFile("input-", getExt(multipartFile.getOriginalFilename()));
            File outputFile = File.createTempFile("output-", ".wav");

            multipartFile.transferTo(inputFile);

            ProcessBuilder pb = new ProcessBuilder(
                    "ffmpeg",
                    "-y",
                    "-i", inputFile.getAbsolutePath(),
                    "-ac", "1",
                    "-ar", "16000",
                    outputFile.getAbsolutePath()
            );

            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new RuntimeException("ffmpeg 변환 실패");
            }

            inputFile.delete();

            return outputFile;

        } catch (Exception e) {
            throw new RuntimeException("오디오 변환 실패", e);
        }
    }

    private String getExt(String name) {
        if (name == null || !name.contains(".")) return ".tmp";
        return name.substring(name.lastIndexOf("."));
    }
}
