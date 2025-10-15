package com.example.frota.solicitacao;

import jakarta.validation.constraints.*;

public record CadastroSolicitacao(
        @NotNull(message = "ID do produto é obrigatório")
        Long produtoId,

        @NotNull(message = "ID da caixa é obrigatório")
        Long caixaId,

        @NotNull(message = "ID do caminhão é obrigatório")
        Long caminhaoId,

        @NotNull(message = "Distância é obrigatória")
        @Positive(message = "Distância deve ser positiva")
        Double distanciaKm,

        @NotNull(message = "Valor por km é obrigatório")
        @Positive(message = "Valor por km deve ser positivo")
        Double valorKm
) {}
