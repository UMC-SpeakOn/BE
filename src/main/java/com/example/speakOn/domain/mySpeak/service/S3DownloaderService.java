package com.example.speakOn.domain.mySpeak.service;

import com.example.speakOn.domain.mySpeak.exception.MySpeakException;
import com.example.speakOn.domain.mySpeak.exception.code.MySpeakErrorCode;
import com.example.speakOn.global.util.S3UrlParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3DownloaderService {

    private final S3Client s3Client;

    public File downloadUrlToTempFile(String s3Url) {
        S3UrlParser parsed = S3UrlParser.parse(s3Url);
        return downloadToTempFile(parsed.getBucket(), parsed.getKey());
    }

    public File downloadToTempFile(String bucket, String key) {
        File temp = null;
        try {
            String suffix = guessSuffixFromKey(key);
            temp = File.createTempFile("s3_audio_", suffix);

            GetObjectRequest req = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            try (ResponseInputStream<GetObjectResponse> is = s3Client.getObject(req);
                 FileOutputStream os = new FileOutputStream(temp)) {
                is.transferTo(os);
            }

            log.info("S3 download success: bucket={}, key={}, temp={}", bucket, key, temp.getAbsolutePath());
            return temp;

        } catch (Exception e) {
            safeDelete(temp);
            log.error("S3 download failed: bucket={}, key={}", bucket, key, e);
            throw new MySpeakException(MySpeakErrorCode.S3_DOWNLOAD_FAILED);
        }
    }

    public void safeDelete(File f) {
        try {
            if (f != null && f.exists()) Files.deleteIfExists(f.toPath());
        } catch (Exception ignored) {}
    }

    private String guessSuffixFromKey(String key) {
        int idx = key.lastIndexOf('.');
        if (idx == -1) return ".bin";
        return key.substring(idx);
    }
}
