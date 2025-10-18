package com.example.frota.caixa;

import jakarta.validation.constraints.*;

public record CadastroCaixa(
    @NotBlank(message = "Nome é obrigatório")
    String nome,

    @NotBlank(message = "Material é obrigatório")
    String material,

    @NotNull(message = "Comprimento é obrigatório")
    @DecimalMin(value = "0.1", message = "Comprimento mínimo 0.1 metros")
    @DecimalMax(value = "5.0", message = "Comprimento máximo 5 metros")
    Double comprimento,

    @NotNull(message = "Largura é obrigatória")
    @DecimalMin(value = "0.1", message = "Largura mínima 0.1 metros")
    @DecimalMax(value = "3.0", message = "Largura máxima 3 metros")
    Double largura,

    @NotNull(message = "Altura é obrigatória")
    @DecimalMin(value = "0.1", message = "Altura mínima 0.1 metros")
    @DecimalMax(value = "3.0", message = "Altura máxima 3 metros")
    Double altura,

    @NotNull(message = "Limite de peso é obrigatório")
    @DecimalMin(value = "1.0", message = "Limite de peso mínimo 1 kg")
    @DecimalMax(value = "1000.0", message = "Limite de peso máximo 1000 kg")
    Double limitePeso
) {}