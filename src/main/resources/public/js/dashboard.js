/**
 * STACKBERRY - DASHBOARD ADMINISTRATIVO
 * Gerenciamento em tempo real de Pedidos, Cardápio (Tamanhos e Adicionais) e Usuários
 */

let pollingInterval = null;
let isUpdating = false;

// Formatador de Moeda Brasileiro (R$ 0,00)
function formatarMoeda(valor) {
    const num = Number(valor) || 0;
    return num.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
}

// Formatador de Data e Hora
function formatarDataHora(dataString) {
    if (!dataString) return '-';
    try {
        const d = new Date(dataString);
        if (isNaN(d.getTime())) return dataString;
        return d.toLocaleDateString('pt-BR', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    } catch {
        return dataString;
    }
}

/**
 * Carrega todos os dados do painel simultaneamente
 */
async function carregarTudo() {
    if (isUpdating) return;
    isUpdating = true;

    const btnRefresh = document.querySelector('.btn-admin-refresh i');
    if (btnRefresh) btnRefresh.classList.add('fa-spin');

    try {
        await Promise.allSettled([
            carregarPedidos(),
            carregarCardapio(),
            carregarUsuarios()
        ]);
    } catch (e) {
        console.error('Erro ao atualizar dashboard:', e);
    } finally {
        isUpdating = false;
        if (btnRefresh) {
            setTimeout(() => btnRefresh.classList.remove('fa-spin'), 400);
        }
    }
}

/**
 * 1. SEÇÃO DE PEDIDOS & KPI DE FATURAMENTO / TOTAL DE PEDIDOS
 */
async function carregarPedidos() {
    const tbody = document.getElementById('pedidos-tbody');
    const badgeAndamento = document.getElementById('badge-pedidos-andamento');
    const kpiTotalPedidos = document.getElementById('kpi-total-pedidos');
    const kpiFaturamento = document.getElementById('kpi-faturamento');

    try {
        const res = await fetch('/admin/api/pedidos');
        if (!res.ok) throw new Error(`Status ${res.status}`);
        const pedidos = await res.json();

        if (!Array.isArray(pedidos) || pedidos.length === 0) {
            if (tbody) {
                tbody.innerHTML = `
                    <tr>
                        <td colspan="6" style="text-align: center; color: var(--text-muted); padding: 36px 20px;">
                            <i class="fa-solid fa-inbox" style="font-size: 2rem; margin-bottom: 8px; display: block; opacity: 0.4;"></i>
                            Nenhum pedido registrado no sistema ainda.
                        </td>
                    </tr>
                `;
            }
            if (badgeAndamento) {
                badgeAndamento.className = 'badge-status entregue';
                badgeAndamento.innerHTML = '<i class="fa-solid fa-circle-check"></i> Sem pedidos pendentes';
            }
            if (kpiTotalPedidos) kpiTotalPedidos.textContent = '0';
            if (kpiFaturamento) kpiFaturamento.textContent = formatarMoeda(0);
            return;
        }

        // Ordenar pedidos por data decrescente (mais recentes primeiro)
        pedidos.sort((a, b) => new Date(b.dataHora || 0) - new Date(a.dataHora || 0));

        let emPreparoCount = 0;
        let totalFaturamento = 0;

        const rowsHtml = pedidos.map(p => {
            const valorTotal = Number(p.valorTotal) || 0;
            if (p.status !== 'CANCELADO') {
                totalFaturamento += valorTotal;
            }

            // Itens com tamanho, quantidade e adicionais
            const itensHtml = (p.itens || []).map(item => {
                const tamNome = item.acai?.tamanho?.nome || 'Açaí';
                const adicionais = (item.acai?.adicionais || []).map(a => a.nome).join(', ');
                const adicStr = adicionais ? ` <span style="color: var(--text-muted); font-size: 0.8rem;">(${adicionais})</span>` : '';
                return `
                    <div style="margin-bottom: 4px; line-height: 1.3;">
                        <strong>${tamNome}</strong> 
                        <span style="font-weight: 700; color: var(--color-purple);">x${item.quantidade}</span>
                        ${adicStr}
                    </div>
                `;
            }).join('');

            let statusBadge = '';
            let acaoCell = '';

            switch (p.status) {
                case 'EM_PREPARO':
                    emPreparoCount++;
                    statusBadge = '<span class="badge-status em-preparo"><i class="fa-solid fa-clock"></i> Em Preparo</span>';
                    acaoCell = `
                        <button type="button" class="btn-finish-order" onclick="alterarStatusPedido('${p.id}')" title="Marcar como entregue">
                            <i class="fa-solid fa-check"></i> Entregar
                        </button>
                    `;
                    break;
                case 'ENTREGUE':
                    statusBadge = '<span class="badge-status entregue"><i class="fa-solid fa-circle-check"></i> Entregue</span>';
                    acaoCell = '<span style="color: var(--color-green); font-size: 0.82rem; font-weight: 600;"><i class="fa-solid fa-check-double"></i> Concluído</span>';
                    break;
                case 'CANCELADO':
                    statusBadge = '<span class="badge-status cancelado"><i class="fa-solid fa-ban"></i> Cancelado</span>';
                    acaoCell = '<span style="color: #DC2626; font-size: 0.82rem; font-weight: 600;">Cancelado</span>';
                    break;
                default:
                    statusBadge = `<span class="badge-status em-preparo">${p.status}</span>`;
                    acaoCell = `
                        <button type="button" class="btn-finish-order" onclick="alterarStatusPedido('${p.id}')">
                            <i class="fa-solid fa-check"></i> Entregar
                        </button>
                    `;
            }

            const dataHoraTexto = p.dataHoraFormatada || formatarDataHora(p.dataHora);
            const clienteNome = p.usuarioNome || 'Cliente Anônimo';
            const clienteEmail = p.usuarioEmail ? `<br><small style="color: var(--text-muted); font-size: 0.78rem;">${p.usuarioEmail}</small>` : '';

            return `
                <tr class="table-row-card card-pedido">
                    <td class="col-date">
                        <span class="cell-date"><i class="fa-regular fa-clock" style="margin-right: 5px; opacity: 0.7;"></i>${dataHoraTexto}</span>
                        <span class="cell-status-mobile">${statusBadge}</span>
                    </td>
                    <td class="col-client">
                        <strong>${clienteNome}</strong>${clienteEmail}
                    </td>
                    <td class="col-items">${itensHtml || '<span style="color: var(--text-muted);">Sem itens</span>'}</td>
                    <td class="col-total">
                        <span class="price-val">${formatarMoeda(valorTotal)}</span>
                    </td>
                    <td class="col-status cell-status-desktop">${statusBadge}</td>
                    <td class="col-actions">${acaoCell}</td>
                </tr>
            `;
        }).join('');

        if (tbody) tbody.innerHTML = rowsHtml;

        // Atualizar Badge de Pedidos em Andamento
        if (badgeAndamento) {
            if (emPreparoCount > 0) {
                badgeAndamento.className = 'badge-status em-preparo';
                badgeAndamento.innerHTML = `<i class="fa-solid fa-fire"></i> ${emPreparoCount} em preparo`;
            } else {
                badgeAndamento.className = 'badge-status entregue';
                badgeAndamento.innerHTML = '<i class="fa-solid fa-circle-check"></i> Todos pedidos em dia';
            }
        }

        // Atualizar KPIs
        if (kpiTotalPedidos) kpiTotalPedidos.textContent = pedidos.length;
        if (kpiFaturamento) kpiFaturamento.textContent = formatarMoeda(totalFaturamento);

    } catch (err) {
        console.error('Erro ao carregar pedidos:', err);
        if (tbody) {
            tbody.innerHTML = '<tr><td colspan="6" style="text-align:center; color:#DC2626; padding: 25px;">Erro ao carregar pedidos. Tente atualizar a página.</td></tr>';
        }
    }
}

/**
 * 2. SEÇÃO DO CARDÁPIO: TAMANHOS & ADICIONAIS + KPI DE ITENS
 */
let cacheTamanhos = [];
let cacheAdicionais = [];

async function carregarCardapio() {
    const kpiItensCardapio = document.getElementById('kpi-itens-cardapio');
    let totalItens = 0;

    const [tamanhosRes, adicionaisRes] = await Promise.allSettled([
        fetch('/admin/api/tamanhos').then(r => r.json()),
        fetch('/admin/api/adicionais').then(r => r.json())
    ]);

    // Renderizar Produtos (Açaí, Copos e Tigelas)
    const tbodyTamanhos = document.getElementById('tamanhos-tbody');
    if (tbodyTamanhos && tamanhosRes.status === 'fulfilled') {
        const tamanhos = tamanhosRes.value;
        if (Array.isArray(tamanhos)) {
            cacheTamanhos = tamanhos;
            totalItens += tamanhos.length;
            if (tamanhos.length === 0) {
                tbodyTamanhos.innerHTML = '<tr><td colspan="4" style="text-align:center; color:var(--text-muted); padding:20px;">Nenhum produto cadastrado.</td></tr>';
            } else {
                tamanhos.sort((a, b) => (Number(a.preco) || 0) - (Number(b.preco) || 0));
                tbodyTamanhos.innerHTML = tamanhos.map(t => {
                    const foto = t.imagem || '/images/acai_copo_perfeito.jpg';
                    const volumeStr = t.volume ? `<span class="badge-volume">${t.volume}</span>` : '';
                    return `
                        <tr class="table-row-card card-produto">
                            <td class="col-thumb">
                                <img src="${foto}" alt="${t.nome}" class="admin-product-thumb" onerror="this.src='/images/acai_copo_perfeito.jpg'">
                            </td>
                            <td class="col-prod-info">
                                <strong>${t.nome}</strong>
                                <div>${volumeStr}</div>
                            </td>
                            <td class="col-prod-price">
                                <span class="price-val">${formatarMoeda(t.preco)}</span>
                            </td>
                            <td class="col-actions">
                                <div class="table-actions-row">
                                    <button type="button" onclick="abrirModalEditarProduto('${t.id}')" class="btn-table-icon btn-edit" title="Editar produto">
                                        <i class="fa-solid fa-pen-to-square"></i>
                                    </button>
                                    <button type="button" onclick="removerTamanho('${t.id}')" class="btn-table-icon btn-delete" title="Excluir produto">
                                        <i class="fa-solid fa-trash"></i>
                                    </button>
                                </div>
                            </td>
                        </tr>
                    `;
                }).join('');
            }
        }
    }

    // Renderizar Adicionais
    const tbodyAdicionais = document.getElementById('adicionais-tbody');
    if (tbodyAdicionais && adicionaisRes.status === 'fulfilled') {
        const adicionais = adicionaisRes.value;
        if (Array.isArray(adicionais)) {
            cacheAdicionais = adicionais;
            totalItens += adicionais.length;
            if (adicionais.length === 0) {
                tbodyAdicionais.innerHTML = '<tr><td colspan="3" style="text-align:center; color:var(--text-muted); padding:20px;">Nenhum adicional cadastrado.</td></tr>';
            } else {
                adicionais.sort((a, b) => a.nome.localeCompare(b.nome));
                tbodyAdicionais.innerHTML = adicionais.map(a => `
                    <tr class="table-row-card card-adicional">
                        <td class="col-topping-info">
                            <strong>${a.nome}</strong>
                        </td>
                        <td class="col-topping-price">
                            <span class="price-val-green">${formatarMoeda(a.preco)}</span>
                        </td>
                        <td class="col-actions">
                            <div class="table-actions-row">
                                <button type="button" onclick="abrirModalEditarAdicional('${a.id}')" class="btn-table-icon btn-edit" title="Editar adicional">
                                    <i class="fa-solid fa-pen-to-square"></i>
                                </button>
                                <button type="button" onclick="removerAdicional('${a.id}')" class="btn-table-icon btn-delete" title="Excluir adicional">
                                    <i class="fa-solid fa-trash"></i>
                                </button>
                            </div>
                        </td>
                    </tr>
                `).join('');
            }
        }
    }

    if (kpiItensCardapio) {
        kpiItensCardapio.textContent = totalItens;
    }
}

/**
 * 3. SEÇÃO DE USUÁRIOS + KPI DE TOTAL DE USUÁRIOS
 */
async function carregarUsuarios() {
    const tbody = document.getElementById('usuarios-tbody');
    const subInfo = document.getElementById('usuarios-sub-info');
    const kpiTotalUsuarios = document.getElementById('kpi-total-usuarios');

    try {
        const res = await fetch('/admin/api/usuarios');
        if (!res.ok) throw new Error(`Status ${res.status}`);
        const usuarios = await res.json();

        if (!Array.isArray(usuarios) || usuarios.length === 0) {
            if (tbody) {
                tbody.innerHTML = '<tr><td colspan="5" style="text-align:center; color:var(--text-muted); padding:30px;">Nenhum usuário cadastrado.</td></tr>';
            }
            if (subInfo) subInfo.textContent = '0 usuários cadastrados';
            if (kpiTotalUsuarios) kpiTotalUsuarios.textContent = '0';
            return;
        }

        if (kpiTotalUsuarios) kpiTotalUsuarios.textContent = usuarios.length;

        if (subInfo) {
            const admins = usuarios.filter(u => u.tipoDeUsuario === 'ADMINISTRADOR').length;
            const clientes = usuarios.length - admins;
            subInfo.textContent = `${usuarios.length} usuários (${admins} admin${admins > 1 ? 's' : ''}, ${clientes} cliente${clientes > 1 ? 's' : ''})`;
        }

        usuarios.sort((a, b) => a.nome.localeCompare(b.nome));

        const rowsHtml = usuarios.map(u => {
            const isAdmin = u.tipoDeUsuario === 'ADMINISTRADOR';
            const typeBadge = isAdmin
                ? '<span class="badge-user-type admin"><i class="fa-solid fa-shield-halved"></i> Administrador</span>'
                : '<span class="badge-user-type cliente"><i class="fa-regular fa-user"></i> Cliente</span>';

            const statusStyle = u.status === 'ATIVO'
                ? 'color: var(--color-green); font-weight: 600;'
                : 'color: var(--text-muted);';

            const promoteBtn = !isAdmin
                ? `<button type="button" onclick="promoverUsuario('${u.id}')" class="btn-table-icon btn-promote" title="Promover a Administrador"><i class="fa-solid fa-arrow-up"></i></button>`
                : '';

            return `
                <tr class="table-row-card card-usuario">
                    <td class="col-user-name">
                        <strong>${u.nome}</strong>
                        <span class="user-status-tag-mobile">${typeBadge}</span>
                    </td>
                    <td class="col-user-email">
                        <i class="fa-regular fa-envelope" style="opacity: 0.6; margin-right: 4px;"></i>${u.email}
                    </td>
                    <td class="col-user-type cell-status-desktop">${typeBadge}</td>
                    <td class="col-user-status"><span style="${statusStyle}">${u.status || 'ATIVO'}</span></td>
                    <td class="col-actions">
                        <div class="table-actions-row">
                            ${promoteBtn}
                            <button type="button" onclick="excluirUsuario('${u.id}')" class="btn-table-icon btn-delete" title="Excluir usuário">
                                <i class="fa-solid fa-trash"></i>
                            </button>
                        </div>
                    </td>
                </tr>
            `;
        }).join('');

        if (tbody) tbody.innerHTML = rowsHtml;

    } catch (err) {
        console.error('Erro ao carregar usuários:', err);
        if (tbody) {
            tbody.innerHTML = '<tr><td colspan="5" style="text-align:center; color:#DC2626; padding:25px;">Erro ao carregar usuários.</td></tr>';
        }
    }
}

/**
 * AÇÕES OPERACIONAIS (PEDIDOS, TAMANHOS, ADICIONAIS, USUÁRIOS)
 */

// Marcar Pedido como Entregue
async function alterarStatusPedido(id) {
    if (!confirm("Deseja marcar este pedido como ENTREGUE?")) return;

    try {
        const res = await fetch(`/admin/api/pedidos/${id}/status`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include'
        });

        if (!res.ok) {
            const text = await res.text();
            throw new Error(text || 'Falha ao atualizar pedido');
        }

        await carregarPedidos();
    } catch (err) {
        console.error(err);
        alert('Erro ao atualizar status do pedido: ' + err.message);
    }
}

