package ru.practicum.shareit.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Configuration
public class GatewayConfig {

    @Bean
    public RestTemplate restTemplate() {
        // Создаем RestTemplate с поддержкой буферизации запросов/ответов
        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

        // Добавляем MessageConverters для поддержки JSON
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        // Добавляем логирование HTTP-запросов
        restTemplate.setInterceptors(Collections.singletonList((request, body, execution) -> {
            System.out.println("URI: " + request.getURI());
            System.out.println("Method: " + request.getMethod());
            System.out.println("Headers: " + request.getHeaders());
            System.out.println("Body: " + new String(body, StandardCharsets.UTF_8));
            return execution.execute(request, body);
        }));

        return restTemplate;
    }
}