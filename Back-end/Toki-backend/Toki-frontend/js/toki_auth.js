window.API_BASE = "http://localhost:8080/toki";

// toki_auth.js
document.addEventListener('DOMContentLoaded', () => {
    initToggleSenha();
    initFormCadastro();
    initFormLogin();
    initFormRecuperar();
    initFormRedefinir();
    initFormValidacao();
});

/* =========================================================
    TOGGLE DE SENHA
========================================================= */
function initToggleSenha() {
    const toggles = document.querySelectorAll('.toggle-password');
    toggles.forEach(toggle => {
        toggle.addEventListener('click', () => {
            const input = document.getElementById(toggle.dataset.target);
            if (!input) return;
            input.type = input.type === 'password' ? 'text' : 'password';
            toggle.textContent = (input.type === 'password') ? 'visibility_off' : 'visibility';
        });
    });
}

/* =========================================================
    CADASTRO
========================================================= */
function initFormCadastro() {
    const form = document.getElementById('formCadastro');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const data = {
            nome: form.querySelector('[name="nome"]').value,
            email: form.querySelector('[name="email"]').value,
            senha: form.querySelector('[name="senha"]').value
        };

        try {
            const response = await fetch(`${window.API_BASE}/usuario/cadastrar`, {
                method: 'POST',
                headers: { "Content-Type": "application/x-www-form-urlencoded" },
                body: new URLSearchParams(data),
                credentials: "include"
            });

            let json = await safeParseJSON(response);

            if (response.ok) {
                alert(json.mensagem || "Cadastro realizado!");
                window.location.href = "login.html";
            } else {
                alert(json.erro || "Erro ao cadastrar");
            }
        } catch (err) {
            alert("Erro ao cadastrar: " + err.message);
        }
    });
}

/* =========================================================
    LOGIN
========================================================= */
function initFormLogin() {
    const form = document.getElementById('formLogin');
    if (!form) return;

    const msg = document.getElementById('mensagemLogin');

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const data = {
            email: form.querySelector('[name="email"]').value,
            senha: form.querySelector('[name="senha"]').value
        };

        try {
            const response = await fetch(`${window.API_BASE}/usuario/login`, {
                method: "POST",
                headers: { "Content-Type": "application/x-www-form-urlencoded" },
                body: new URLSearchParams(data),
                credentials: "include"
            });

            let json = await safeParseJSON(response);

            if (response.ok) {
                msg.textContent = "✔ Login realizado!";
                setTimeout(() => {
                    window.location.href = "../tela_principal/tela_principal.html";
                }, 700);
            } else {
                msg.textContent = json.erro || "Usuário ou senha inválidos";
            }
        } catch (err) {
            msg.textContent = "Erro ao logar: " + err.message;
        }
    });
}

/* =========================================================
    RECUPERAR SENHA — ENVIAR CÓDIGO
========================================================= */
function initFormRecuperar() {
    const form = document.getElementById('formRecuperar');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const email = form.querySelector('input[type="email"]').value;

        try {
            const response = await fetch(`${window.API_BASE}/usuario/gerarCodigo`, {
                method: "POST",
                headers: { "Content-Type": "application/x-www-form-urlencoded" },
                body: new URLSearchParams({ email }),
                credentials: "include"
            });

            let json = await safeParseJSON(response);

            if (response.ok) {
                localStorage.setItem("emailRecuperacao", email);
                alert(json.mensagem || "Código enviado!");
                window.location.href = "redefinir.html";
            } else {
                alert(json.erro || "Erro ao enviar código.");
            }
        } catch (err) {
            alert("Erro: " + err.message);
        }
    });
}

/* =========================================================
    REDEFINIR SENHA
========================================================= */
function initFormRedefinir() {
    const form = document.getElementById('formRedefinir');
    if (!form) return;

    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        const codigo = form.querySelector('#codigo').value;
        const novaSenha = form.querySelector('#novaSenha').value;
        const email = localStorage.getItem("emailRecuperacao");

        if (!email) return alert("Solicite o código novamente.");

        try {
            const response = await fetch(`${window.API_BASE}/usuario/redefinirSenha`, {
                method: "POST",
                headers: { "Content-Type": "application/x-www-form-urlencoded" },
                body: new URLSearchParams({ email, codigo, senha: novaSenha }),
                credentials: "include"
            });

            let json = await safeParseJSON(response);

            if (response.ok) {
                alert(json.mensagem || "Senha redefinida!");
                localStorage.removeItem("emailRecuperacao");
                window.location.href = "login.html";
            } else {
                alert(json.erro || "Erro ao redefinir senha.");
            }
        } catch (err) {
            alert("Erro: " + err.message);
        }
    });
}

/* =========================================================
    TELA DE VALIDAÇÃO DE CÓDIGO
========================================================= */
function initFormValidacao() {
    const form = document.getElementById('formValidar');
    const msg = document.getElementById('mensagem');

    if (!form) return;

    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        const data = new FormData(form);

        try {
            const response = await fetch(`${window.API_BASE}/usuario/validarCodigo`, {
                method: "POST",
                body: data,
                credentials: "include"
            });

            const text = await response.text();
            msg.textContent = text;
        } catch (err) {
            msg.textContent = "Erro: " + err.message;
        }
    });
}

/* =========================================================
    FUNÇÃO UTIL — TENTA PARSE DE JSON COM SEGURANÇA
========================================================= */
async function safeParseJSON(response) {
    const text = await response.text();
    try { return JSON.parse(text); }
    catch { return { mensagem: text }; }
}
