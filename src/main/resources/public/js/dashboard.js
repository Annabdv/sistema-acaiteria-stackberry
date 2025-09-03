// atualiza nos stats
function atualizarUsuarios() {
    fetch('/admin/totalUsuarios')
        .then(res => res.json())
        .then(data => {
            const span = document.getElementById('usuarios-logados');
            if(span) {
                span.textContent = data.total;
            }
        });
}

// carrega o conteúdo das abas
function loadContent(section) {
    const contentArea = document.getElementById('content-area');

    switch(section) {

        // pag de inicio da dashboard (stats)
        case 'inicio':
            contentArea.innerHTML = `
                <div id="inicio">
                    <h1>Área administrativa</h1>
                    <div class="stats">
                        <button>
                            Usuários Registrados
                            <span class="stat-number" id="usuarios-logados">0</span>
                        </button>
                        <button>
                            Pedidos Feitos
                            <span class="stat-number" id="pedidos-feitos">0</span>
                        </button>
                    </div>
                </div>
            `;
            atualizarUsuarios();
            break;

        // pag de lista de usuarios
        case 'usuarios':
            contentArea.innerHTML = `
                <h2>Usuários Cadastrados</h2>
                <div id="usuarios-list"><p>Carregando...</p></div>
            `;

            fetch('/admin/api/usuarios')
                .then(res => res.json())
                .then(usuarios => {
                    const listDiv = document.getElementById('usuarios-list');

                    if (!usuarios || usuarios.length === 0) {
                        listDiv.innerHTML = '<p>Nenhum usuário cadastrado.</p>';
                        return;
                    }

                    // ordena pelo nome
                    usuarios.sort((a, b) => a.nome.localeCompare(b.nome));

                    let html = '<table border="1" cellpadding="8" cellspacing="0">';
                    html += '<thead><tr><th>ID</th><th>Nome</th><th>Email</th><th></th></tr></thead><tbody>';

                    usuarios.forEach(u => {
                        html += `<tr>
                            <td>${u.id}</td>
                            <td>${u.nome}</td>
                            <td>${u.email}</td>
                            <td>
                                <button onclick="excluirUsuario('${u.id}')">
                                    <i class="fa-solid fa-trash"></i>
                                </button>
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

        // pag de lista de pedidos
        case 'pedidos':
            contentArea.innerHTML = '<h2>Pedidos</h2><p>Lista de pedidos feitos...</p>';
            break;

        // pag de gerenciar tamanhos
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
                        listDiv.innerHTML = '<p>Nenhum tamanho cadastrado ainda.</p>';
                        return;
                    }

                    let html = '<table border="1" cellpadding="8" cellspacing="0">';
                    html += '<thead><tr><th>Nome</th><th>Preço</th><th>Ações</th></tr></thead><tbody>';

                    tamanhos.forEach(t => {
                        html += `
                            <tr>
                                <td>${t.nome}</td>
                                <td>R$ ${Number(t.preco).toFixed(2)}</td>
                                <td>
                                    <a href="/admin/tamanhos/editar/${t.id}" class="btn btn-editar">Editar</a>
                                    <a href="/admin/tamanhos/remover/${t.id}" class="btn btn-remover">Remover</a>
                                </td>
                            </tr>
                        `;
                    });

                    html += '</tbody></table>';
                    listDiv.innerHTML = html;
                })
                .catch(err => {
                    document.getElementById('tamanho-list').innerHTML = '<p>Erro ao carregar tamanhos.</p>';
                    console.error(err);
                });
            break;

        // pag de gerenciar adicionais
        case 'adicionais':
            contentArea.innerHTML = `
                 <h2>Gerenciar Adicionais</h2>
                 <a href="/admin/adicionais/novo" class="btn-default">Adicionar Novo Adicional</a>
                 <hr>
                 <div id="tamanho-list"><p>Carregando...</p></div>
            `;

            fetch('/admin/api/adicionais')
                .then(res => res.json())
                .then(adicionais => {
                    const listDiv = document.getElementById('adicional-list');

                    if (!adicionais || adicionais.length === 0) {
                        listDiv.innerHTML = '<p>Nenhum adicional cadastrado ainda.</p>';
                        return;
                    }

                    let html = '<table border="1" cellpadding="8" cellspacing="0">';
                    html += '<thead><tr><th>Nome</th><th>Preço</th><th>Ações</th></tr></thead><tbody>';

                    tamanhos.forEach(t => {
                        html += `
                            <tr>
                                <td>${t.nome}</td>
                                <td>R$ ${Number(t.preco).toFixed(2)}</td>
                                <td>
                                    <a href="/admin/adicionais/editar/${t.id}" class="btn btn-editar">Editar</a>
                                    <a href="/admin/adicionais/remover/${t.id}" class="btn btn-remover">Remover</a>
                                </td>
                            </tr>
                        `;
                    });

                    html += '</tbody></table>';
                    listDiv.innerHTML = html;
                })
                .catch(err => {
                    document.getElementById('adicional-list').innerHTML = '<p>Erro ao carregar adicionais.</p>';
                    console.error(err);
                });
            break;
    }
}

function excluirUsuario(id) {
    if (!confirm('Tem certeza que deseja excluir este usuário?')) return;

    fetch(`/admin/api/usuarios/${id}`, { method: 'DELETE' })
        .then(res => res.json())
        .then(data => {
            alert(data.mensagem);
            loadContent('usuarios');
            atualizarUsuarios();    // atualiza stats
        })
        .catch(err => console.error(err));
}

// carrega a aba inicial automaticamente
window.addEventListener('DOMContentLoaded', () => {
    loadContent('inicio');
});