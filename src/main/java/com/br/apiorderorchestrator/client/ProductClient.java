package com.br.apiorderorchestrator.client;

import com.br.apiorderorchestrator.dto.ProductOrderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(url = "${products.url}", name = "products-api")
public interface ProductClient {

    @PostMapping("/encumber")
    ResponseEntity<String> encumber(@RequestBody List<ProductOrderDTO> productOrderDTOS);
}
