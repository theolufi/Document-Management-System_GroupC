package com.example.demo.document.konfiguration;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue documentQueue() {
        return new Queue("documentQueue", false);
    }
}