// Remover Tamanho
async function removerTamanho(id) {
    if (!confirm('Tem certeza de que deseja excluir este tamanho de açaí?')) return;

    try {
        const res = await fetch(`/admin/api/tamanhos/${id}`, {
            method: 'DELETE',
            credentials: 'include'
        });

        const data = await res.json().catch(() => ({}));
        if (!res.ok) throw new Error(data.mensagem || 'Falha ao remover tamanho');

        await carregarCardapio();
    } catch (err) {
        console.error(err);
        alert('Erro ao excluir tamanho: ' + err.message);
    }
}

// Remover Adicional
async function removerAdicional(id) {
    if (!confirm('Tem certeza de que deseja excluir este adicional?')) return;

    try {
        const res = await fetch(`/admin/api/adicionais/${id}`, {
            method: 'DELETE',
            credentials: 'include'
        });

        const data = await res.json().catch(() => ({}));
        if (!res.ok) throw new Error(data.mensagem || 'Falha ao remover adicional');

        await carregarCardapio();
    } catch (err) {
        console.error(err);
        alert('Erro ao excluir adicional: ' + err.message);
    }
}

// Promover Usuário a Administrador
async function promoverUsuario(id) {
    if (!confirm('Tem certeza de que deseja promover este usuário a Administrador?')) return;

    try {
        const res = await fetch(`/admin/api/usuarios/${id}/promover`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include'
        });

        if (!res.ok) {
            const text = await res.text();
            throw new Error(text || 'Falha ao promover usuário');
        }

        alert('Usuário promovido a Administrador com sucesso!');
        await carregarUsuarios();
    } catch (err) {
        console.error(err);
        alert('Erro ao promover usuário: ' + err.message);
    }
}

