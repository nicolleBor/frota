package com.example.frota.solicitacao;

import java.util.List;
import java.util.Optional;

import com.example.frota.caixa.Caixa;
import com.example.frota.caminhao.Caminhao;
import com.example.frota.produto.Produto;
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

            mapper.updateEntityFromDto(dto, existente, produto, caixa, caminhao);

            if (!existente.produtoCabeNaCaixa())
                throw new IllegalArgumentException("Produto não cabe na caixa selecionada");

            return solicitacaoRepository.save(existente);

        } else {
            SolicitacaoTransporte nova = mapper.toEntityFromAtualizacao(dto, produto, caixa, caminhao);

            if (!nova.produtoCabeNaCaixa())
                throw new IllegalArgumentException("Produto não cabe na caixa selecionada");

            return solicitacaoRepository.save(nova);
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
}
