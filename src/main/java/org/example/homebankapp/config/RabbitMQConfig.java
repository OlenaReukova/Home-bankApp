package org.example.homebankapp.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue transferQueue() {
        return new Queue("transfers.pending", true);
    }
}
