package com.example.frota.produto;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ProdutoMapper produtoMapper;

    @Transactional
    public Produto salvarOuAtualizar(AtualizacaoProduto dto) {
        if (dto.id() != null) {
            Produto existente = produtoRepository.findById(dto.id())
                    .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado com ID: " + dto.id()));
            produtoMapper.updateEntityFromDto(dto, existente);
            return produtoRepository.save(existente);
        } else {
            Produto novo = produtoMapper.toEntityFromAtualizacao(dto);
            return produtoRepository.save(novo);
        }
    }

    public List<Produto> procurarTodos() {
        return produtoRepository.findAll();
    }

    public Optional<Produto> procurarPorId(Long id) {
        return produtoRepository.findById(id);
    }

    @Transactional
    public void apagarPorId(Long id) {
        produtoRepository.deleteById(id);
    }
}
