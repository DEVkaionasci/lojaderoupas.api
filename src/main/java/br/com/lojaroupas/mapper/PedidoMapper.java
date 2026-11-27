package br.com.lojaroupas.mapper;

import br.com.lojaroupas.dto.ItemPedidoResponseDTO;
import br.com.lojaroupas.dto.PedidoResponseDTO;
import br.com.lojaroupas.model.ItemPedido;
import br.com.lojaroupas.model.Pedido;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** Converte Pedido/ItemPedido (dominio) em DTOs de resposta. */
public final class PedidoMapper {

    private PedidoMapper() {
    }

    public static PedidoResponseDTO toResponse(Pedido pedido) {
        List<ItemPedidoResponseDTO> itens = new ArrayList<>();
        for (ItemPedido item : pedido.getItens()) {
            itens.add(new ItemPedidoResponseDTO(
                    item.getProdutoId(),
                    item.getNomeProduto(),
                    item.getQuantidade(),
                    item.getPrecoUnitario(),
                    item.getSubtotal()));
        }
        return new PedidoResponseDTO(
                pedido.getId(),
                pedido.getClienteId(),
                pedido.getDataCriacao().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                pedido.getStatus().name(),
                pedido.getFormaPagamento().name(),
                pedido.getCepEntrega(),
                pedido.getUfEntrega(),
                itens,
                pedido.getSubtotal(),
                pedido.getDesconto(),
                pedido.getFrete(),
                pedido.getTotal(),
                pedido.getTipoFrete(),
                pedido.getDescontosAplicados());
    }
}
