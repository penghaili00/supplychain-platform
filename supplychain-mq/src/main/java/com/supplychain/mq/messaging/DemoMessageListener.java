package com.supplychain.mq.messaging;

import com.supplychain.mq.config.RabbitQueueConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(name = "supplychain.rabbitmq.enabled", havingValue = "true")
public class DemoMessageListener {

    @RabbitListener(queues = RabbitQueueConfiguration.DEMO_QUEUE)
    public void consume(String payload) {
        log.info("consume rabbit message: {}", payload);
    }
}
