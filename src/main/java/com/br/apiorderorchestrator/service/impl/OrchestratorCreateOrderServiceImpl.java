package com.br.apiorderorchestrator.service.impl;

import com.br.apiorderorchestrator.dto.OrderRequestDTO;
import com.br.apiorderorchestrator.service.EncumberService;
import com.br.apiorderorchestrator.service.OrchestratorCreateOrderService;
import com.br.apiorderorchestrator.service.ProducerMessageService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class OrchestratorCreateOrderServiceImpl implements OrchestratorCreateOrderService {

    @Autowired
    private EncumberService encumberService;

    @Autowired
    private ProducerMessageService producerMessageService;

    @Override
    public void start(OrderRequestDTO orderRequestDTO) {
        log.info("Starting order creation orchestration flow");
        encumberService.start(orderRequestDTO.getProducts());
        producerMessageService.start(orderRequestDTO);
        log.info("Finishing order creation orchestration flow");
    }
}