// Desativar / Excluir Usuário
async function excluirUsuario(id) {
    if (!confirm('Tem certeza de que deseja remover/desativar este usuário?')) return;

    try {
        const res = await fetch(`/admin/api/usuarios/${id}`, {
            method: 'DELETE',
            credentials: 'include'
        });

        const data = await res.json().catch(() => ({}));
        if (!res.ok) throw new Error(data.mensagem || 'Falha ao excluir usuário');

        alert(data.mensagem || 'Usuário removido com sucesso.');
        await carregarUsuarios();
    } catch (err) {
        console.error(err);
        alert('Erro ao remover usuário: ' + err.message);
    }
}

/**
 * RETROCOMPATIBILIDADE (para evitar erros caso algum script antigo invoque)
 */
function atualizarUsuarios() { carregarUsuarios(); }
function atualizarPedidos() { carregarPedidos(); }
function atualizarUsuariosEPedidos() { carregarUsuarios(); carregarPedidos(); }
function loadContent(section) { carregarTudo(); }

// Inicialização automática ao carregar o DOM
window.addEventListener('DOMContentLoaded', () => {
    carregarTudo();

    // Auto-atualização periódica a cada 8 segundos (apenas quando a aba está ativa)
    if (pollingInterval) clearInterval(pollingInterval);
    pollingInterval = setInterval(() => {
        if (!document.hidden) {
            carregarPedidos();
        }
    }, 8000);
});

