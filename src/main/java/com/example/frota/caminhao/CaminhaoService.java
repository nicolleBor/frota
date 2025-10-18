package com.example.frota.caminhao;

import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

import com.example.frota.marca.Marca;

@Service
public class CaminhaoService {
	@Autowired
	private CaminhaoRepository caminhaoRepository;
	
	@Autowired
	private CaminhaoMapper caminhaoMapper;

    @Transactional
	public Caminhao salvarOuAtualizar(AtualizacaoCaminhao dto) {
        if (dto.id() != null) {
            // atualizando Busca existente e atualiza
            Caminhao existente = caminhaoRepository.findById(dto.id())
                .orElseThrow(() -> new EntityNotFoundException("Caminhão não encontrado com ID: " + dto.id()));
            caminhaoMapper.updateEntityFromDto(dto, existente);
            return caminhaoRepository.save(existente);
        } else {
            // criando Novo caminhão
            Caminhao novoCaminhao = caminhaoMapper.toEntityFromAtualizacao(dto);
            return caminhaoRepository.save(novoCaminhao);
        }
    }
	
	public List<Caminhao> procurarTodos(){
		return caminhaoRepository.findAll(Sort.by("modelo").ascending());
	}

	@Transactional
    public void apagarPorId (Long id) {
		caminhaoRepository.deleteById(id);
	}
	
	public Optional<Caminhao> procurarPorId(Long id) {
	    return caminhaoRepository.findById(id);
	}
	
	public long contarTotal() {
	    return caminhaoRepository.count();
	}
}
