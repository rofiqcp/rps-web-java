package com.undip.rps.config;

import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class OpenAIConfig {

    @Value("${openai.api.key:}")
    private String apiKey;

    @Value("${openai.timeout:300}")
    private int timeout;

    @Bean
    public OpenAiService openAiService() {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("OpenAI API key not configured. Set OPENAI_API_KEY environment variable.");
        }
        return new OpenAiService(apiKey, Duration.ofSeconds(timeout));
    }
}
