package com.example.speakOn.global.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "cors") // yml에서 "cors"로 시작하는 설정을 가져옴
public class CorsProperties {
    private List<String> allowedOrigins;
}
