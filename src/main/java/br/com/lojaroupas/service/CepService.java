package br.com.lojaroupas.service;

import br.com.lojaroupas.dto.CepResponseDTO;
import br.com.lojaroupas.integration.CepGateway;
import br.com.lojaroupas.integration.EnderecoCep;

/** Expoe a consulta de CEP (API externa) para a nossa propria API. */
public class CepService {

    private final CepGateway cepGateway;

    public CepService(CepGateway cepGateway) {
        this.cepGateway = cepGateway;
    }

    public CepResponseDTO consultar(String cep) {
        EnderecoCep e = cepGateway.consultar(cep);
        return new CepResponseDTO(e.getCep(), e.getLogradouro(), e.getBairro(), e.getCidade(), e.getUf());
    }
}
