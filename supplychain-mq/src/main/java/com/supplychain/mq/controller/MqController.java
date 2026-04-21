package com.supplychain.mq.controller;

import com.supplychain.common.core.domain.R;
import com.supplychain.mq.config.RabbitQueueConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MqController {

    private final RabbitTemplate rabbitTemplate;

    @Value("${supplychain.rabbitmq.enabled:false}")
    private boolean rabbitEnabled;

    @PostMapping("/mq/demo/publish")
    public R<String> publish(@RequestParam(defaultValue = "Hello from SupplyChain MQ") String body) {
        if (!rabbitEnabled) {
            return R.fail(400, "RabbitMQ 模块当前未启用，请先设置 supplychain.rabbitmq.enabled=true");
        }
        rabbitTemplate.convertAndSend(RabbitQueueConfiguration.DEMO_EXCHANGE,
                RabbitQueueConfiguration.DEMO_ROUTING_KEY, body);
        return R.ok("消息已发送", body);
    }
}
