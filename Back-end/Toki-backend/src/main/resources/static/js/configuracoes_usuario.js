// configuracoes_usuario.js
(function () {
  const API_BASE = "http://localhost:8080/usuario"; // ajuste se necessário

  document.addEventListener('DOMContentLoaded', init);

  function init() {
    setupTabs();
    cacheElements();
    bindUI();
    loadUser();
  }

  // ----- cache UI -----
  let fotoInput, fotoUser, fotoSidebar;
  let nomeInput, emailInput, telefoneInput, idiomaSelect;
  let salvarContaBtn, notifToggle, metodoLembrete, antecedencia, salvarNot;
  let radiosTema, colorDots, salvarApar;
  let twoFaToggle, sessaoList;
  let logoutBtn;

  function cacheElements() {
    fotoInput = document.getElementById('inputFoto');
    fotoUser = document.getElementById('fotoUsuario');
    fotoSidebar = document.getElementById('fotoPerfil');

    nomeInput = document.getElementById('nome');
    emailInput = document.getElementById('email');
    telefoneInput = document.getElementById('telefone');
    idiomaSelect = document.getElementById('idioma');
    salvarContaBtn = document.getElementById('salvarConta');

    notifToggle = document.getElementById('notifToggle');
    metodoLembrete = document.getElementById('metodoLembrete');
    antecedencia = document.getElementById('antecedencia');
    salvarNot = document.getElementById('salvarNot');

    radiosTema = document.querySelectorAll("input[name='tema']");
    colorDots = document.querySelectorAll('.color-dot');
    salvarApar = document.getElementById('salvarApar');

    twoFaToggle = document.getElementById('twoFaToggle');
    sessaoList = document.getElementById('sessaoList');

    logoutBtn = document.getElementById('logoutBtn');
  }

  // ----- tabs -----
  function setupTabs() {
    const tabs = document.querySelectorAll('.tab-btn');
    const panels = document.querySelectorAll('.tab-panel');
    tabs.forEach(btn => {
      btn.addEventListener('click', () => {
        tabs.forEach(b => b.classList.remove('tab-active'));
        tabs.forEach(b => b.setAttribute('aria-selected','false'));
        btn.classList.add('tab-active');
        btn.setAttribute('aria-selected','true');

        panels.forEach(p => {
          p.classList.add('hidden');
          p.setAttribute('aria-hidden','true');
        });

        const id = btn.dataset.tab;
        const target = document.getElementById(id);
        if (target) {
          target.classList.remove('hidden');
          target.setAttribute('aria-hidden','false');
        }
      });
    });
  }

  // ----- bind UI events -----
  function bindUI() {
    // Foto
    if (fotoUser && fotoInput) {
      fotoUser.addEventListener('click', () => fotoInput.click());
      fotoInput.addEventListener('change', onFotoChange);
    }

    // Salvar conta
    if (salvarContaBtn) salvarContaBtn.addEventListener('click', salvarConta);

    // Notificações
    if (salvarNot) salvarNot.addEventListener('click', salvarNotificacoes);

    // Aparência
    if (radiosTema) radiosTema.forEach(r => r.addEventListener('change', onTemaChange));
    if (colorDots) colorDots.forEach(d => d.addEventListener('click', onColorClick));
    if (salvarApar) salvarApar.addEventListener('click', () => alert('✔ Aparência salva!'));

    // Privacidade
    if (twoFaToggle) twoFaToggle.addEventListener('change', toggle2FA);

    // Logout
    if (logoutBtn) logoutBtn.addEventListener('click', logout);
  }

  // ----- load user from backend -----
  async function loadUser() {
    try {
      const res = await fetch(`${API_BASE}/logado`, { credentials: 'include' });
      if (res.status === 401) {
        // redirect to login page (adjust path)
        window.location.href = '/login.html';
        return;
      }
      const usuario = await res.json();
      applyUserToUI(usuario);
    } catch (err) {
      console.error('Erro ao carregar usuário', err);
    }
  }

  function applyUserToUI(u) {
    if (!u) return;

    // fill inputs
    if (nomeInput) nomeInput.value = u.nome || '';
    if (emailInput) emailInput.value = u.email || '';
    if (telefoneInput) telefoneInput.value = u.telefone || '';
    if (idiomaSelect) idiomaSelect.value = u.idioma || 'pt-BR';

    // sidebar
    const nomeSidebar = document.getElementById('nomeSidebar');
    const emailSidebar = document.getElementById('emailSidebar');
    if (nomeSidebar) nomeSidebar.textContent = u.nome || 'Usuário TOKI';
    if (emailSidebar) emailSidebar.textContent = u.email || '';

    // foto
    if (u.fotoPerfil) {
      const url = absolutePath(u.fotoPerfil);
      if (fotoUser) fotoUser.src = url;
      if (fotoSidebar) fotoSidebar.src = url;
    }

    // notificações
    if (notifToggle) notifToggle.checked = !!u.notif;
    if (metodoLembrete) metodoLembrete.value = u.metodo || 'app';
    if (antecedencia) antecedencia.value = u.antecedencia || '30 minutos';

    // tema
    const tema = u.tema || 'claro';
    if (tema === 'escuro') document.documentElement.setAttribute('data-theme','dark');
    else if (tema === 'claro') document.documentElement.setAttribute('data-theme','light');
    else {
      document.documentElement.setAttribute('data-theme', window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light');
    }
    if (radiosTema) radiosTema.forEach(r => r.checked = (r.value === tema));

    // cor principal
    if (u.corPrincipal) document.documentElement.style.setProperty('--toki-primary', u.corPrincipal);

    // 2FA
    if (twoFaToggle) twoFaToggle.checked = !!u.doisFatores;

    // sessões: se houver lista (mock no backend) — aqui mostramos o que vier em u.sessoes
    renderSessaoList(u.sessoes || []);
  }

  function renderSessaoList(sessoes) {
    if (!sessaoList) return;
    sessaoList.innerHTML = '';
    if (sessoes.length === 0) {
      const li = document.createElement('li');
      li.className = "text-sm text-slate-500";
      li.textContent = "Nenhuma sessão ativa encontrada.";
      sessaoList.appendChild(li);
      return;
    }
    sessoes.forEach((s, idx) => {
      const li = document.createElement('li');
      li.className = "flex items-center justify-between";
      li.innerHTML = `
        <div>
          <div class="font-medium">${escapeHtml(s.device || 'Desconhecido')}</div>
          <div class="text-xs opacity-70">${escapeHtml(s.location || '')} · ${escapeHtml(s.time || '')}</div>
        </div>
      `;
      const btn = document.createElement('button');
      btn.className = "px-3 py-1 rounded-lg border text-sm";
      btn.textContent = "Encerrar";
      btn.addEventListener('click', () => {
        // aqui apenas remove visualmente e chama endpoint (opcional)
        if (confirm('Encerrar sessão?')) {
          // opcional: chamar backend para encerrar sessão por id (se suportado)
          if (s.id) {
            fetch(`${API_BASE}/encerrar-sessao/${s.id}`, { method: 'DELETE', credentials: 'include' })
              .then(() => { li.remove(); });
          } else {
            li.remove();
          }
        }
      });
      li.appendChild(btn);
      sessaoList.appendChild(li);
    });
  }

  // ----- foto upload -----
  async function onFotoChange() {
    const file = fotoInput.files && fotoInput.files[0];
    if (!file) return;
    // preview imediata
    const previewUrl = URL.createObjectURL(file);
    if (fotoUser) fotoUser.src = previewUrl;
    if (fotoSidebar) fotoSidebar.src = previewUrl;

    // upload para backend
    try {
      const fd = new FormData();
      fd.append('fotoPerfil', file);
      const res = await fetch(`${API_BASE}/upload`, {
        method: 'POST',
        credentials: 'include',
        body: fd
      });
      if (!res.ok) throw new Error('Upload falhou');
      const data = await res.json(); // { caminho: "uploads/perfil/..." }
      const url = absolutePath(data.caminho);
      if (fotoUser) fotoUser.src = url;
      if (fotoSidebar) fotoSidebar.src = url;
      alert('✔ Foto atualizada');
    } catch (err) {
      console.error(err);
      alert('Erro ao enviar a foto');
    }
  }

  // ----- salvar conta (envia para backend) -----
  async function salvarConta() {
    const payload = {
      nome: nomeInput.value,
      email: emailInput.value,
      telefone: telefoneInput.value,
      idioma: idiomaSelect.value
    };
    try {
      const res = await fetch(`${API_BASE}/atualizar`, {
        method: 'POST',
        credentials: 'include',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
      if (!res.ok) throw new Error('Falha ao salvar');
      const updated = await res.json();
      // atualizar sidebar
      const nomeSidebar = document.getElementById('nomeSidebar');
      const emailSidebar = document.getElementById('emailSidebar');
      if (nomeSidebar) nomeSidebar.textContent = updated.nome || nomeInput.value;
      if (emailSidebar) emailSidebar.textContent = updated.email || emailInput.value;
      alert('✔ Dados atualizados');
    } catch (err) {
      console.error(err);
      alert('Erro ao salvar dados');
    }
  }

  // ----- notificações -----
  async function salvarNotificacoes() {
    const payload = {
      notif: !!notifToggle.checked,
      metodo: metodoLembrete.value,
      antecedencia: antecedencia.value
    };
    try {
      const res = await fetch(`${API_BASE}/atualizar`, {
        method: 'POST',
        credentials: 'include',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
      if (!res.ok) throw new Error('Falha');
      alert('✔ Notificações atualizadas');
    } catch (err) {
      console.error(err);
      alert('Erro ao salvar notificações');
    }
  }

  // ----- tema -----
  async function onTemaChange(e) {
    const v = e.target.value;
    if (v === 'escuro') document.documentElement.setAttribute('data-theme','dark');
    else if (v === 'claro') document.documentElement.setAttribute('data-theme','light');
    else {
      // automático: respeita preferências do sistema
      const prefersDark = window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches;
      document.documentElement.setAttribute('data-theme', prefersDark ? 'dark' : 'light');
    }

    // salvar no backend
    try {
      await fetch(`${API_BASE}/atualizar`, {
        method: 'POST',
        credentials: 'include',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ tema: v })
      });
    } catch (err) {
      console.warn('Não foi possível salvar tema', err);
    }
  }

  // cor principal clique
  async function onColorClick(e) {
    const color = e.currentTarget.dataset.color;
    if (!color) return;
    document.documentElement.style.setProperty('--toki-primary', color);

    colorDots.forEach(d => d.classList.remove('ring-2'));
    e.currentTarget.classList.add('ring-2');

    try {
      await fetch(`${API_BASE}/atualizar`, {
        method: 'POST',
        credentials: 'include',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ corPrincipal: color })
      });
    } catch (err) {
      console.warn('Erro ao salvar cor', err);
    }
  }

  // ----- 2FA -----
  async function toggle2FA() {
    const enabled = twoFaToggle.checked;
    try {
      await fetch(`${API_BASE}/atualizar`, {
        method: 'POST',
        credentials: 'include',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ doisFatores: enabled })
      });
      alert(`2FA ${enabled ? 'ativada' : 'desativada'}`);
    } catch (err) {
      console.error(err);
      alert('Erro ao atualizar 2FA');
    }
  }

  // ----- logout -----
  async function logout() {
    if (!confirm('Deseja sair?')) return;
    try {
      // seu servlet suporta DELETE /logout -> pathInfo "/logout"
      const res = await fetch(`${API_BASE}/logout`, {
        method: 'DELETE',
        credentials: 'include'
      });
      if (res.ok) {
        window.location.href = '/login.html';
      } else {
        alert('Erro ao sair');
      }
    } catch (err) {
      console.error(err);
    }
  }

  // helpers
  function absolutePath(caminhoRelativo) {
    if (!caminhoRelativo) return caminhoRelativo;
    // caso backend retorne "uploads/perfil/arquivo.jpg"
    if (/^https?:\/\//.test(caminhoRelativo)) return caminhoRelativo;
    // remove leading slash duplicates
    const base = window.location.origin;
    return base.replace(/:3000$|:5500$/, '') + '/' + caminhoRelativo.replace(/^\/+/, '');
  }

  function escapeHtml(s) {
    if (!s) return '';
    return String(s).replace(/[&<>"']/g, function (m) { return ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[m]); });
  }

})();
