package com.example.frota.produto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProdutoMapper {
    
    // Converte Entity para DTO (para preencher formulário de edição)
    AtualizacaoProduto toAtualizacaoDto(Produto produto);
    
    // Converte DTO para Entity (para criação NOVA - ignora ID)
    @Mapping(target = "id", ignore = true)
    Produto toEntityFromAtualizacao(AtualizacaoProduto dto);
    
    // Atualiza Entity existente com dados do DTO
    @Mapping(target = "id", ignore = true) // Não atualiza ID
    void updateEntityFromDto(AtualizacaoProduto dto, @MappingTarget Produto produto);
}