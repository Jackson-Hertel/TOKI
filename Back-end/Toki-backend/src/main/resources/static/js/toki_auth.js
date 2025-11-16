/* =======================
   MOSTRAR / OCULTAR SENHA
========================== */

document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll(".toggle-password").forEach(icon => {
        icon.addEventListener("click", () => {

            const target = icon.getAttribute("data-target");
            const input = document.getElementById(target);

            if (!input) return;

            const escondido = input.type === "password";
            input.type = escondido ? "text" : "password";

            // troca imagem
            icon.src = escondido
                ? "../img/eye-open.png"
                : "../img/eye-closed.png";
        });
    });
});



/* =======================
        LOGIN
========================== */
const formLogin = document.getElementById("formLogin");

if (formLogin) {
    formLogin.addEventListener("submit", async (e) => {
        e.preventDefault();

        const email = document.getElementById("email").value;
        const senha = document.getElementById("senha").value;

        try {
            const r = await fetch("http://localhost:8080/api/usuarios/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ email, senha })
            });

            if (r.ok) {
                const usuario = await r.json();
                localStorage.setItem("usuarioLogado", JSON.stringify(usuario));
                window.location.href = "../tela_principal/tela_principal.html";
            } else {
                alert("❌ Email ou senha incorretos!");
            }
        } catch {
            alert("🚫 Erro ao conectar ao servidor.");
        }
    });
}


/* =======================
        CADASTRO
========================== */
const formCadastro = document.getElementById("formCadastro");

if (formCadastro) {
    formCadastro.addEventListener("submit", async (e) => {
        e.preventDefault();

        const nome = document.getElementById("nome").value;
        const email = document.getElementById("email").value;
        const senha = document.getElementById("senha").value;

        try {
            const r = await fetch("http://localhost:8080/api/usuarios/cadastrar", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ nome, email, senha })
            });

            if (r.ok) {
                alert("✔ Usuário cadastrado!");
                window.location.href = "login.html";
            } else {
                alert("Erro ao cadastrar.");
            }
        } catch {
            alert("🚫 Erro ao conectar ao servidor.");
        }
    });
}


/* =======================
        RECUPERAÇÃO
========================== */
const formRecuperar = document.getElementById("formRecuperar");

if (formRecuperar) {
    formRecuperar.addEventListener("submit", async (e) => {
        e.preventDefault();

        const email = document.getElementById("emailRecuperar").value;

        try {
            const r = await fetch("/toki/recuperar", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ email })
            });

            if (r.ok) {
                alert("✔ Link enviado ao seu e-mail!");
            } else {
                alert("E-mail não encontrado.");
            }
        } catch {
            alert("🚫 Erro ao conectar ao servidor.");
        }
    });
}