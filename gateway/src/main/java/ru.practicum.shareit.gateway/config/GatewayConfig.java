package ru.practicum.shareit.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Slf4j
@Configuration
public class GatewayConfig {

    @Bean
    public RestTemplate restTemplate() {
        // Создаем RestTemplate с поддержкой буферизации запросов/ответов
        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

        // Добавляем MessageConverters для поддержки JSON
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        // Логируем только основную информацию о запросе
        restTemplate.setInterceptors(Collections.singletonList((request, body, execution) -> {
            log.info("HTTP {} request to {}", request.getMethod(), request.getURI());
            return execution.execute(request, body);
        }));

        return restTemplate;
    }
}