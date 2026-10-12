package org.example.homebankapp.messaging;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class TransferProducerTest {

    @Autowired
    private TransferProducer transferProducer;

    @Test
    void shouldSendTransferMessage() {
        transferProducer.sendTransfer("Transfer requested: 100 GBP");
    }
}