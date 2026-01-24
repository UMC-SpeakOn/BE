package com.example.speakOn.domain.mySpeak.service;

import com.example.speakOn.domain.mySpeak.dto.request.SttRequestDto;
import com.example.speakOn.domain.mySpeak.exception.MySpeakException;
import com.example.speakOn.domain.mySpeak.exception.code.MySpeakErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3UploaderService {

    private final S3Client s3Client;
    private final String s3BucketName;

    public String uploadAudio(MultipartFile file, SttRequestDto requestDto) {
        try {
            String ext = getExtension(file.getOriginalFilename());
            String key = "audio/" + requestDto.getSessionId() + "/" + UUID.randomUUID() + ext;

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(s3BucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));

            String url = s3Client.utilities()
                    .getUrl(builder -> builder.bucket(s3BucketName).key(key))
                    .toExternalForm();

            log.info("S3 upload success: {}", url);

            return url;

        } catch (S3Exception e) {

            log.error("S3 서비스 오류 - Bucket: {}, errorCode: {}", s3BucketName, e.awsErrorDetails().errorCode(), e);

            throw new MySpeakException(MySpeakErrorCode.S3_BUCKET_ACCESS_DENIED);

        } catch (SdkClientException e) {
            log.error("AWS SDK 오류: {}", e.getMessage(), e);
            throw new MySpeakException(MySpeakErrorCode.S3_UPLOAD_FAILED);

        } catch (Exception e) {
            log.error("S3 업로드 알 수 없는 오류", e);
            throw new MySpeakException(MySpeakErrorCode.S3_UPLOAD_FAILED);
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return ".tmp"; // 그냥 임시 확장자
        }
        return filename.substring(filename.lastIndexOf('.'));
    }
}

