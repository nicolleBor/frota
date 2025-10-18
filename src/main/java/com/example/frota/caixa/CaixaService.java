package com.example.frota.caixa;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CaixaService {

    @Autowired
    private CaixaRepository caixaRepository;

    @Autowired
    private CaixaMapper caixaMapper;

    @Transactional
    public Caixa salvarOuAtualizar(AtualizacaoCaixa dto) {
        if (dto.id() != null) {
            Caixa existente = caixaRepository.findById(dto.id())
                    .orElseThrow(() -> new EntityNotFoundException("Caixa não encontrada com ID: " + dto.id()));
            existente.atualizarInformacoes(dto);
            return caixaRepository.save(existente);
        } else {
            Caixa nova = new Caixa();
            nova.atualizarInformacoes(dto);
            return caixaRepository.save(nova);
        }
    }

    public List<Caixa> procurarTodos() {
        return caixaRepository.findAll();
    }

    public Optional<Caixa> procurarPorId(Long id) {
        return caixaRepository.findById(id);
    }

    @Transactional
    public void apagarPorId(Long id) {
        caixaRepository.deleteById(id);
    }
    
    public long contarTotal() {
        return caixaRepository.count();
    }
}
