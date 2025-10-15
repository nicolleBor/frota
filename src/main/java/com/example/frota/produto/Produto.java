package com.example.frota.produto;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "produto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "produto_id")
    private Long id;

    private String nome;
    private double peso; // kg
    private double comprimento; // metros
    private double largura;     // metros
    private double altura;      // metros

    // 🔹 Construtor a partir do DTO de cadastro
    public Produto(CadastroProduto dados) {
        this.nome = dados.nome();
        this.peso = dados.peso();
        this.comprimento = dados.comprimento();
        this.largura = dados.largura();
        this.altura = dados.altura();
    }

    // 🔹 Atualiza campos a partir do DTO de atualização
    public void atualizarInformacoes(AtualizacaoProduto dados) {
        if (dados.nome() != null)
            this.nome = dados.nome();
        if (dados.peso() != null && dados.peso() > 0)
            this.peso = dados.peso();
        if (dados.comprimento() != null && dados.comprimento() > 0)
            this.comprimento = dados.comprimento();
        if (dados.largura() != null && dados.largura() > 0)
            this.largura = dados.largura();
        if (dados.altura() != null && dados.altura() > 0)
            this.altura = dados.altura();
    }

    // 🔹 Volume do produto (m³)
    @Transient
    public double getVolume() {
        return comprimento * largura * altura;
    }
}
