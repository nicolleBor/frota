package com.example.frota.caixa;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "caixa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Caixa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "caixa_id")
    private Long id;

    private String nome;
    private String material;
    private double comprimento; // metros
    private double largura;     // metros
    private double altura;      // metros
    private double limitePeso;  // kg

    // 🔹 Construtor a partir do DTO de cadastro
    public Caixa(CadastroCaixa dados) {
        this.nome = dados.nome();
        this.material = dados.material();
        this.comprimento = dados.comprimento();
        this.largura = dados.largura();
        this.altura = dados.altura();
        this.limitePeso = dados.limitePeso();
    }

    // 🔹 Atualiza os campos a partir do DTO de atualização
    public void atualizarInformacoes(AtualizacaoCaixa dados) {
        if (dados.nome() != null)
            this.nome = dados.nome();
        if (dados.material() != null)
            this.material = dados.material();
        if (dados.comprimento() != null && dados.comprimento() > 0)
            this.comprimento = dados.comprimento();
        if (dados.largura() != null && dados.largura() > 0)
            this.largura = dados.largura();
        if (dados.altura() != null && dados.altura() > 0)
            this.altura = dados.altura();
        if (dados.limitePeso() != null && dados.limitePeso() > 0)
            this.limitePeso = dados.limitePeso();
    }

    // 🔹 Volume da caixa (m³)
    @Transient
    public double getVolume() {
        return comprimento * largura * altura;
    }
}
