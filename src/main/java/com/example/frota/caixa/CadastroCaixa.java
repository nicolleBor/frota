package com.example.frota.caixa;

import jakarta.validation.constraints.*;

public record CadastroCaixa(
        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @NotBlank(message = "Material é obrigatório")
        String material,

        @NotNull(message = "Comprimento é obrigatório")
        @DecimalMin(value = "0.1", message = "Comprimento deve ser positivo")
        Double comprimento,

        @NotNull(message = "Largura é obrigatória")
        @DecimalMin(value = "0.1", message = "Largura deve ser positiva")
        Double largura,

        @NotNull(message = "Altura é obrigatória")
        @DecimalMin(value = "0.1", message = "Altura deve ser positiva")
        Double altura,

        @NotNull(message = "Limite de peso é obrigatório")
        @Positive(message = "Limite de peso deve ser positivo")
        Double limitePeso
) {}
