package com.example.speakOn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@EntityScan(basePackages = "com.example.speakOn") // ai_coversation_context 테이블 생성을 위한 설정
public class SpeakOnApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpeakOnApplication.class, args);
	}

}
