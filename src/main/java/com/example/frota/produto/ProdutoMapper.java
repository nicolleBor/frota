package com.example.frota.produto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProdutoMapper {

    // Converte Entity para DTO
    AtualizacaoProduto toAtualizacaoDto(Produto produto);

    // Converte DTO para Entity (criação nova)
    @Mapping(target = "id", ignore = true)
    Produto toEntityFromAtualizacao(AtualizacaoProduto dto);

    // Atualiza Entity existente com dados do DTO
    @Mapping(target = "id", ignore = true) // ID não é atualizado
    void updateEntityFromDto(AtualizacaoProduto dto, @MappingTarget Produto produto);
}
