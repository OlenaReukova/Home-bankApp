package org.example.homebankapp.routes;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.example.homebankapp.messaging.TransferMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class TransferRouteTest {
    private CamelContext context;
    private ProducerTemplate producerTemplate;

    @BeforeEach
    void setUp() throws Exception {
        context = new DefaultCamelContext();

        context.addRoutes(new TransferRoute());

        AdviceWith.adviceWith(context, "transfer-route", advice -> {

            advice.interceptSendToEndpoint("direct:smallTransfer")
                    .skipSendToOriginalEndpoint()
                    .to("mock:smallTransfer");

            advice.interceptSendToEndpoint("direct:largeTransfer")
                    .skipSendToOriginalEndpoint()
                    .to("mock:largeTransfer");
        });

        context.start();

        producerTemplate = context.createProducerTemplate();
        producerTemplate.start();
    }

    @AfterEach
    void tearDown() throws Exception {
        if (producerTemplate != null) {
            producerTemplate.stop();
        }
        if (context != null) {
            context.stop();
        }
    }

    @Test
    void shouldRouteSmallTransfer() throws Exception {
        MockEndpoint small = context.getEndpoint(
                "mock:smallTransfer", MockEndpoint.class
        );

        MockEndpoint large = context.getEndpoint(
                "mock:largeTransfer", MockEndpoint.class
        );

        small.expectedMessageCount(1);
        large.expectedMessageCount(0);

        TransferMessage message = new TransferMessage(
                "ACC-001",
                "ACC-002",
                new BigDecimal("250.00")
        );

        producerTemplate.sendBody("direct:transfer", message);

        MockEndpoint.assertIsSatisfied(context);
    }

    @Test
    void shouldRouteLargeTransfer() throws Exception {
        MockEndpoint small = context.getEndpoint(
                "mock:smallTransfer", MockEndpoint.class
        );

        MockEndpoint large = context.getEndpoint(
                "mock:largeTransfer", MockEndpoint.class
        );

        small.expectedMessageCount(0);
        large.expectedMessageCount(1);

        TransferMessage message = new TransferMessage(
                "ACC-001",
                "ACC-002",
                new BigDecimal("5000.00")
        );

        producerTemplate.sendBody("direct:transfer", message);

        MockEndpoint.assertIsSatisfied(context);
    }

    @Test
    void shouldRouteTransferOfExactly1000ToLarge() throws Exception {

        MockEndpoint small = context.getEndpoint(
                "mock:smallTransfer", MockEndpoint.class
        );

        MockEndpoint large = context.getEndpoint(
                "mock:largeTransfer", MockEndpoint.class
        );

        small.expectedMessageCount(0);
        large.expectedMessageCount(1);

        TransferMessage message = new TransferMessage(
                "ACC-001",
                "ACC-002",
                new BigDecimal("1000.00")
        );

        producerTemplate.sendBody("direct:transfer", message);

        MockEndpoint.assertIsSatisfied(context);
    }
}