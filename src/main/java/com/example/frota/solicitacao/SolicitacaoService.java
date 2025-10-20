package com.example.frota.solicitacao;

import java.util.List;
import java.util.Optional;

import com.example.frota.caixa.Caixa;
import com.example.frota.caminhao.Caminhao;
import com.example.frota.produto.Produto;
import com.example.frota.api.externo.FreteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

import com.example.frota.produto.ProdutoRepository;
import com.example.frota.caixa.CaixaRepository;
import com.example.frota.caminhao.CaminhaoRepository;

@Service
public class SolicitacaoService {

    @Autowired
    private SolicitacaoRepository solicitacaoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private CaixaRepository caixaRepository;

    @Autowired
    private CaminhaoRepository caminhaoRepository;

    @Autowired
    private SolicitacaoMapper mapper;

    @Autowired
    private FreteService freteService;

    @Autowired
    private ValidacaoService validacaoService;

    @Transactional
    public SolicitacaoTransporte salvarOuAtualizar(AtualizacaoSolicitacao dto) {
        Produto produto = null;
        Caixa caixa = null;
        Caminhao caminhao = null;

        if (dto.produtoId() != null)
            produto = produtoRepository.findById(dto.produtoId())
                    .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado com ID: " + dto.produtoId()));

        if (dto.caixaId() != null)
            caixa = caixaRepository.findById(dto.caixaId())
                    .orElseThrow(() -> new EntityNotFoundException("Caixa não encontrada com ID: " + dto.caixaId()));

        if (dto.caminhaoId() != null)
            caminhao = caminhaoRepository.findById(dto.caminhaoId())
                    .orElseThrow(() -> new EntityNotFoundException("Caminhão não encontrado com ID: " + dto.caminhaoId()));

        if (dto.id() != null) {
            SolicitacaoTransporte existente = solicitacaoRepository.findById(dto.id())
                    .orElseThrow(() -> new EntityNotFoundException("Solicitação não encontrada com ID: " + dto.id()));

            existente.atualizarInformacoes(dto, produto, caixa, caminhao);

            if (!existente.produtoCabeNaCaixa())
                throw new IllegalArgumentException("Produto não cabe na caixa selecionada");

            // Recalcular frete se origem ou destino mudaram
            if (dto.origem() != null || dto.destino() != null) {
                calcularFreteParaSolicitacao(existente);
            }

            return solicitacaoRepository.save(existente);

        } else {
            SolicitacaoTransporte nova = new SolicitacaoTransporte(
                new CadastroSolicitacao(
                    dto.caminhaoId(),
                    dto.produtoId(),
                    dto.caixaId(),
                    dto.quantidade(),
                    dto.origem(),
                    dto.destino(),
                    dto.observacoes()
                ),
                produto, caixa, caminhao
            );

            if (!nova.produtoCabeNaCaixa())
                throw new IllegalArgumentException("Produto não cabe na caixa selecionada");

            // Calcular frete automaticamente
            calcularFreteParaSolicitacao(nova);

            return solicitacaoRepository.save(nova);
        }
    }

    private void calcularFreteParaSolicitacao(SolicitacaoTransporte solicitacao) {
        try {
            // Determinar tipo de cálculo baseado no produto
            String tipoCalculo = determinarTipoCalculo(solicitacao);
            
            // Calcular peso cobrado usando a lógica de cubagem
            double pesoCobrado = validacaoService.determinarPesoCobrado(solicitacao.getProduto());
            
            // Calcular volume do produto
            double volumeProduto = validacaoService.calcularVolumeProduto(solicitacao.getProduto());
            
            // Calcular frete usando o serviço externo com peso real e volume
            var resultadoFrete = freteService.calcularFrete(
                solicitacao.getOrigem(),
                solicitacao.getDestino(),
                solicitacao.getProduto().getPeso(),
                volumeProduto,
                tipoCalculo
            );

            // Aplicar os resultados na solicitação
            double distanciaKm = (Double) resultadoFrete.get("distanciaKm");
            double valorPedagio = (Double) resultadoFrete.get("pedagio");
            double valorTotal = (Double) resultadoFrete.get("valorTotal");

            // Usar o valor total calculado pelo FreteService (que já considera cubagem)
            solicitacao.setDistanciaKm(distanciaKm);
            solicitacao.setValorKm(0.0); // Não usado no novo sistema
            solicitacao.setValorPedagio(valorPedagio);
            solicitacao.setValorFrete(valorTotal);
            
            // Definir informações de cubagem
            double pesoReal = solicitacao.getProduto().getPeso();
            double pesoCubado = validacaoService.calcularPesoCubado(solicitacao.getProduto());
            solicitacao.setPesoCobrado(pesoCobrado);
            solicitacao.setPesoCubado(pesoCubado);
            solicitacao.setPesoCubadoMaior(pesoCubado > pesoReal);

        } catch (Exception e) {
            // Fallback: usar valores padrão mais realistas
            solicitacao.setDistanciaKm(100.0);
            solicitacao.setValorKm(0.0); // Não usado no novo sistema
            solicitacao.setValorPedagio(25.0);
            solicitacao.setValorFrete(50.0); // Valor mais realista para fallback
        }
    }

    private String determinarTipoCalculo(SolicitacaoTransporte solicitacao) {
        double peso = solicitacao.getProduto().getPeso();
        double volume = solicitacao.getProduto().getVolume();
        
        // Lógica para determinar o tipo de cálculo
        if (peso > 100) {
            return "peso"; // Produtos pesados
        } else if (volume > 0.5) {
            return "volume"; // Produtos volumosos
        } else {
            return "caixa"; // Produtos leves e pequenos
        }
    }

    public List<SolicitacaoTransporte> procurarTodos() {
        return solicitacaoRepository.findAll();
    }

    public Optional<SolicitacaoTransporte> procurarPorId(Long id) {
        return solicitacaoRepository.findById(id);
    }

    @Transactional
    public void apagarPorId(Long id) {
        solicitacaoRepository.deleteById(id);
    }
    
    public long contarTotal() {
        return solicitacaoRepository.count();
    }
}
