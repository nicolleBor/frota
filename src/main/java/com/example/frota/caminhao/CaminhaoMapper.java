package com.example.frota.caminhao;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import com.example.frota.marca.Marca;


@Mapper(componentModel = "spring")
public interface CaminhaoMapper {
    
    // Converte Entity para DTO (para preencher formulário de edição)
    @Mapping(target = "marcaId", source = "marca.id")
    AtualizacaoCaminhao toAtualizacaoDto(Caminhao caminhao);
    
    // Converte DTO para Entity (para criação NOVA - ignora ID)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "marca", source = "marcaId", qualifiedByName = "idToMarca")
    Caminhao toEntityFromAtualizacao(AtualizacaoCaminhao dto);
    
    // Atualiza Entity existente com dados do DTO
    @Mapping(target = "id", ignore = true) // Não atualiza ID
    @Mapping(target = "marca", source = "marcaId", qualifiedByName = "idToMarca")
    void updateEntityFromDto(AtualizacaoCaminhao dto, @MappingTarget Caminhao caminhao);
    
    // Método para converter marcaId em objeto Marca
    @Named("idToMarca")
    default Marca idToMarca(Long marcaId) {
        if (marcaId == null) return null;
        Marca marca = new Marca();
        marca.setId(marcaId);
        return marca;
    }
}
