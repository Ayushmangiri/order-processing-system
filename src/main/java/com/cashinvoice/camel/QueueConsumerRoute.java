package com.cashinvoice.camel;

import com.cashinvoice.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class QueueConsumerRoute extends RouteBuilder {

    @Value("${activemq.queue.name}")
    private String queueName;

    @Override
    public void configure() throws Exception {

        from("activemq:queue:" + queueName)
                .routeId("activemq-consumer-route")
                .log("Message received from queue")

                .unmarshal().json(JsonLibrary.Jackson, Order.class)

                .process(exchange -> {
                    Order order = exchange.getIn().getBody(Order.class);

                    log.info("Order processed | OrderId={} | CustomerId={} | Amount={}",
                            order.getOrderId(),
                            order.getCustomerId(),
                            order.getAmount());
                })

                .log("Order processing completed successfully")
                .end();
    }
}