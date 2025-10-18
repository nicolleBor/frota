package com.example.frota.caminhao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "caminhao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of ="id")
public class Caminhao {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "caminhao_id")
	private Long id;
	private String modelo;
	private String placa;
	private int ano;
    
    // Dimensões físicas do caminhão (em metros)
    private double comprimento;
    private double largura;
    private double altura;
    
    // Fator de cubagem para transporte rodoviário: 300 kg/m³
    @Column(name = "fator_cubagem")
    private double fatorCubagem = 300.0;
    
    // Atributo derivado para metragem cúbica
    @Transient
    public double getMetragemCubica() {
        return comprimento * largura * altura;
    }
    
    // Método para calcular peso cubado de um produto
    @Transient
    public double calcularPesoCubado(double volumeProduto) {
        return volumeProduto * fatorCubagem;
    }


	public Caminhao(CadastroCaminhao dados) {
		this.modelo = dados.modelo();
		this.placa = dados.placa();
		this.ano = dados.ano();
        this.comprimento = dados.comprimento();
        this.largura = dados.largura();
        this.altura = dados.altura();
        this.fatorCubagem = 300.0; // Valor padrão fixo
	}
	
	public void atualizarInformacoes(AtualizacaoCaminhao dados) {
		if (dados.modelo() != null )
			this.modelo = dados.modelo();
		if (dados.placa() != null)
			this.placa = dados.placa();
		if (dados.ano() != 0)
			this.ano = dados.ano();
        if (dados.comprimento() != 0)
            this.comprimento = dados.comprimento();
        if (dados.largura() != 0)
            this.largura = dados.largura();
        if (dados.altura() != 0)
            this.altura = dados.altura();
        // fatorCubagem não é alterado - mantém o valor padrão de 300.0
	}
	
}
