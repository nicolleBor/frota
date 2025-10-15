package com.example.frota.caixa;

import jakarta.validation.constraints.*;

public record AtualizacaoCaixa(
        Long id,

        String nome,

        String material,

        @DecimalMin(value = "0.1", message = "Comprimento deve ser positivo")
        Double comprimento,

        @DecimalMin(value = "0.1", message = "Largura deve ser positiva")
        Double largura,

        @DecimalMin(value = "0.1", message = "Altura deve ser positiva")
        Double altura,

        @Positive(message = "Limite de peso deve ser positivo")
        Double limitePeso
) {}
