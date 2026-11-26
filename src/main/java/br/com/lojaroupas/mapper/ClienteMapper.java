package br.com.lojaroupas.mapper;

import br.com.lojaroupas.dto.ClienteResponseDTO;
import br.com.lojaroupas.model.Cliente;

/**
 * Converte Cliente (dominio) em ClienteResponseDTO.
 * Repare: o DTO de resposta NAO tem senha nem senhaHash. E exatamente isso que
 * o padrao DTO resolve: a entidade tem dados sensiveis, o DTO expoe so o necessario.
 */
public final class ClienteMapper {

    private ClienteMapper() {
    }

    public static ClienteResponseDTO toResponse(Cliente cliente) {
        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getCep(),
                cliente.getLogradouro(),
                cliente.getBairro(),
                cliente.getCidade(),
                cliente.getUf());
    }
}
