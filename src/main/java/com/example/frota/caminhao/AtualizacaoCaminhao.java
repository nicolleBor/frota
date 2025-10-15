package com.example.frota.caminhao;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AtualizacaoCaminhao(
	    Long id,
	    @NotBlank(message = "Modelo é obrigatório") 
	    String modelo,
	    
	    @NotBlank(message = "Placa é obrigatória")
	    String placa,
	    
	    @NotNull(message = "Ano é obrigatório")
	    @Min(value = 2000, message = "Ano deve ser a partir de 2000")
	    Integer ano,
	    
	    @NotNull(message = "Carga máxima é obrigatória")
	    @Positive(message = "Carga máxima deve ser positiva")
	    Double cargaMaxima,
	    
	    @NotNull(message = "Marca é obrigatória")
	    Long marcaId
	) {}


