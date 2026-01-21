package com.example.speakOn.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@Slf4j
public class S3Config {

    @Value("${cloud.aws.credentials.access-key:${AWS_ACCESS_KEY}}")
    private String accessKeyId;

    @Value("${cloud.aws.credentials.secret-key:${AWS_SECRET_KEY}}")
    private String secretAccessKey;

    @Value("${cloud.aws.region.static:ap-northeast-2}")
    private String region;

    @Value("${cloud.aws.s3.bucket:myspeak-audio-storage}")
    private String bucketName;

    @Bean
    public S3Client s3Client() {
        // 우선순위: 환경변수 > yml > 기본값
        String keyId = getNonBlank(accessKeyId, System.getenv("AWS_ACCESS_KEY"));
        String secretKey = getNonBlank(secretAccessKey, System.getenv("AWS_SECRET_KEY"));

        if (keyId == null || secretKey == null) {
            throw new IllegalStateException("AWS S3 credentials required. Set AWS_ACCESS_KEY/AWS_SECRET_KEY or cloud.aws.credentials.*");
        }

        Region awsRegion = Region.of(region);
        AwsBasicCredentials credentials = AwsBasicCredentials.create(keyId, secretKey);

        log.info("✅ S3Client initialized - Region: {}, Bucket: {}", awsRegion, bucketName);

        return S3Client.builder()
                .region(awsRegion)
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    // S3 버킷 헬퍼 (Controller에서 사용)
    @Bean
    public String s3BucketName() {
        return bucketName;
    }

    private String getNonBlank(String... candidates) {
        for (String candidate : candidates) {
            if (candidate != null && !candidate.trim().isEmpty()) {
                return candidate.trim();
            }
        }
        return null;
    }
}
