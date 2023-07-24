package com.br.apiorderorchestrator.controller;

import com.br.apiorderorchestrator.dto.OrderRequestDTO;
import com.br.apiorderorchestrator.service.OrchestratorCreateOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("v1/order")
public class OrderOrchestratorController {

    @Autowired
    private OrchestratorCreateOrderService orchestratorCreateOrderService;

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping
    private void createOrder(@RequestBody OrderRequestDTO body) {
        orchestratorCreateOrderService.start(body);
    }

}
