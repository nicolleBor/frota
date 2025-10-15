package com.example.frota.produto;

import jakarta.validation.constraints.*;

public record AtualizacaoProduto(
        Long id,
        String nome,
        @Positive(message = "Peso deve ser positivo") Double peso,
        @DecimalMin(value = "0.01", message = "Comprimento deve ser positivo") Double comprimento,
        @DecimalMin(value = "0.01", message = "Largura deve ser positiva") Double largura,
        @DecimalMin(value = "0.01", message = "Altura deve ser positiva") Double altura
) {}
