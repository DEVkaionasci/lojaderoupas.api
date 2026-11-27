package br.com.lojaroupas.controller;

import br.com.lojaroupas.command.AtualizarClienteCommand;
import br.com.lojaroupas.command.AtualizarProdutoCommand;
import br.com.lojaroupas.command.AtualizarStatusPedidoCommand;
import br.com.lojaroupas.command.BuscarClienteCommand;
import br.com.lojaroupas.command.BuscarPedidoCommand;
import br.com.lojaroupas.command.BuscarProdutoCommand;
import br.com.lojaroupas.command.CadastrarClienteCommand;
import br.com.lojaroupas.command.CadastrarProdutoCommand;
import br.com.lojaroupas.command.ConsultarCepCommand;
import br.com.lojaroupas.command.CriarPedidoCommand;
import br.com.lojaroupas.command.ListarClientesCommand;
import br.com.lojaroupas.command.ListarPedidosCommand;
import br.com.lojaroupas.command.ListarPedidosDoClienteCommand;
import br.com.lojaroupas.command.ListarProdutosCommand;
import br.com.lojaroupas.command.RemoverClienteCommand;
import br.com.lojaroupas.command.RemoverProdutoCommand;
import br.com.lojaroupas.service.CepService;
import br.com.lojaroupas.service.ClienteService;
import br.com.lojaroupas.service.PedidoService;
import br.com.lojaroupas.service.ProdutoService;

/**
 * A tabela de rotas da API (ja sem o prefixo /api, que vem do mapeamento do Tomcat).
 * Terceiro argumento: true = rota administrativa (exige X-API-Key).
 *
 * Regra adotada: como nao ha login de cliente, tudo que le/altera dados de um
 * cliente ja existente e administrativo. Publico: catalogo, cadastro, criar pedido e CEP.
 */
public final class Rotas {

    private Rotas() {
    }

    public static Router montar(ProdutoService produtos, ClienteService clientes,
                                PedidoService pedidos, CepService cep) {
        return new Router()
                // ---- produtos
                .adicionar("GET", "/produtos", false, new ListarProdutosCommand(produtos))
                .adicionar("GET", "/produtos/(\\d+)", false, new BuscarProdutoCommand(produtos))
                .adicionar("POST", "/produtos", true, new CadastrarProdutoCommand(produtos))
                .adicionar("PUT", "/produtos/(\\d+)", true, new AtualizarProdutoCommand(produtos))
                .adicionar("DELETE", "/produtos/(\\d+)", true, new RemoverProdutoCommand(produtos))
                // ---- clientes
                .adicionar("POST", "/clientes", false, new CadastrarClienteCommand(clientes))
                .adicionar("GET", "/clientes", true, new ListarClientesCommand(clientes))
                .adicionar("GET", "/clientes/(\\d+)", true, new BuscarClienteCommand(clientes))
                .adicionar("PUT", "/clientes/(\\d+)", true, new AtualizarClienteCommand(clientes))
                .adicionar("DELETE", "/clientes/(\\d+)", true, new RemoverClienteCommand(clientes))
                .adicionar("GET", "/clientes/(\\d+)/pedidos", true, new ListarPedidosDoClienteCommand(pedidos))
                // ---- pedidos
                .adicionar("POST", "/pedidos", false, new CriarPedidoCommand(pedidos))
                .adicionar("GET", "/pedidos", true, new ListarPedidosCommand(pedidos))
                .adicionar("GET", "/pedidos/(\\d+)", true, new BuscarPedidoCommand(pedidos))
                .adicionar("PUT", "/pedidos/(\\d+)/status", true, new AtualizarStatusPedidoCommand(pedidos))
                // ---- API externa (ViaCEP)
                .adicionar("GET", "/cep/([\\w-]+)", false, new ConsultarCepCommand(cep));
    }
}
