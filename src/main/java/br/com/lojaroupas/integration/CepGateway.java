package br.com.lojaroupas.integration;

/**
 * Contrato para consultar um CEP. A aplicacao depende desta interface, nao do
 * ViaCEP: se amanha a API mudar, so a implementacao muda (e os testes usam uma
 * versao falsa, sem internet).
 */
public interface CepGateway {

    EnderecoCep consultar(String cep);
}
