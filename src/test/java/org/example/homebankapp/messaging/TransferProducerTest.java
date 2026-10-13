package org.example.homebankapp.messaging;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

@SpringBootTest
@ActiveProfiles("test")
class TransferProducerTest {

    @Autowired
    private TransferProducer transferProducer;

    @Test
    void shouldSendTransferMessage() {
        TransferMessage message = new TransferMessage(
                "ACC-001",
                "ACC-002",
                new BigDecimal("100.00")
        );
        transferProducer.sendTransfer(message);
    }
}