/* ==========================================================================
   MODAIS FLUTUANTES DO PAINEL ADMINISTRATIVO (ESTILO MONTE SEU AÇAÍ)
   ========================================================================== */

function abrirModalNovoProduto() {
    const modal = document.getElementById('modal-admin-produto');
    const form = document.getElementById('form-admin-produto');
    const title = document.getElementById('modal-admin-produto-title');
    const btnSubmit = document.getElementById('admin-prod-btn-submit');
    if (!modal || !form) return;

    if (title) title.innerHTML = '<i class="fa-solid fa-bowl-food" style="margin-right: 8px; color: var(--color-pink, #E11D48);"></i> Cadastrar Novo Produto';
    form.action = '/admin/tamanhos/salvar';
    
    document.getElementById('admin-prod-id').value = '';
    document.getElementById('admin-prod-nome').value = '';
    document.getElementById('admin-prod-volume').value = '';
    document.getElementById('admin-prod-preco').value = '';
    document.getElementById('admin-prod-descricao').value = '';

    const defaultImg = '/images/acai_copo_perfeito.jpg';
    const hiddenImg = document.getElementById('admin-prod-imagem');
    if (hiddenImg) hiddenImg.value = defaultImg;

    const preview = document.getElementById('admin-prod-img-preview');
    if (preview) preview.src = defaultImg;
    const previewText = document.getElementById('admin-prod-img-preview-text');
    if (previewText) previewText.textContent = defaultImg;

    document.querySelectorAll('#admin-image-presets .image-preset-option').forEach(el => {
        el.classList.toggle('selected', el.getAttribute('data-img') === defaultImg);
    });

    if (btnSubmit) btnSubmit.innerHTML = '<i class="fa-solid fa-floppy-disk"></i> Salvar Produto';
    modal.classList.add('active');
    document.body.style.overflow = 'hidden';
}

