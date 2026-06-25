package com.msa.userservice.configs;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Carrega o nome da fila definido no seu application.properties
    // Se não houver nada definido lá, ele adota "ms.email" como padrão
    @Value("${broker.queue.email.name:ms.email}")
    private String queueName;

    @Bean
    public Queue emailQueue() {
        // Cria a fila necessária para o construtor do UserProducer
        return new Queue(queueName, true);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}