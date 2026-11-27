package br.com.lojaroupas.fake;

import br.com.lojaroupas.exception.RecursoNaoEncontradoException;
import br.com.lojaroupas.integration.CepGateway;
import br.com.lojaroupas.integration.EnderecoCep;
import br.com.lojaroupas.util.Validacao;

import java.util.HashMap;
import java.util.Map;

/** Simula o ViaCEP sem internet: so conhece os CEPs cadastrados com adicionar(). */
public class FakeCepGateway implements CepGateway {

    private final Map<String, EnderecoCep> enderecos = new HashMap<>();

    public FakeCepGateway() {
        adicionar(new EnderecoCep("40020000", "Praça Municipal", "Centro", "Salvador", "BA"));
        adicionar(new EnderecoCep("01001000", "Praça da Sé", "Sé", "São Paulo", "SP"));
        adicionar(new EnderecoCep("50010000", "Rua do Bom Jesus", "Recife", "Recife", "PE"));
    }

    public void adicionar(EnderecoCep endereco) {
        enderecos.put(endereco.getCep(), endereco);
    }

    @Override
    public EnderecoCep consultar(String cep) {
        String limpo = Validacao.normalizarCep(cep);
        EnderecoCep endereco = enderecos.get(limpo);
        if (endereco == null) {
            throw new RecursoNaoEncontradoException("CEP não encontrado: " + limpo);
        }
        return endereco;
    }
}
