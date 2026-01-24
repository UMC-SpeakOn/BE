package com.example.speakOn.global.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt") // application.yml의 "jwt" 하위 속성들을 묶어옴
public class JwtProperties {
    private String secretKey;
    private String issuer;
    private Long accessExpiration;
    private Long refreshExpiration;
}
