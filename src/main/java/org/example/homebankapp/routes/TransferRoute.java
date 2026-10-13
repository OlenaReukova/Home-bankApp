package org.example.homebankapp.routes;

import org.apache.camel.builder.RouteBuilder;
import org.example.homebankapp.messaging.TransferMessage;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TransferRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("direct:transfer")
                .routeId("transfer-route")
                .log("Camel received transfer: ${body}")
                .wireTap("direct:transferAudit")
                .choice()
                .when(exchange -> {
                    TransferMessage message =
                            exchange.getMessage().getBody(TransferMessage.class);

                    boolean isSmall = message.amount()
                            .compareTo(new BigDecimal("1000")) < 0;

                    return isSmall;
                })
                .to("direct:smallTransfer")
                .otherwise()
                .to("direct:largeTransfer")
                .end();
        from("direct:smallTransfer")
                .routeId("small-transfer-route")
                .log("Small transfer: aotomatic processing - ${body} GBP");

        from("direct:largeTransfer")
                .routeId("large-transfer-route")
                .log("Large transfer: manual review required- ${body} GBP");

        from("direct:transferAudit")
                .routeId("transfer-audit-route")
                .log("AUDIT: transfer received - ${body}");
    }
}
