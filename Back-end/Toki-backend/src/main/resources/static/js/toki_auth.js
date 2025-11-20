// toki_auth.js
document.addEventListener('DOMContentLoaded', () => {

  // =========================
  // Mostrar / ocultar senha
  // =========================
  document.querySelectorAll('.toggle-password').forEach(icon => {
    icon.addEventListener('click', () => {
      const targetId = icon.dataset.target;
      const input = document.getElementById(targetId);
      if (!input) return;

      input.type = input.type === 'password' ? 'text' : 'password';
      icon.textContent = input.type === 'password' ? 'visibility_off' : 'visibility';
    });
  });

  // =========================
  // Mensagem de feedback
  // =========================
  const showMessage = (msg, isError = true) => {
    const alertBox = document.createElement('div');
    alertBox.textContent = msg;
    alertBox.style.color = isError ? 'red' : 'green';
    alertBox.style.fontWeight = 'bold';
    alertBox.style.marginTop = '10px';
    document.querySelector('.auth-container').appendChild(alertBox);
    setTimeout(() => alertBox.remove(), 5000);
  };

  // =========================
  // LOGIN
  // =========================
  const formLogin = document.getElementById('formLogin');
  if (formLogin) {
    formLogin.addEventListener('submit', async (ev) => {
      ev.preventDefault();
      const email = document.getElementById('loginEmail')?.value.trim();
      const senha = document.getElementById('loginSenha')?.value.trim();
      if (!email || !senha) return showMessage('Preencha e-mail e senha.');

      try {
        const resp = await fetch('/usuario/login', {
          method: 'POST',
          credentials: 'include',
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
          body: new URLSearchParams({ email, senha })
        });

        const text = await resp.text();
        if (resp.ok && text.includes('sucesso')) {
          showMessage('✔ Login realizado!', false);
          setTimeout(() => window.location.href = '/tela_principal.html', 1000);
        } else {
          showMessage(text || 'Credenciais inválidas.');
        }
      } catch {
        showMessage('Falha na conexão com o servidor.');
      }
    });
  }

  // =========================
  // CADASTRO
  // =========================
  const formCadastro = document.getElementById('formCadastro');
  if (formCadastro) {
    formCadastro.addEventListener('submit', async (ev) => {
      ev.preventDefault();
      const nome = document.getElementById('cadNome')?.value.trim();
      const email = document.getElementById('cadEmail')?.value.trim();
      const senha = document.getElementById('cadSenha')?.value.trim();
      if (!nome || !email || !senha) return showMessage('Preencha nome, e-mail e senha.');

      try {
        const resp = await fetch('/usuario/cadastrar', {
          method: 'POST',
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
          body: new URLSearchParams({ nome, email, senha }),
          credentials: 'include'
        });

        const text = await resp.text();
        if (resp.ok || resp.status === 201) {
          showMessage('✔ Conta criada com sucesso!', false);
          setTimeout(() => window.location.href = '/login_cadastro/login.html', 1000);
        } else {
          showMessage(text || 'Erro ao cadastrar.');
        }
      } catch {
        showMessage('Falha na conexão com o servidor.');
      }
    });
  }

  // =========================
  // RECUPERAR SENHA
  // =========================
  const formRecuperar = document.getElementById('formRecuperar');
  if (formRecuperar) {
    formRecuperar.addEventListener('submit', async (ev) => {
      ev.preventDefault();
      const email = document.getElementById('emailRecuperar')?.value.trim();
      if (!email) return showMessage('Informe o e-mail.');

      try {
        const resp = await fetch('/usuario/gerarCodigo', {
          method: 'POST',
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
          body: new URLSearchParams({ email }),
          credentials: 'include'
        });

        const data = await resp.json().catch(() => ({}));

        if (resp.ok) {
          localStorage.setItem('emailRecuperacao', email);
          showMessage('✔ Código enviado! Verifique seu e-mail.', false);
          setTimeout(() => window.location.href = '/login_cadastro/redefinir.html', 1500);
        } else {
          showMessage(data.erro || 'Falha ao enviar código.');
        }
      } catch {
        showMessage('Falha na conexão com o servidor.');
      }
    });
  }

  // =========================
  // REDEFINIR SENHA
  // =========================
  const formRedefinir = document.getElementById('formRedefinir');
  if (formRedefinir) {
    formRedefinir.addEventListener('submit', async (ev) => {
      ev.preventDefault();
      const codigo = document.getElementById('codigo')?.value.trim();
      const novaSenha = document.getElementById('novaSenha')?.value.trim();
      const email = localStorage.getItem('emailRecuperacao');
      if (!codigo || !novaSenha || !email) return showMessage('Preencha todos os campos.');

      try {
        const resp = await fetch('/usuario/redefinirSenha', {
          method: 'POST',
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
          body: new URLSearchParams({ email, codigo, senha: novaSenha }),
          credentials: 'include'
        });

        const data = await resp.json().catch(() => ({}));

        if (resp.ok) {
          showMessage('✔ Senha redefinida com sucesso!', false);
          localStorage.removeItem('emailRecuperacao');
          setTimeout(() => window.location.href = '/login_cadastro/login.html', 1500);
        } else {
          showMessage(data.erro || 'Código inválido ou expirado.');
        }
      } catch {
        showMessage('Falha na conexão com o servidor.');
      }
    });
  }

  // =========================
  // Submit com Enter
  // =========================
  document.querySelectorAll('.formulario input').forEach(i => {
    i.addEventListener('keydown', e => {
      if (e.key === 'Enter') i.closest('form')?.requestSubmit();
    });
  });

});
