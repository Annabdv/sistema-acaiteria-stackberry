// Atualiza número de usuários
function atualizarUsuarios() {
    fetch('/admin/totalUsuarios')
        .then(res => res.json())
        .then(data => {
            const span = document.getElementById('usuarios-logados');
            if(span) span.textContent = data.total;
        });
}


// atualiza número de pedidos
function atualizarPedidos() {
    fetch('/admin/totalPedidos')
        .then(res => res.json())
        .then(data => {
            const span = document.getElementById('pedidos-feitos');
            if(span) span.textContent = data.total;
        });
}

function atualizarUsuariosEPedidos() {
    atualizarUsuarios();
    atualizarPedidos();
}

let intervaloPedidos;

// Carrega conteúdo das abas
function loadContent(section) {
    const contentArea = document.getElementById('content-area');
    if (intervaloPedidos) clearInterval(intervaloPedidos);

    switch(section) {
            case 'inicio':
                contentArea.innerHTML = `
                    <div id="inicio">
                        <h1>Área administrativa</h1>
                        <div class="stats">
                            <button>Usuários Registrados <span class="stat-number" id="usuarios-logados">0</span></button>
                            <button>Pedidos Feitos <span class="stat-number" id="pedidos-feitos">0</span></button>
                        </div>
                        <div class="cliente-button">
                            <button onclick="window.location.href='/cardapio'">Área do Cliente</button>
                        </div>
                    </div>
                `;
                atualizarUsuariosEPedidos();
                break;

            case 'usuarios':
                            contentArea.innerHTML = `
                                <h2>Usuários Cadastrados</h2>
                                <hr>
                                <div id="usuarios-list"><p>Carregando...</p></div>
                            `;
                            fetch('/admin/api/usuarios')
                                .then(res => res.json())
                                .then(usuarios => {
                                    const listDiv = document.getElementById('usuarios-list');
                                    if (!usuarios || usuarios.length === 0) return listDiv.innerHTML = '<p>Nenhum usuário cadastrado.</p>';

                                    usuarios.sort((a, b) => a.nome.localeCompare(b.nome));

                                     let tableRows = '';
                                    usuarios.forEach(u => {
                                        tableRows += `<tr>
                                            <td>${u.nome}</td>
                                            <td>${u.email}</td>
                                            <td>${u.tipoDeUsuario}</td>
                                            <td>${u.status}</td>
                                            <td>
                                                <button onclick="excluirUsuario('${u.id}')"><i class="fa-solid fa-trash"></i></button>
                                            </td>
                                            <td>
                                                <button onclick="promoverUsuario('${u.id}')"><i class="fa-solid fa-arrow-up"></i></button>
                                            </td>
                                        </tr>`;
                                    });

                                    const tableHTML = `
                                        <div class="table-responsive">
                                            <table border="1" cellpadding="8" cellspacing="0">
                                                <thead>
                                                    <tr>
                                                        <th>Nome</th>
                                                        <th>Email</th>
                                                        <th>Tipo</th>
                                                        <th>Status</th>
                                                        <th>Remover</th>
                                                        <th>Promover</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    ${tableRows}
                                                </tbody>
                                            </table>
                                        </div>
                                    `;
                                    listDiv.innerHTML = tableHTML;
                                })
                                .catch(err => {
                                    document.getElementById('usuarios-list').innerHTML = '<p>Erro ao carregar usuários.</p>';
                                    console.error(err);
                                });
                            break;

            case 'pedidos':
                            contentArea.innerHTML = `
                                <h2>Pedidos</h2>
                                <hr>
                                <div id="pedido-list">
                                    <div class="table-responsive">
                                        <table id="pedido-table">
                                            <thead>
                                                <tr>
                                                    <th>Data</th>
                                                    <th>Cliente</th>
                                                    <th>Itens</th>
                                                    <th>Valor Total</th>
                                                    <th>Status</th>
                                                    <th>Finalizar</th>
                                                </tr>
                                            </thead>
                                            <tbody id="pedido-tbody">
                                                <tr><td colspan="6">Carregando...</td></tr>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            `;

                            function atualizarPedidosTabela() {
                                fetch('/admin/api/pedidos')
                                    .then(res => res.json())
                                    .then(pedidos => {
                                        const tbody = document.getElementById('pedido-tbody');
                                        if (!tbody) return;

                                        if (!pedidos || pedidos.length === 0) {
                                            tbody.innerHTML = '<tr><td colspan="6">Nenhum pedido encontrado.</td></tr>';
                                            return;
                                        }

                                        pedidos.sort((a, b) => new Date(b.dataHora) - new Date(a.dataHora));

                                        const html = pedidos.map(p => {
                                            const itensHtml = p.itens.map(i => {
                                                const tamanho = i.acai.tamanho.nome;
                                                const adicionais = (i.acai.adicionais || []).map(a => a.nome).join(', ');
                                                const adicionaisStr = adicionais ? ` (${adicionais})` : '';
                                                return `${tamanho}${adicionaisStr} x${i.quantidade}`;
                                            }).join('<br>');

                                            let statusBadge;
                                            let acaoCell;

                                            switch (p.status) {
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
                                                    acaoCell = `<td><button class="btn btn-success btn-sm" onclick="alterarStatusPedido('${p.id}')"><i class="fa-solid fa-check"></i></button></td>`;
                                                    break;
                                            }

                                            return `
                                                <tr>
                                                    <td>${p.dataHoraFormatada}</td>
                                                    <td>${p.usuarioNome}</td>
                                                    <td>${itensHtml}</td>
                                                    <td>R$ ${Number(p.valorTotal).toFixed(2)}</td>
                                                    <td>${statusBadge}</td>
                                                    ${acaoCell}
                                                </tr>
                                            `;
                                        }).join('');

                                        tbody.innerHTML = html;
                                    })
                                    .catch(err => {
                                        console.error(err);
                                        const tbody = document.getElementById('pedido-tbody');
                                        if (tbody) {
                                            tbody.innerHTML = '<tr><td colspan="6">Erro ao carregar pedidos.</td></tr>';
                                        }
                                    });
                            }

                            atualizarPedidosTabela();
                            intervaloPedidos = setInterval(atualizarPedidosTabela, 5000);
                            break;


    case 'tamanho':
        contentArea.innerHTML = `
            <h2>Tamanhos de Açaí Cadastrados</h2>
            <a href="/admin/tamanhos/novo" class="btn-default">Adicionar Novo Tamanho</a>
            <hr>
            <div id="tamanho-list"><p>Carregando...</p></div>
        `;
        fetch('/admin/api/tamanhos')
            .then(res => res.json())
            .then(tamanhos => {
                const listDiv = document.getElementById('tamanho-list');
                if (!tamanhos || tamanhos.length === 0) {
                    return listDiv.innerHTML = '<p>Nenhum tamanho cadastrado ainda.</p>';
                }

                let tableRows = '';
                tamanhos.forEach(t => {
                    const precoFormatado = Number(t.preco).toFixed(2).replace('.', ',');
                    tableRows += `
                        <tr>
                            <td>${t.nome}</td>
                            <td>R$ ${precoFormatado}</td>
                            <td class="acoes">
                                <a href="/admin/tamanhos/editar/${t.id}" class="btn btn-editar">Editar</a>
                                <button onclick="removerTamanho('${t.id}')" class="btn btn-remover">Remover</button>
                            </td>
                        </tr>`;
                });

                const tableHTML = `
                    <div class="table-responsive">
                        <table class="table table-striped">
                            <thead>
                                <tr>
                                    <th>Nome</th>
                                    <th>Preço</th>
                                    <th class="acoes">Ações</th>
                                </tr>
                            </thead>
                            <tbody>
                                ${tableRows}
                            </tbody>
                        </table>
                    </div>
                `;
                listDiv.innerHTML = tableHTML;
            })
            .catch(err => {
                document.getElementById('tamanho-list').innerHTML = '<p>Erro ao carregar tamanhos.</p>';
                console.error(err);
            });
        break;

        case 'adicionais':
            contentArea.innerHTML = `
                 <h2>Gerenciar Adicionais</h2>
                 <a href="/admin/adicionais/novo" class="btn-default">Adicionar Novo Adicional</a>
                 <hr>
                 <div id="adicional-list"><p>Carregando...</p></div>
            `;
            fetch('/admin/api/adicionais')
                .then(res => res.json())
                .then(adicionais => {
                    const listDiv = document.getElementById('adicional-list');
                    if (!adicionais || adicionais.length === 0) {
                        return listDiv.innerHTML = '<p>Nenhum adicional cadastrado ainda.</p>';
                    }

                    let tableRows = '';
                    adicionais.forEach(a => {
                        const precoFormatado = Number(a.preco).toFixed(2).replace('.', ',');
                        tableRows += `
                            <tr>
                                <td>${a.nome}</td>
                                <td>R$ ${precoFormatado}</td>
                                <td class="acoes">
                                    <a href="/admin/adicionais/editar/${a.id}" class="btn btn-editar">Editar</a>
                                    <button onclick="removerAdicional('${a.id}')" class="btn btn-remover">Remover</button>
                                </td>
                            </tr>`;
                    });

                    const tableHTML = `
                        <div class="table-responsive">
                            <table class="table table-striped">
                                <thead>
                                    <tr>
                                        <th>Nome</th>
                                        <th>Preço</th>
                                        <th class="acoes">Ações</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    ${tableRows}
                                </tbody>
                            </table>
                        </div>
                    `;
                    listDiv.innerHTML = tableHTML;
                })
                .catch(err => {
                    document.getElementById('adicional-list').innerHTML = '<p>Erro ao carregar adicionais.</p>';
                    console.error(err);
                });
            break;

    }
}

