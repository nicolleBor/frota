package com.example.frota.produto;

import jakarta.validation.constraints.*;

public record AtualizacaoProduto(
    Long id,
    
    @NotBlank(message = "Nome é obrigatório")
    String nome,

    @NotNull(message = "Peso é obrigatório")
    @DecimalMin(value = "0.1", message = "Peso mínimo 0.1 kg")
    @DecimalMax(value = "1000.0", message = "Peso máximo 1000 kg")
    Double peso,

    @NotNull(message = "Comprimento é obrigatório")
    @DecimalMin(value = "0.01", message = "Comprimento mínimo 0.01 metros")
    @DecimalMax(value = "5.0", message = "Comprimento máximo 5 metros")
    Double comprimento,

    @NotNull(message = "Largura é obrigatória")
    @DecimalMin(value = "0.01", message = "Largura mínima 0.01 metros")
    @DecimalMax(value = "3.0", message = "Largura máxima 3 metros")
    Double largura,

    @NotNull(message = "Altura é obrigatória")
    @DecimalMin(value = "0.01", message = "Altura mínima 0.01 metros")
    @DecimalMax(value = "3.0", message = "Altura máxima 3 metros")
    Double altura
) {}