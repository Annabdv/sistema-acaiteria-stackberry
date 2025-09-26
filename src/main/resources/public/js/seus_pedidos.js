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

                const statusBadge = pedido.realizado
                    ? '<span class="badge bg-success">Concluído</span>'
                    : '<span class="badge bg-warning text-dark">Em Preparo</span>';

                const linha = `
                    <tr>
                        <td>${pedido.dataHoraFormatada}</td>
                        <td>${itensFormatados}</td>
                        <td>${valorFormatado}</td>
                        <td>${statusBadge}</td>
                    </tr>
                `;
                tabelaBody.innerHTML += linha;
            });
        }
    } catch (error) {
        console.error('Erro:', error);
    }
}