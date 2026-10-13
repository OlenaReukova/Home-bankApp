package org.example.homebankapp.messaging;

import org.apache.camel.ProducerTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class TransferConsumer {

    private final ProducerTemplate producerTemplate;
    private final ObjectMapper objectMapper;

    public TransferConsumer(ProducerTemplate producerTemplate, ObjectMapper objectMapper) {
        this.producerTemplate = producerTemplate;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "transfers.pending")
    public void receiveTransfer(String message) throws Exception {

        TransferMessage transferMessage = objectMapper.readValue(
                message,
                TransferMessage.class
        );

        producerTemplate.sendBody("direct:transfer", transferMessage);
    }
}
