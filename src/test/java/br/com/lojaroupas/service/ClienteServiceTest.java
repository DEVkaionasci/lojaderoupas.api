package br.com.lojaroupas.service;

import br.com.lojaroupas.dto.ClienteRequestDTO;
import br.com.lojaroupas.dto.ClienteResponseDTO;
import br.com.lojaroupas.exception.ConflitoException;
import br.com.lojaroupas.exception.NegocioException;
import br.com.lojaroupas.exception.RecursoNaoEncontradoException;
import br.com.lojaroupas.fake.FakeCepGateway;
import br.com.lojaroupas.fake.FakeClienteDAO;
import br.com.lojaroupas.util.SenhaUtil;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClienteServiceTest {

    private final FakeClienteDAO dao = new FakeClienteDAO();
    private final ClienteService service = new ClienteService(dao, new FakeCepGateway());

    private ClienteRequestDTO dto(String nome, String email, String senha, String cep) {
        ClienteRequestDTO dto = new ClienteRequestDTO();
        dto.setNome(nome);
        dto.setEmail(email);
        dto.setSenha(senha);
        dto.setCep(cep);
        return dto;
    }

    @Test
    void enderecoVemDaApiExternaAPartirDoCep() {
        ClienteResponseDTO r = service.criar(dto("Ana", "ana@loja.com", "segredo123", "40020-000"));

        assertEquals("40020000", r.getCep());
        assertEquals("Salvador", r.getCidade());
        assertEquals("BA", r.getUf());
        assertEquals("Praça Municipal", r.getLogradouro());
    }

    @Test
    void senhaFicaGravadaComoHashEOutputNaoTemCampoDeSenha() {
        service.criar(dto("Ana", "ana@loja.com", "segredo123", "40020000"));

        String guardado = dao.buscarPorId(1L).get().getSenhaHash();
        assertFalse(guardado.contains("segredo123"));
        assertTrue(SenhaUtil.confere("segredo123", guardado));

        for (Field campo : ClienteResponseDTO.class.getDeclaredFields()) {
            assertFalse(campo.getName().toLowerCase().contains("senha"),
                    "O DTO de resposta nao pode expor senha: " + campo.getName());
        }
    }

    @Test
    void rejeitaDadosInvalidos() {
        assertThrows(NegocioException.class, () -> service.criar(null));
        assertThrows(NegocioException.class, () -> service.criar(dto("", "ana@loja.com", "segredo123", "40020000")));
        assertThrows(NegocioException.class, () -> service.criar(dto("Ana", "sem-arroba", "segredo123", "40020000")));
        assertThrows(NegocioException.class, () -> service.criar(dto("Ana", "ana@loja.com", "123", "40020000")));
        assertThrows(NegocioException.class, () -> service.criar(dto("Ana", "ana@loja.com", "segredo123", "12")));
        assertThrows(RecursoNaoEncontradoException.class,
                () -> service.criar(dto("Ana", "ana@loja.com", "segredo123", "00000000")));
    }

    @Test
    void emailDuplicadoGeraConflito() {
        service.criar(dto("Ana", "ana@loja.com", "segredo123", "40020000"));
        assertThrows(ConflitoException.class,
                () -> service.criar(dto("Outra Ana", "ANA@loja.com", "outrasenha", "40020000")));
    }

    @Test
    void atualizarSemInformarSenhaMantemOHashAntigo() {
        service.criar(dto("Ana", "ana@loja.com", "segredo123", "40020000"));
        String hashAntes = dao.buscarPorId(1L).get().getSenhaHash();

        ClienteResponseDTO r = service.atualizar(1L, dto("Ana Maria", "ana@loja.com", null, "01001-000"));

        assertEquals("Ana Maria", r.getNome());
        assertEquals("SP", r.getUf()); // endereco foi refeito pelo novo CEP
        assertEquals(hashAntes, dao.buscarPorId(1L).get().getSenhaHash());
    }

    @Test
    void atualizarComNovaSenhaTrocaOHash() {
        service.criar(dto("Ana", "ana@loja.com", "segredo123", "40020000"));

        service.atualizar(1L, dto("Ana", "ana@loja.com", "novasenha1", "40020000"));

        assertTrue(SenhaUtil.confere("novasenha1", dao.buscarPorId(1L).get().getSenhaHash()));
    }

    @Test
    void operacoesEmClienteInexistenteGeram404() {
        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscar(99L));
        assertThrows(RecursoNaoEncontradoException.class, () -> service.remover(99L));
        assertThrows(RecursoNaoEncontradoException.class,
                () -> service.atualizar(99L, dto("Ana", "ana@loja.com", null, "40020000")));
    }

    @Test
    void listaERemoveClientes() {
        service.criar(dto("Ana", "ana@loja.com", "segredo123", "40020000"));
        service.criar(dto("Bia", "bia@loja.com", "segredo123", "01001000"));
        assertEquals(2, service.listar().size());

        service.remover(1L);
        assertEquals(1, service.listar().size());
    }
}
