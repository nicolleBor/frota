package com.example.frota.caminhao;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CaminhaoMapper {
    
    // Converte Entity para DTO (para preencher formulário de edição)
    AtualizacaoCaminhao toAtualizacaoDto(Caminhao caminhao);
    
    // Converte DTO para Entity (para criação NOVA - ignora ID)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fatorCubagem", ignore = true) // Ignora fatorCubagem na criação
    Caminhao toEntityFromAtualizacao(AtualizacaoCaminhao dto);
    
    // Atualiza Entity existente com dados do DTO
    @Mapping(target = "id", ignore = true) // Não atualiza ID
    @Mapping(target = "fatorCubagem", ignore = true) // Não atualiza fatorCubagem
    void updateEntityFromDto(AtualizacaoCaminhao dto, @MappingTarget Caminhao caminhao);
}
