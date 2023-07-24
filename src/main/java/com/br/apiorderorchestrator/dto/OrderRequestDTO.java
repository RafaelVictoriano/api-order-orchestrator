package com.br.apiorderorchestrator.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequestDTO {

    @NotNull
    @Min(1)
    private Long clientId;

    @Valid
    @NotNull
    private List<@Valid ProductOrderDTO> products;
}
