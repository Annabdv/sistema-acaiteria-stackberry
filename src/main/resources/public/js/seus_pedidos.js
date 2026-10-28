document.addEventListener('DOMContentLoaded', function () {
    carregarPedidos();
});

async function carregarPedidos() {
    try {
        const response = await fetch('/api/meus-pedidos');
        if (!response.ok) {
            throw new Error('Falha ao buscar os pedidos.');
        }

        const pedidos = await response.json();
        const tabelaBody = document.getElementById('tabela-pedidos-body');
        const mensagemSemPedidos = document.getElementById('mensagem-sem-pedidos');
        const pedidosListContainer = document.getElementById('pedidos-list');
        tabelaBody.innerHTML = '';

        if (!pedidos || pedidos.length === 0) {
            mensagemSemPedidos.style.display = 'block';
            if (pedidosListContainer) pedidosListContainer.style.display = 'none';
        } else {
            mensagemSemPedidos.style.display = 'none';
            if (pedidosListContainer) pedidosListContainer.style.display = 'block';

            pedidos.forEach(pedido => {
                const itensFormatados = pedido.itens.map(item => {
                    const tamanho = item.acai.tamanho.nome;
                    const adicionais = (item.acai.adicionais || []).map(adicional => adicional.nome).join(', ');
                    const adicionaisStr = adicionais ? ` + ${adicionais}` : '';
                    return `<div class="order-item-chip"><strong>${item.quantidade}x</strong> ${tamanho}${adicionaisStr}</div>`;
                }).join('');

                const valorFormatado = new Intl.NumberFormat('pt-BR', {
                    style: 'currency',
                    currency: 'BRL'
                }).format(pedido.valorTotal);

                let statusBadge;
                let acaoCell;

                switch (pedido.status) {
                    case "ENTREGUE":
                        statusBadge = '<span class="order-status-badge status-delivered"><i class="fa-solid fa-check"></i> Entregue</span>';
                        acaoCell = '<td></td>';
                        break;
                    case "CANCELADO":
                        statusBadge = '<span class="order-status-badge status-cancelled"><i class="fa-solid fa-xmark"></i> Cancelado</span>';
                        acaoCell = '<td></td>';
                        break;
                    case "EM_PREPARO":
                    default:
                        statusBadge = '<span class="order-status-badge status-preparing"><i class="fa-solid fa-clock"></i> Em Preparo</span>';
                        acaoCell = `<td style="text-align: right;"><button class="btn-cancelar-pedido" onclick="cancelarPedido('${pedido.id}')"><i class="fa-solid fa-ban"></i> Cancelar</button></td>`;
                        break;
                }

                const acaoCellComLabel = acaoCell ? acaoCell.replace('<td>', '<td data-label="Ação">') : '<td></td>';

                const linha = `
                    <tr>
                        <td data-label="Data &amp; Hora"><i class="fa-regular fa-calendar-days" style="color: var(--text-muted); margin-right: 6px;"></i>${pedido.dataHoraFormatada}</td>
                        <td data-label="Itens do Pedido">${itensFormatados}</td>
                        <td data-label="Valor Total"><strong style="color: var(--color-acai, #3A0B2E); font-size: 1.05rem;">${valorFormatado}</strong></td>
                        <td data-label="Status">${statusBadge}</td>
                        ${acaoCellComLabel}
                    </tr>
                `;
                tabelaBody.innerHTML += linha;
            });
        }
    } catch (error) {
        console.error('Erro:', error);
    }
}

async function cancelarPedido(pedidoId) {
    if (!confirm('Deseja realmente cancelar este pedido?')) {
        return;
    }

    try {
        const response = await fetch(`/api/pedidos/${pedidoId}/cancelar`, {
            method: 'DELETE',
        });

        if (!response.ok) {
            const erro = await response.json();
            throw new Error(erro.mensagem || 'Não foi possível cancelar o pedido.');
        }

        carregarPedidos();

    } catch (error) {
        console.error('Erro ao cancelar pedido:', error);
        alert(error.message);
    }
}