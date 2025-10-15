package com.example.frota.caixa;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CaixaMapper {

    // Converte Entity para DTO
    AtualizacaoCaixa toAtualizacaoDto(Caixa caixa);

    // Converte DTO para Entity (criação nova)
    @Mapping(target = "id", ignore = true)
    Caixa toEntityFromAtualizacao(AtualizacaoCaixa dto);

    // Atualiza Entity existente com dados do DTO
    @Mapping(target = "id", ignore = true) // ID não é atualizado
    void updateEntityFromDto(AtualizacaoCaixa dto, @MappingTarget Caixa caixa);
}
