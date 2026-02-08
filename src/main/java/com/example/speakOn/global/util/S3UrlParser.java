package com.example.speakOn.global.util;

import lombok.Getter;

import java.net.URI;

@Getter
public class S3UrlParser {
    private final String bucket;
    private final String key;

    public S3UrlParser(String bucket, String key) {
        this.bucket = bucket;
        this.key = key;
    }

    public static S3UrlParser parse(String s3Url) {
        try {
            URI uri = URI.create(s3Url);
            String host = uri.getHost();
            String path = uri.getPath();

            if (host == null || path == null) {
                throw new IllegalArgumentException("Invalid S3 URL");
            }

            String[] hostParts = host.split("\\.");
            if (hostParts.length < 3) {
                throw new IllegalArgumentException("Invalid S3 host: " + host);
            }
            String bucket = hostParts[0];

            String key = path.startsWith("/") ? path.substring(1) : path;
            if (key.isBlank()) {
                throw new IllegalArgumentException("Empty key");
            }

            return new S3UrlParser(bucket, key);

        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot parse S3 URL: " + s3Url, e);
        }
    }
}
