package com.example.frota.marca;

import jakarta.validation.constraints.NotNull;

public record DadosAtualizacaoMarca(
		@NotNull
		Long id,
		String nome,
		String pais) {
}
