package com.br.apiorderorchestrator.service;

import com.br.apiorderorchestrator.dto.OrderRequestDTO;

public interface OrchestratorCreateOrderService {

    void start(OrderRequestDTO orderRequestDTO);

}
