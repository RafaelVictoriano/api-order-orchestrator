package com.br.apiorderorchestrator.service.impl;

import com.br.apiorderorchestrator.service.ProducerMessageService;
import com.br.apiorderorchestrator.util.JsonUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class ProducerMessageServiceImpl implements ProducerMessageService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${topic.create-order.name}")
    private String topicName;

    @Override
    public void start(Object message) {
        log.info("Sending message to topic, message{}", message);
        kafkaTemplate.send(topicName,JsonUtil.toJson(message));
    }
}