// Promove o usuário para administrador
async function promoverUsuario(id) {
    if (!confirm('Tem certeza que deseja promover este usuário?')) return;
    console.log('Promovendo id:', id);

    try {
        const res = await fetch(`/admin/api/usuarios/${id}/promover`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include'
        });

        if (!res.ok) {
            const text = await res.text();
            throw new Error(`${res.status} ${res.statusText} - ${text}`);
        }

        if (res.status === 204) {
            alert('Usuário promovido com sucesso.');
        } else {
            const text = await res.text();
            let data;
            try { data = JSON.parse(text); } catch { data = { mensagem: text || 'Sucesso' }; }
            alert(data.mensagem || 'Usuário promovido com sucesso.');
        }

        loadContent('usuarios');
        atualizarUsuarios();
    } catch (e) {
        console.error(e);
        alert('Erro ao promover usuário: ' + e.message);
    }
}

// Desativar usuário
function excluirUsuario(id) {
     if (!confirm('Tem certeza que deseja remover este usuário?')) return;

    fetch(`/admin/api/usuarios/${id}`, { method: 'DELETE' })
        .then(res => res.json())
        .then(data => {
            alert(data.mensagem);
            loadContent('usuarios');
            atualizarUsuarios();
        })
        .catch(err => console.error(err));
}

