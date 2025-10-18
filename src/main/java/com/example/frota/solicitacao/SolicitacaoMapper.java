package com.example.frota.solicitacao;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SolicitacaoMapper {
    
    // Converte Entity para DTO (para preencher formulário de edição)
    AtualizacaoSolicitacao toAtualizacaoDto(SolicitacaoTransporte solicitacao);
    
    // Converte DTO para Entity (para criação NOVA - ignora ID)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "valorFrete", ignore = true)
    @Mapping(target = "pesoCubadoMaior", ignore = true)
    SolicitacaoTransporte toEntityFromAtualizacao(AtualizacaoSolicitacao dto);
    
    // Atualiza Entity existente com dados do DTO
    @Mapping(target = "id", ignore = true) // Não atualiza ID
    @Mapping(target = "valorFrete", ignore = true) // Não atualiza valorFrete
    @Mapping(target = "pesoCubadoMaior", ignore = true) // Não atualiza pesoCubadoMaior
    void updateEntityFromDto(AtualizacaoSolicitacao dto, @MappingTarget SolicitacaoTransporte solicitacao);
}