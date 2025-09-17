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
            contentArea.innerHTML = `<h2>Usuários Cadastrados</h2><div id="usuarios-list"><p>Carregando...</p></div>`;
            fetch('/admin/api/usuarios')
                .then(res => res.json())
                .then(usuarios => {
                    const listDiv = document.getElementById('usuarios-list');
                    if (!usuarios || usuarios.length === 0) return listDiv.innerHTML = '<p>Nenhum usuário cadastrado.</p>';

                    usuarios.sort((a, b) => a.nome.localeCompare(b.nome));

                    let html = '<table border="1" cellpadding="8" cellspacing="0">';
                    html += '<thead><tr><th>Nome</th><th>Email</th><th>Tipo</th><th>Status</th><th>Remover</th><th>Promover</th></tr></thead><tbody>';

                    usuarios.forEach(u => {
                        html += `<tr>
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

                    html += '</tbody></table>';
                    listDiv.innerHTML = html;
                })
                .catch(err => {
                    document.getElementById('usuarios-list').innerHTML = '<p>Erro ao carregar usuários.</p>';
                    console.error(err);
                });
            break;

        case 'pedidos':
                    contentArea.innerHTML = `
                        <h2>Pedidos</h2>
                        <div id="pedido-list" style="max-height: 400px; overflow-y: auto;">
                            <table border="1" cellpadding="8" cellspacing="0" id="pedido-table">
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
                                    <tr><td colspan="7">Carregando...</td></tr>
                                </tbody>
                            </table>
                        </div>
                    `;

                    function atualizarPedidosTabela() {
                        fetch('/admin/api/pedidos')
                            .then(res => res.json())
                            .then(pedidos => {
                                const tbody = document.getElementById('pedido-tbody');
                                if (!pedidos || pedidos.length === 0) {
                                    tbody.innerHTML = '<tr><td colspan="7">Nenhum pedido encontrado.</td></tr>';
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

                                    return `
                                        <tr>
                                            <td>${p.dataHoraFormatada}</td>
                                            <td>${p.usuarioNome}</td>
                                            <td>${itensHtml}</td>
                                            <td>R$ ${Number(p.valorTotal).toFixed(2)}</td>
                                            <td>${p.status}</td>
                                            <td>
                                                ${p.realizado ? '' : `<button class="btn btn-status" onclick="alterarStatusPedido('${p.id}')">
                                                    <i class="fa-solid fa-check"></i>
                                                </button>`}
                                            </td>
                                        </tr>
                                    `;
                                }).join('');

                                tbody.innerHTML = html;
                            })
                            .catch(err => {
                                console.error(err);
                                document.getElementById('pedido-tbody').innerHTML =
                                    '<tr><td colspan="7">Erro ao carregar pedidos.</td></tr>';
                            });
                    }

                    atualizarPedidosTabela();

                    if (intervaloPedidos) clearInterval(intervaloPedidos);

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
                    if (!tamanhos || tamanhos.length === 0) return listDiv.innerHTML = '<p>Nenhum tamanho cadastrado ainda.</p>';

                    let html = '<table border="1" cellpadding="8" cellspacing="0">';
                    html += '<thead><tr><th>Nome</th><th>Preço</th><th>Ações</th></tr></thead><tbody>';

                    tamanhos.forEach(t => {
                        html += `<tr>
                            <td>${t.nome}</td>
                            <td>R$ ${Number(t.preco).toFixed(2)}</td>
                            <td>
                                <a href="/admin/tamanhos/editar/${t.id}" class="btn btn-editar">Editar</a>
                                <a href="/admin/tamanhos/remover/${t.id}" class="btn btn-remover">Remover</a>
                            </td>
                        </tr>`;
                    });

                    html += '</tbody></table>';
                    listDiv.innerHTML = html;
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
                 <div id="usuarios-list"><p>Carregando...</p></div> 
            `;

            fetch('/admin/api/adicionais')
                .then(res => res.json())
                .then(adicionais => {
                    const listDiv = document.getElementById('usuarios-list'); // E aqui também


                    // Verifica se a lista de adicionais está vazia
                    if (!adicionais || adicionais.length === 0) {
                        listDiv.innerHTML = '<p>Nenhum adicional cadastrado ainda.</p>';
                        return;
                    }

                    // Constrói a tabela HTML dinamicamente
                    let html = '<table class="table table-striped">'; // Usando classes do Bootstrap para um visual melhor
                    html += '<thead><tr><th>Nome</th><th>Preço</th><th class="acoes">Ações</th></tr></thead><tbody>';

                    adicionais.forEach(a => {
                        // Formata o preço para ter sempre duas casas decimais
                        const precoFormatado = Number(a.preco).toFixed(2).replace('.', ',');

                        html += `
                            <tr>
                                <td>${a.nome}</td>
                                <td>R$ ${precoFormatado}</td>
                                <td class="acoes">
                                    <a href="/admin/adicionais/editar/${a.id}" class="btn btn-editar">Editar</a>
                                    
                                    <button onclick="removerAdicional('${a.id}')" class="btn btn-remover">
                                        Remover
                                    </button>
                                </td>
                            </tr>
                        `;
                    });

                    html += '</tbody></table>';

                    //  Insere a tabela pronta na página
                    listDiv.innerHTML = html;
                })
                .catch(err => {
                    // Trata possíveis erros de rede
                    document.getElementById('usuarios-list').innerHTML = '<p>Erro ao carregar adicionais.</p>';
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

function alterarStatusPedido(id, status) {
    if (status === "Concluído") {
        alert("Pedido já concluído");
        return;
    }

    if (!confirm("Deseja alterar o status deste pedido?")) return;

    fetch(`/admin/api/pedidos/${id}/status`, { method: 'PUT' })
        .then(async res => {
            if (!res.ok) {
                const text = await res.text();
                throw new Error(`${res.status} ${res.statusText} - ${text}`);
            }

            if (res.status === 204) {
                alert("Status atualizado com sucesso!");
                return;
            }

            const text = await res.text();
            let data;
            try {
                data = JSON.parse(text);
            } catch {
                data = { mensagem: text || "Status atualizado com sucesso!" };
            }

            alert(data.mensagem || "Status atualizado com sucesso!");
        })
        .then(() => {
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
