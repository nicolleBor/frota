package com.example.frota.solicitacao;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import com.example.frota.produto.Produto;
import com.example.frota.caixa.Caixa;
import com.example.frota.caminhao.Caminhao;

@Mapper(componentModel = "spring")
public interface SolicitacaoMapper {

    // Converte Entity para DTO
    @Mapping(target = "produtoId", source = "produto.id")
    @Mapping(target = "caixaId", source = "caixa.id")
    @Mapping(target = "caminhaoId", source = "caminhao.id")
    AtualizacaoSolicitacao toAtualizacaoDto(SolicitacaoTransporte solicitacao);

    // Converte DTO para Entity (nova criação)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "produto", source = "produtoId", qualifiedByName = "idToProduto")
    @Mapping(target = "caixa", source = "caixaId", qualifiedByName = "idToCaixa")
    @Mapping(target = "caminhao", source = "caminhaoId", qualifiedByName = "idToCaminhao")
    SolicitacaoTransporte toEntityFromAtualizacao(AtualizacaoSolicitacao dto,
                                                  Produto produto,
                                                  Caixa caixa,
                                                  Caminhao caminhao);

    // Atualiza Entity existente
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "produto", source = "produtoId", qualifiedByName = "idToProduto")
    @Mapping(target = "caixa", source = "caixaId", qualifiedByName = "idToCaixa")
    @Mapping(target = "caminhao", source = "caminhaoId", qualifiedByName = "idToCaminhao")
    void updateEntityFromDto(AtualizacaoSolicitacao dto,
                             @MappingTarget SolicitacaoTransporte solicitacao,
                             Produto produto,
                             Caixa caixa,
                             Caminhao caminhao);

    // Métodos auxiliares para criar entidades a partir de IDs
    @Named("idToProduto")
    default Produto idToProduto(Long id) {
        if (id == null) return null;
        Produto p = new Produto();
        p.setId(id);
        return p;
    }

    @Named("idToCaixa")
    default Caixa idToCaixa(Long id) {
        if (id == null) return null;
        Caixa c = new Caixa();
        c.setId(id);
        return c;
    }

    @Named("idToCaminhao")
    default Caminhao idToCaminhao(Long id) {
        if (id == null) return null;
        Caminhao cam = new Caminhao();
        cam.setId(id);
        return cam;
    }
}
