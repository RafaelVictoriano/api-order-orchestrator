package com.br.apiorderorchestrator.service;

import com.br.apiorderorchestrator.dto.ProductOrderDTO;

import java.util.List;

public interface EncumberService {

    void start(List<ProductOrderDTO> productOrderDTO);

}
