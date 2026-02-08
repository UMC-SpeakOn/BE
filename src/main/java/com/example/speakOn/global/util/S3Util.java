package com.example.speakOn.global.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3Util {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket:myspeak-audio-storage}")
    private String bucketName;

    @Value("${cloud.aws.region.static:ap-northeast-2}")
    private String region;

    /**
     * 파일을 S3에 업로드합니다.
     *
     * @param file 업로드할 파일
     * @param directory 저장할 디렉토리
     * @return S3 URL
     */
    public String uploadFile(MultipartFile file, String directory) {
        try {
            // 파일명 생성 (UUID + 원본 파일명)
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            String key = directory + "/" + fileName;

            // S3에 파일 업로드
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            PutObjectResponse response = s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            log.info("S3 업로드 성공 - key: {}, etag: {}", key, response.eTag());

            // S3 URL 생성
            return buildS3Url(key);

        } catch (IOException e) {
            log.error("S3 파일 업로드 실패", e);
            throw new RuntimeException("파일 업로드에 실패했습니다.", e);
        }
    }

    /**
     * S3에서 파일을 삭제합니다.
     */
    public void deleteFile(String fileUrl) {
        try {
            // URL에서 key 추출
            String key = extractKeyFromUrl(fileUrl);

            if (key == null || key.isEmpty()) {
                log.warn("삭제할 파일의 key를 추출할 수 없습니다. - url: {}", fileUrl);
                return;
            }

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            log.info("S3 파일 삭제 성공 - key: {}", key);

        } catch (Exception e) {
            log.error("S3 파일 삭제 실패 - url: {}", fileUrl, e);
        }
    }

    /**
     * S3 URL을 생성합니다.
     *
     * @param key S3 객체 키
     * @return S3 URL
     */
    private String buildS3Url(String key) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, key);
    }

    /**
     * S3 URL에서 key를 추출합니다.
     *
     * @param fileUrl S3 URL
     * @return S3 객체 키
     */
    private String extractKeyFromUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return null;
        }

        String prefix = String.format("https://%s.s3.%s.amazonaws.com/", bucketName, region);
        if (fileUrl.startsWith(prefix)) {
            return fileUrl.substring(prefix.length());
        }

        return null;
    }
}