function alterarStatusPedido(id) {
    if (!confirm("Marcar este pedido como 'Entregue'?")) {
        return;
    }

    fetch(`/admin/api/pedidos/${id}/status`, { method: 'PUT' })
        .then(async res => {
            if (!res.ok) {
                const text = await res.text();
                throw new Error(`${res.status} ${res.statusText} - ${text}`);
            }
            loadContent("pedidos");
            atualizarPedidos();
        })
        .catch(err => {
            console.error(err);
            alert("Erro ao alterar status do pedido: " + err.message);
        });
}

// Carrega aba inicial automaticamente
window.addEventListener('DOMContentLoaded', () => {
    loadContent('inicio');
});

function removerAdicional(id) {
    if (!confirm('Tem certeza que deseja remover este adicional?')) {
        return;
    }
    fetch(`/admin/api/adicionais/${id}`, {
        method: 'DELETE'
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Falha ao remover o adicional. Status: ' + response.status);
            }
            return response.json();
        })
        .then(data => {
            alert(data.mensagem);
            loadContent('adicionais');
        })
        .catch(error => {
            alert('Ocorreu um erro ao tentar remover o adicional.');
            console.error('Erro:', error);
        });
}

function removerTamanho(id) {
    if (!confirm('Tem certeza que deseja remover este tamanho?')) {
        return;
    }

    fetch(`/admin/api/tamanhos/${id}`, {
        method: 'DELETE'
    })
        .then(response => response.json())
        .then(data => {
            alert(data.mensagem);
            loadContent('tamanho');
        })
        .catch(error => {
            alert('Ocorreu um erro ao tentar remover o tamanho.');
            console.error('Erro:', error);
        });
}
