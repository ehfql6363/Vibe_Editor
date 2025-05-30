package com.ssafy.vibe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableJpaAuditing
public class VibeEditorProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(VibeEditorProjectApplication.class, args);
	}

}
