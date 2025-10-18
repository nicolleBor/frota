package com.example.frota.caixa;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CaixaMapper {
    
    // Converte Entity para DTO (para preencher formulário de edição)
    AtualizacaoCaixa toAtualizacaoDto(Caixa caixa);
    
    // Converte DTO para Entity (para criação NOVA - ignora ID)
    @Mapping(target = "id", ignore = true)
    Caixa toEntityFromAtualizacao(AtualizacaoCaixa dto);
    
    // Atualiza Entity existente com dados do DTO
    @Mapping(target = "id", ignore = true) // Não atualiza ID
    void updateEntityFromDto(AtualizacaoCaixa dto, @MappingTarget Caixa caixa);
}