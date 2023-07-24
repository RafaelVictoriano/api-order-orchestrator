package com.br.apiorderorchestrator.dto;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProductOrderDTO {

    @Min(1)
    private Long productId;
    @Min(1)
    private Integer quantity;
}
