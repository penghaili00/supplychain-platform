package com.supplychain.mq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Configuration
@ConditionalOnProperty(name = "supplychain.rabbitmq.enabled", havingValue = "true")
public class RabbitQueueConfiguration {

    public static final String DEMO_QUEUE = "supplychain.demo.queue";
    public static final String DEMO_EXCHANGE = "supplychain.demo.exchange";
    public static final String DEMO_ROUTING_KEY = "supplychain.demo.routing";

    @Bean
    public Queue demoQueue() {
        return new Queue(DEMO_QUEUE, true);
    }

    @Bean
    public DirectExchange demoExchange() {
        return new DirectExchange(DEMO_EXCHANGE, true, false);
    }

    @Bean
    public Binding demoBinding(Queue demoQueue, DirectExchange demoExchange) {
        return BindingBuilder.bind(demoQueue).to(demoExchange).with(DEMO_ROUTING_KEY);
    }
}
