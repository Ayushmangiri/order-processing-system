package com.cashinvoice.camel;

import com.cashinvoice.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FileToQueueRoute extends RouteBuilder {

    @Value("${file.input.path}")
    private String inputPath;

    @Value("${file.error.path}")
    private String errorPath;

    @Value("${activemq.queue.name}")
    private String queueName;

    @Override
    public void configure() throws Exception {

        onException(Exception.class)
                .handled(true)
                .log("Error processing file: ${exception.message}")
                .to("file:" + errorPath)
                .end();

        from("file:" + inputPath + "?noop=true&delay=5000")
                .routeId("file-to-activemq-route")
                .log("Processing file: ${header.CamelFileName}")

                .unmarshal().json(JsonLibrary.Jackson, Order.class)

                .process(exchange -> {
                    Order order = exchange.getIn().getBody(Order.class);

                    log.info("Validating order | OrderId={} | CustomerId={}",
                            order.getOrderId(), order.getCustomerId());

                    if (order.getOrderId() == null) {
                        throw new IllegalArgumentException("Order ID cannot be null");
                    }
                    if (order.getCustomerId() == null) {
                        throw new IllegalArgumentException("Customer ID cannot be null");
                    }
                    if (order.getAmount() == null || order.getAmount() <= 0) {
                        throw new IllegalArgumentException("Amount must be greater than 0");
                    }
                })

                .marshal().json(JsonLibrary.Jackson)

                .to("activemq:queue:" + queueName)

                .log("File successfully sent to queue: ${header.CamelFileName}")
                .end();
    }
}