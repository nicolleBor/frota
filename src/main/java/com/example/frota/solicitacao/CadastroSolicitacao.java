package com.example.frota.solicitacao;

import jakarta.validation.constraints.*;

public record CadastroSolicitacao(
    @NotNull(message = "Caminhão é obrigatório")
    Long caminhaoId,

    @NotNull(message = "Produto é obrigatório")
    Long produtoId,

    @NotNull(message = "Caixa é obrigatória")
    Long caixaId,

    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade mínima 1")
    @Max(value = 1000, message = "Quantidade máxima 1000")
    Integer quantidade,

    @NotBlank(message = "Origem é obrigatória")
    String origem,

    @NotBlank(message = "Destino é obrigatório")
    String destino,

    String observacoes
) {}