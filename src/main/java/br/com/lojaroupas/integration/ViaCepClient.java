package br.com.lojaroupas.integration;

import br.com.lojaroupas.exception.IntegracaoException;
import br.com.lojaroupas.exception.NegocioException;
import br.com.lojaroupas.exception.RecursoNaoEncontradoException;
import br.com.lojaroupas.util.Validacao;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Consumo da API EXTERNA publica ViaCEP (https://viacep.com.br).
 * GET https://viacep.com.br/ws/{cep}/json/
 * Usa o HttpClient que ja vem no Java 11+ (nenhuma biblioteca extra).
 */
public class ViaCepClient implements CepGateway {

    private static final String URL_BASE = "https://viacep.com.br/ws/";

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    private final Gson gson = new Gson();

    @Override
    public EnderecoCep consultar(String cep) {
        String cepLimpo = Validacao.normalizarCep(cep);
        HttpRequest requisicao = HttpRequest.newBuilder(URI.create(URL_BASE + cepLimpo + "/json/"))
                .timeout(Duration.ofSeconds(8))
                .header("Accept", "application/json")
                .GET()
                .build();
        try {
            HttpResponse<String> resposta = http.send(requisicao,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resposta.statusCode() == 400) {
                throw new NegocioException("CEP inválido");
            }
            if (resposta.statusCode() != 200) {
                throw new IntegracaoException("O ViaCEP respondeu com status " + resposta.statusCode());
            }
            ViaCepResposta dados = gson.fromJson(resposta.body(), ViaCepResposta.class);
            if (dados == null || Boolean.TRUE.equals(dados.erro)) {
                throw new RecursoNaoEncontradoException("CEP não encontrado: " + cepLimpo);
            }
            return new EnderecoCep(cepLimpo, dados.logradouro, dados.bairro, dados.localidade, dados.uf);
        } catch (IOException e) {
            throw new IntegracaoException("Falha ao consultar o ViaCEP: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IntegracaoException("A consulta ao ViaCEP foi interrompida", e);
        } catch (JsonParseException e) {
            throw new IntegracaoException("Resposta inválida do ViaCEP", e);
        }
    }

    /** Formato do JSON devolvido pelo ViaCEP (so os campos que nos interessam). */
    private static class ViaCepResposta {
        String cep;
        String logradouro;
        String bairro;
        String localidade;
        String uf;
        Boolean erro;
    }
}
