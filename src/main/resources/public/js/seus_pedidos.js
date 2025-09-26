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
        tabelaBody.innerHTML = '';

        if (pedidos.length === 0) {
            mensagemSemPedidos.style.display = 'block';
        } else {
            mensagemSemPedidos.style.display = 'none';
            pedidos.forEach(pedido => {
                const itensFormatados = pedido.itens.map(item => {
                    const tamanho = item.acai.tamanho.nome;
                    const adicionais = (item.acai.adicionais || []).map(adicional => adicional.nome).join(', ');
                    const adicionaisStr = adicionais ? ` (com ${adicionais})` : '';
                    return `${item.quantidade}x ${tamanho}${adicionaisStr}`;
                }).join('<br>');

                const valorFormatado = new Intl.NumberFormat('pt-BR', {
                    style: 'currency',
                    currency: 'BRL'
                }).format(pedido.valorTotal);

                let statusBadge;
                let acaoCell;

                switch (pedido.status) {
                    case "ENTREGUE":
                        statusBadge = '<span class="badge bg-success">Entregue</span>';
                        acaoCell = '<td></td>';
                        break;
                    case "CANCELADO":
                        statusBadge = '<span class="badge bg-danger">Cancelado</span>';
                        acaoCell = '<td></td>';
                        break;
                    case "EM_PREPARO":
                    default:
                        statusBadge = '<span class="badge bg-warning text-dark">Em Preparo</span>';
                        acaoCell = `<td><button class="btn-cancelar-pedido" onclick="cancelarPedido('${pedido.id}')">Cancelar</button></td>`; // <-- MUDANÇA AQUI
                        break;
                }

                const linha = `
                    <tr>
                        <td>${pedido.dataHoraFormatada}</td>
                        <td>${itensFormatados}</td>
                        <td>${valorFormatado}</td>
                        <td>${statusBadge}</td>
                        ${acaoCell}
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
    if (!confirm('Tem certeza que deseja cancelar este pedido?')) {
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

        alert('Pedido cancelado com sucesso!');
        carregarPedidos();

    } catch (error) {
        console.error('Erro ao cancelar pedido:', error);
        alert(error.message);
    }
}