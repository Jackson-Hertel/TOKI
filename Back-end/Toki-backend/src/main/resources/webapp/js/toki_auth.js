/* ================================
    CONFIGURAÇÃO BASE DAS APIs
================================ */
(function () {
    const host = window.location.hostname;

    if (host === "localhost" || host === "127.0.0.1") {
        window.API_USUARIO = "http://localhost:8080/toki/usuario";
        window.API_EVENTO  = "http://localhost:8080/toki/evento";
    } else {
        window.API_USUARIO = "./toki/usuario";
        window.API_EVENTO  = "./toki/evento";
    }

    console.log("API_USUARIO:", window.API_USUARIO);
    console.log("API_EVENTO:", window.API_EVENTO);
})();

/* ================================
    INICIALIZAÇÃO
================================ */
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
    document.querySelectorAll('.toggle-password').forEach(toggle => {
        toggle.addEventListener('click', () => {
            const input = document.getElementById(toggle.dataset.target);
            if (!input) return;

            input.type = input.type === 'password' ? 'text' : 'password';
            toggle.textContent = input.type === 'password' ? 'visibility_off' : 'visibility';
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
            nome: form.nome.value,
            email: form.email.value,
            senha: form.senha.value
        };

        try {
            const response = await fetch(`${API_USUARIO}/cadastrar`, {
                method: 'POST',
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(data),
                credentials: "include"
            });

            const json = await safeParseJSON(response);

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
            email: form.email.value,
            senha: form.senha.value
        };

        try {
            const response = await fetch(`${API_USUARIO}/login`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(data),
                credentials: "include"
            });

            const json = await safeParseJSON(response);

            if (response.ok) {
                msg.textContent = "✔ Login realizado!";
                setTimeout(() => {
                    window.location.href = "../tela_principal/tela_principal.html";
                }, 500);
            } else {
                msg.textContent = json.erro || "Usuário ou senha inválidos";
            }
        } catch (err) {
            msg.textContent = "Erro ao logar: " + err.message;
        }
    });
}

/* =========================================================
    RECUPERAÇÃO – ENVIAR CÓDIGO
========================================================= */
function initFormRecuperar() {
    const form = document.getElementById('formRecuperar');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const email = form.email.value;

        try {
            const response = await fetch(`${API_USUARIO}/gerarCodigo`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ email }),
                credentials: "include"
            });

            const json = await safeParseJSON(response);

            if (response.ok) {
                localStorage.setItem("emailRecuperacao", email);
                alert(json.mensagem || "Código enviado!");
                window.location.href = "redefinir.html";
            } else {
                alert(json.erro || "Erro ao enviar código");
            }
        } catch (err) {
            alert("Erro: " + err.message);
        }
    });
}

/* =========================================================
   REDEFINIR SENHA (CORRIGIDO!)
========================================================= */
function initFormRedefinir() {
    const form = document.getElementById('formRedefinir');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const email = localStorage.getItem("emailRecuperacao");
        if (!email) return alert("Solicite o código novamente.");

        const codigo = form.codigo.value.trim();
        const senha = form.senha.value.trim(); // <-- variável correta

        if (!codigo || !senha) {
            return alert("Todos os campos são obrigatórios.");
        }

        console.log({ email, codigo, senha });

        try {
            const response = await fetch(`${API_USUARIO}/redefinirSenha`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    email: email,
                    codigo: codigo,
                    senha: senha // <-- enviado corretamente!!!
                }),
                credentials: "include"
            });

            const json = await safeParseJSON(response);

            if (response.ok) {
                alert(json.mensagem || "Senha redefinida com sucesso!");
                localStorage.removeItem("emailRecuperacao");
                window.location.href = "login.html";
            } else {
                alert(json.erro || "Erro ao redefinir senha");
            }
        } catch (err) {
            alert("Erro: " + err.message);
        }
    });
}

/* =========================================================
    VALIDAÇÃO
========================================================= */
function initFormValidacao() {
    const form = document.getElementById('formValidar');
    if (!form) return;

    const msg = document.getElementById('mensagem');

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const codigo = form.codigo.value;

        try {
            const response = await fetch(`${API_USUARIO}/validar`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ codigo }),
                credentials: "include"
            });

            const json = await safeParseJSON(response);
            msg.textContent = json.mensagem || json.erro || "Resultado recebido";
        } catch (err) {
            msg.textContent = "Erro: " + err.message;
        }
    });
}

/* =========================================================
    JSON SEGURO
========================================================= */
async function safeParseJSON(response) {
    const text = await response.text();
    try { return JSON.parse(text); }
    catch { return { mensagem: text }; }
}
