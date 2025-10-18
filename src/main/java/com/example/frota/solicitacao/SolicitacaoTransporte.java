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

    private Integer quantidade;
    private String origem;
    private String destino;
    private String observacoes;
    private String status;
    private Double distanciaKm;
    private Double valorFrete;
    private Double pesoCobrado;
    private Double pesoCubado;
    private Double valorKm;
    private Double valorPedagio;
    private boolean pesoCubadoMaior; // indica se o peso cobrado foi cubado ou real

    // 🔹 Construtor a partir do DTO
    public SolicitacaoTransporte(CadastroSolicitacao dados, Produto produto, Caixa caixa, Caminhao caminhao) {
        this.produto = produto;
        this.caixa = caixa;
        this.caminhao = caminhao;
        this.quantidade = dados.quantidade();
        this.origem = dados.origem();
        this.destino = dados.destino();
        this.observacoes = dados.observacoes();
        this.status = "PENDENTE";
    }

    // 🔹 Atualização incremental
    public void atualizarInformacoes(AtualizacaoSolicitacao dados, Produto produto, Caixa caixa, Caminhao caminhao) {
        if (produto != null) this.produto = produto;
        if (caixa != null) this.caixa = caixa;
        if (caminhao != null) this.caminhao = caminhao;
        if (dados.quantidade() != null) this.quantidade = dados.quantidade();
        if (dados.origem() != null) this.origem = dados.origem();
        if (dados.destino() != null) this.destino = dados.destino();
        if (dados.observacoes() != null) this.observacoes = dados.observacoes();
        if (dados.status() != null) this.status = dados.status();
    }

    // 🔹 Cálculo do frete considerando peso cubado
    public void calcularFrete(double valorPorKm, double distancia) {
        this.valorKm = valorPorKm;
        this.distanciaKm = distancia;
        
        // Calcula o volume do produto em m³
        double volumeProduto = produto.getVolume();
        
        // Calcula o peso cubado usando o fator de cubagem do caminhão
        this.pesoCubado = caminhao.calcularPesoCubado(volumeProduto);
        
        // Peso real do produto
        double pesoReal = produto.getPeso();
        
        // O valor que determinará o custo do frete será sempre o maior entre peso real e peso cubado
        this.pesoCobrado = Math.max(this.pesoCubado, pesoReal);
        this.pesoCubadoMaior = this.pesoCubado > pesoReal;
        
        // Calcula o frete: peso cobrado × valor por km × distância
        this.valorFrete = this.pesoCobrado * valorPorKm * distancia;
    }

    // 🔹 Verifica se o produto cabe na caixa
    public boolean produtoCabeNaCaixa() {
        return produto.getComprimento() <= caixa.getComprimento() &&
                produto.getLargura() <= caixa.getLargura() &&
                produto.getAltura() <= caixa.getAltura() &&
                produto.getPeso() <= caixa.getLimitePeso();
    }
}