function abrirModalEditarProduto(id) {
    const modal = document.getElementById('modal-admin-produto');
    const form = document.getElementById('form-admin-produto');
    const title = document.getElementById('modal-admin-produto-title');
    const btnSubmit = document.getElementById('admin-prod-btn-submit');
    if (!modal || !form) return;

    const item = cacheTamanhos.find(t => String(t.id) === String(id));
    if (!item) return;

    if (title) title.innerHTML = '<i class="fa-solid fa-pen-to-square" style="margin-right: 8px; color: var(--color-purple, #66023C);"></i> Editar Produto';
    form.action = '/admin/tamanhos/atualizar';

    document.getElementById('admin-prod-id').value = item.id;
    document.getElementById('admin-prod-nome').value = item.nome || '';
    document.getElementById('admin-prod-volume').value = item.volume || '';
    document.getElementById('admin-prod-preco').value = item.preco || '';
    document.getElementById('admin-prod-descricao').value = item.descricao || '';

    const img = item.imagem || '/images/acai_copo_perfeito.jpg';
    const hiddenImg = document.getElementById('admin-prod-imagem');
    if (hiddenImg) hiddenImg.value = img;

    const preview = document.getElementById('admin-prod-img-preview');
    if (preview) preview.src = img;
    const previewText = document.getElementById('admin-prod-img-preview-text');
    if (previewText) previewText.textContent = img;

    document.querySelectorAll('#admin-image-presets .image-preset-option').forEach(el => {
        el.classList.toggle('selected', el.getAttribute('data-img') === img);
    });

    if (btnSubmit) btnSubmit.innerHTML = '<i class="fa-solid fa-floppy-disk"></i> Salvar Alterações';
    modal.classList.add('active');
    document.body.style.overflow = 'hidden';
}

