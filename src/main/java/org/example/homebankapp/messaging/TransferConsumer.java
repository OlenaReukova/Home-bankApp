package org.example.homebankapp.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class TransferConsumer {
    @RabbitListener(queues = "transfers.pending")
    public void receiveTransfer(String message) {
        System.out.println("Received transfer message: " + message);
    }
}
