package com.example.frota.solicitacao;

import com.example.frota.caixa.Caixa;
import com.example.frota.caminhao.Caminhao;
import com.example.frota.produto.Produto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "solicitacao_transporte")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class SolicitacaoTransporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "solicitacao_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", referencedColumnName = "produto_id")
    private Produto produto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caixa_id", referencedColumnName = "caixa_id")
    private Caixa caixa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caminhao_id", referencedColumnName = "caminhao_id")
    private Caminhao caminhao;

    private double distanciaKm;
    private double valorFrete;
    private boolean pesoCubadoMaior; // indica se o peso cobrado foi cubado ou real

    // 🔹 Construtor a partir do DTO
    public SolicitacaoTransporte(CadastroSolicitacao dados, Produto produto, Caixa caixa, Caminhao caminhao) {
        this.produto = produto;
        this.caixa = caixa;
        this.caminhao = caminhao;
        this.distanciaKm = dados.distanciaKm();

        calcularFrete(dados.valorKm());
    }

    // 🔹 Atualização incremental
    public void atualizarInformacoes(AtualizacaoSolicitacao dados, Produto produto, Caixa caixa, Caminhao caminhao) {
        if (produto != null) this.produto = produto;
        if (caixa != null) this.caixa = caixa;
        if (caminhao != null) this.caminhao = caminhao;
        if (dados.distanciaKm() != 0) this.distanciaKm = dados.distanciaKm();

        if (dados.valorKm() != 0) calcularFrete(dados.valorKm());
    }

    // 🔹 Cálculo do frete considerando peso cubado
    private void calcularFrete(double valorPorKm) {
        double pesoCubado = produto.getVolume() * caminhao.getFatorCubagem(); // kg
        double pesoReal = produto.getPeso(); // kg

        double pesoCobrado = Math.max(pesoCubado, pesoReal);
        this.pesoCubadoMaior = pesoCubado > pesoReal;

        this.valorFrete = pesoCobrado * valorPorKm * distanciaKm;
    }

    // 🔹 Verifica se o produto cabe na caixa
    public boolean produtoCabeNaCaixa() {
        return produto.getComprimento() <= caixa.getComprimento() &&
                produto.getLargura() <= caixa.getLargura() &&
                produto.getAltura() <= caixa.getAltura() &&
                produto.getPeso() <= caixa.getLimitePeso();
    }
}
