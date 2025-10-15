package com.example.frota.produto;

import jakarta.validation.constraints.*;

public record CadastroProduto(
        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @NotNull(message = "Peso é obrigatório")
        @Positive(message = "Peso deve ser positivo")
        Double peso,

        @NotNull(message = "Comprimento é obrigatório")
        @DecimalMin(value = "0.01", message = "Comprimento deve ser positivo")
        Double comprimento,

        @NotNull(message = "Largura é obrigatória")
        @DecimalMin(value = "0.01", message = "Largura deve ser positiva")
        Double largura,

        @NotNull(message = "Altura é obrigatória")
        @DecimalMin(value = "0.01", message = "Altura deve ser positiva")
        Double altura
) {}