function fecharModalAdminProduto() {
    const modal = document.getElementById('modal-admin-produto');
    if (modal) modal.classList.remove('active');
    document.body.style.overflow = '';
}

function abrirModalNovoAdicional() {
    const modal = document.getElementById('modal-admin-adicional');
    const form = document.getElementById('form-admin-adicional');
    const title = document.getElementById('modal-admin-adic-title');
    const btnSubmit = document.getElementById('admin-adic-btn-submit');
    if (!modal || !form) return;

    if (title) title.innerHTML = '<i class="fa-solid fa-seedling" style="margin-right: 8px; color: var(--color-green, #10B981);"></i> Cadastrar Novo Adicional';
    form.action = '/admin/adicionais/salvar';

    document.getElementById('admin-adic-id').value = '';
    document.getElementById('admin-adic-nome').value = '';
    document.getElementById('admin-adic-preco').value = '';

    if (btnSubmit) btnSubmit.innerHTML = '<i class="fa-solid fa-floppy-disk"></i> Salvar Adicional';
    modal.classList.add('active');
    document.body.style.overflow = 'hidden';
}

function abrirModalEditarAdicional(id) {
    const modal = document.getElementById('modal-admin-adicional');
    const form = document.getElementById('form-admin-adicional');
    const title = document.getElementById('modal-admin-adic-title');
    const btnSubmit = document.getElementById('admin-adic-btn-submit');
    if (!modal || !form) return;

    const item = cacheAdicionais.find(a => String(a.id) === String(id));
    if (!item) return;

    if (title) title.innerHTML = '<i class="fa-solid fa-pen-to-square" style="margin-right: 8px; color: var(--color-green, #10B981);"></i> Editar Adicional';
    form.action = '/admin/adicionais/atualizar';

    document.getElementById('admin-adic-id').value = item.id;
    document.getElementById('admin-adic-nome').value = item.nome || '';
    document.getElementById('admin-adic-preco').value = item.preco || '';

    if (btnSubmit) btnSubmit.innerHTML = '<i class="fa-solid fa-floppy-disk"></i> Salvar Alterações';
    modal.classList.add('active');
    document.body.style.overflow = 'hidden';
}

