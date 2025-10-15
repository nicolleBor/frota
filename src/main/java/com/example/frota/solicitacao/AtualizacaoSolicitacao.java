package com.example.frota.solicitacao;

import jakarta.validation.constraints.*;

public record AtualizacaoSolicitacao(
        Long id,
        Long produtoId,
        Long caixaId,
        Long caminhaoId,
        @Positive(message = "Distância deve ser positiva") Double distanciaKm,
        @Positive(message = "Valor por km deve ser positivo") Double valorKm
) {}
