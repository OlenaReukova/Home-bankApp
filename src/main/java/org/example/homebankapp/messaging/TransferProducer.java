package org.example.homebankapp.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class TransferProducer {

    private final RabbitTemplate rabbitTemplate;

    public TransferProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendTransfer(String message) {
        rabbitTemplate.convertAndSend("transfers.pending", message);
    }
}
