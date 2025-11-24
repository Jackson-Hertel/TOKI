window.API_BASE = "http://localhost:8080/toki";

document.addEventListener('DOMContentLoaded', () => {
    initTabs();
    initPerfil();
    initConta();
    initNotificacoes();
    initAparencia();
    initPrivacidade();
    initLogout();
});

// ===================== TABS =====================
function initTabs() {
    const tabs = document.querySelectorAll('.tab-btn');
    const panels = document.querySelectorAll('.tab-panel');

    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            tabs.forEach(t => { t.classList.remove('tab-active'); t.setAttribute('aria-selected', 'false'); });
            tab.classList.add('tab-active');
            tab.setAttribute('aria-selected', 'true');

            const target = tab.dataset.tab;
            panels.forEach(panel => {
                panel.id === target ? panel.classList.remove('hidden') : panel.classList.add('hidden');
            });
        });
    });
}

// ===================== PERFIL =====================
function initPerfil() {
    const fotoUsuario = document.getElementById('fotoUsuario');
    const inputFoto = document.getElementById('inputFoto');
    const fotoSidebar = document.getElementById('fotoPerfil');

    if (!fotoUsuario || !inputFoto) return;

    fotoUsuario.addEventListener('click', () => inputFoto.click());

    inputFoto.addEventListener('change', async () => {
        const file = inputFoto.files[0];
        if (!file) return;

        const formData = new FormData();
        formData.append('foto', file);

        try {
            const res = await fetch('/usuario/atualizarFoto', {
                method: 'POST',
                body: formData,
                credentials: 'include'
            });

            if (res.ok) {
                const url = URL.createObjectURL(file);
                fotoUsuario.src = url;
                if (fotoSidebar) fotoSidebar.src = url;
                alert('Foto atualizada com sucesso!');
            } else {
                alert('Erro ao atualizar a foto.');
            }
        } catch (err) {
            alert('Erro: ' + err.message);
        }
    });
}

// ===================== CONTA =====================
function initConta() {
    const btnSalvar = document.getElementById('salvarConta');
    const btnCancelar = document.getElementById('cancelarConta');

    if (!btnSalvar) return;

    btnSalvar.addEventListener('click', async () => {
        const data = {
            nome: document.getElementById('nome').value,
            email: document.getElementById('email').value,
            telefone: document.getElementById('telefone').value,
            idioma: document.getElementById('idioma').value
        };

        try {
            const res = await fetch('/usuario/atualizarConta', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data),
                credentials: 'include'
            });

            const result = await res.json();
            alert(result.message || 'Conta atualizada com sucesso!');
        } catch (err) {
            alert('Erro: ' + err.message);
        }
    });

    btnCancelar?.addEventListener('click', () => window.location.reload());
}

// ===================== NOTIFICAÇÕES =====================
function initNotificacoes() {
    const btnSalvar = document.getElementById('salvarNot');
    const btnCancelar = document.getElementById('cancelarNot');
    if (!btnSalvar) return;

    btnSalvar.addEventListener('click', async () => {
        const data = {
            receberLembretes: document.getElementById('notifToggle').checked,
            metodo: document.getElementById('metodoLembrete').value,
            antecedencia: document.getElementById('antecedencia').value
        };

        try {
            const res = await fetch('/usuario/atualizarNotificacoes', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data),
                credentials: 'include'
            });
            const result = await res.json();
            alert(result.message || 'Notificações atualizadas!');
        } catch (err) {
            alert('Erro: ' + err.message);
        }
    });

    btnCancelar?.addEventListener('click', () => window.location.reload());
}

// ===================== APARÊNCIA =====================
function initAparencia() {
    const btnSalvar = document.getElementById('salvarApar');
    const btnCancelar = document.getElementById('cancelarApar');
    if (!btnSalvar) return;

    btnSalvar.addEventListener('click', async () => {
        const tema = document.querySelector('input[name="tema"]:checked')?.value;
        const cor = document.querySelector('.color-dot.selected')?.dataset.color;

        const data = { tema, cor };

        try {
            const res = await fetch('/usuario/atualizarAparencia', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data),
                credentials: 'include'
            });
            const result = await res.json();
            alert(result.message || 'Aparência atualizada!');

            if (tema) document.documentElement.setAttribute('data-theme', tema);
            if (cor) document.documentElement.style.setProperty('--toki-primary', cor);
        } catch (err) {
            alert('Erro: ' + err.message);
        }
    });

    btnCancelar?.addEventListener('click', () => window.location.reload());

    // Seleção de cor
    const colorDots = document.querySelectorAll('.color-dot');
    colorDots.forEach(dot => {
        dot.addEventListener('click', () => {
            colorDots.forEach(d => d.classList.remove('selected'));
            dot.classList.add('selected');
        });
    });
}

// ===================== PRIVACIDADE =====================
function initPrivacidade() {
    const btnSalvar = document.getElementById('salvarPriv');
    const btnCancelar = document.getElementById('cancelarPriv');
    if (!btnSalvar) return;

    btnSalvar.addEventListener('click', async () => {
        const data = { twoFA: document.getElementById('twoFaToggle').checked };

        try {
            const res = await fetch('/usuario/atualizarPrivacidade', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data),
                credentials: 'include'
            });
            const result = await res.json();
            alert(result.message || 'Privacidade atualizada!');
        } catch (err) {
            alert('Erro: ' + err.message);
        }
    });

    btnCancelar?.addEventListener('click', () => window.location.reload());
}

// ===================== LOGOUT =====================
function initLogout() {
    const btn = document.getElementById('logoutBtn');
    if (!btn) return;

    btn.addEventListener('click', async () => {
        try {
            const res = await fetch('/usuario/logout', { method: 'POST', credentials: 'include' });
            if (res.ok) window.location.href = '../login_cadastro/login.html';
        } catch (err) {
            alert('Erro ao sair: ' + err.message);
        }
    });
}
