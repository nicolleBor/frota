package com.example.frota.caminhao;

import jakarta.validation.constraints.*;

public record CadastroCaminhao(
		@NotBlank(message = "Modelo é obrigatório")
		String modelo,
		
		@NotBlank(message = "Placa é obrigatória")
		String placa,
		
		@NotNull(message = "Ano é obrigatório")
		@Min(value = 2000, message = "Ano deve ser a partir de 2000")
		Integer ano,
		
        @NotNull(message = "Comprimento é obrigatório")
        @DecimalMin(value = "4.0", message = "Comprimento mínimo 4 metros")
        @DecimalMax(value = "20.0", message = "Comprimento máximo 20 metros")
        Double comprimento,
        
        @NotNull(message = "Largura é obrigatória")
        @DecimalMin(value = "2.0", message = "Largura mínima 2 metros")
        @DecimalMax(value = "2.6", message = "Largura máxima 2.6 metros")
        Double largura,
        
        @NotNull(message = "Altura é obrigatória")
        @DecimalMin(value = "2.5", message = "Altura mínima 2.5 metros")
        @DecimalMax(value = "4.5", message = "Altura máxima 4.5 metros")
        Double altura
	) {}

