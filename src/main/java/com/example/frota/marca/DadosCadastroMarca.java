package com.example.frota.marca;

import jakarta.validation.constraints.NotBlank;

public record DadosCadastroMarca(	
		@NotBlank
		String nome,
		String pais) {
		

}