function fecharModalAdminAdicional() {
    const modal = document.getElementById('modal-admin-adicional');
    if (modal) modal.classList.remove('active');
    document.body.style.overflow = '';
}

function fecharModalAdminFora(event, modalId) {
    if (event.target.id === modalId) {
        const modal = document.getElementById(modalId);
        if (modal) modal.classList.remove('active');
        document.body.style.overflow = '';
    }
}

function selecionarPresetModal(el) {
    document.querySelectorAll('#admin-image-presets .image-preset-option').forEach(o => o.classList.remove('selected'));
    el.classList.add('selected');
    const imgUrl = el.getAttribute('data-img');
    const hidden = document.getElementById('admin-prod-imagem');
    if (hidden) hidden.value = imgUrl;

    const preview = document.getElementById('admin-prod-img-preview');
    if (preview) preview.src = imgUrl;
    const previewText = document.getElementById('admin-prod-img-preview-text');
    if (previewText) previewText.textContent = imgUrl;
}

function lidarComUploadModalProduto(input) {
    if (input.files && input.files[0]) {
        const file = input.files[0];
        const reader = new FileReader();
        reader.onload = function(e) {
            const preview = document.getElementById('admin-prod-img-preview');
            if (preview) preview.src = e.target.result;
            const previewText = document.getElementById('admin-prod-img-preview-text');
            if (previewText) previewText.textContent = 'Arquivo selecionado: ' + file.name;
            document.querySelectorAll('#admin-image-presets .image-preset-option').forEach(o => o.classList.remove('selected'));
        };
        reader.readAsDataURL(file);
    }
}

// Fechar modais com tecla Escape
document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape') {
        fecharModalAdminProduto();
        fecharModalAdminAdicional();
    }
});
