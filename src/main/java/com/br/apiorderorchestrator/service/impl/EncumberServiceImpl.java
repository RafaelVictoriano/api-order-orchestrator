package com.br.apiorderorchestrator.service.impl;

import com.br.apiorderorchestrator.client.ProductClient;
import com.br.apiorderorchestrator.dto.ProductOrderDTO;
import com.br.apiorderorchestrator.exceptions.BusinessException;
import com.br.apiorderorchestrator.service.EncumberService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@Log4j2
@Service
public class EncumberServiceImpl implements EncumberService {

    @Autowired
    private ProductClient productClient;

    @Override
    public void start(List<ProductOrderDTO> productOrderDTO) {
        final var response = productClient.encumber(productOrderDTO);

       switch (response.getStatusCode().value()) {
            case 200 -> log.info("Success in encumbering product, responseBody:{} ", response.getBody());
            case 404 -> throw new BusinessException(UNPROCESSABLE_ENTITY, "Produtos não encontrados");
            case 422 -> throw new BusinessException(UNPROCESSABLE_ENTITY, "O Produto não tem a quatidade desejada em estoque");
            default -> throw new BusinessException(INTERNAL_SERVER_ERROR, "Problemas ao se integrar com parceiros internos, tente novamente mais tarde");
        };
    }
}
