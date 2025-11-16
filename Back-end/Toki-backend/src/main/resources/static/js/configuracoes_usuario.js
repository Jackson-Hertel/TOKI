// =========================
//   FOTO DE PERFIL
// =========================
const fotoInput = document.getElementById("fotoInput");
const fotoPreview = document.getElementById("fotoPreview");

// Clicar na foto → abrir seletor
fotoPreview.addEventListener("click", () => {
    fotoInput.click();
});

// Atualizar imagem ao escolher arquivo
fotoInput.addEventListener("change", () => {
    const file = fotoInput.files[0];
    if (file) {
        const url = URL.createObjectURL(file);
        fotoPreview.src = url;
        localStorage.setItem("toki_foto_perfil", url);
    }
});

// Carregar foto salva
function carregarFoto() {
    const fotoSalva = localStorage.getItem("toki_foto_perfil");
    if (fotoSalva) fotoPreview.src = fotoSalva;
}

// =========================
//   PERFIL DO USUÁRIO
// =========================
function carregarPerfil() {
    document.getElementById("nome").value =
        localStorage.getItem("toki_nome") || "Usuário Toki";

    document.getElementById("email").value =
        localStorage.getItem("toki_email") || "email@exemplo.com";

    document.getElementById("senhaAtual").value = "";
    document.getElementById("novaSenha").value = "";
    document.getElementById("confirmarSenha").value = "";
}

document.querySelector("#perfil .btn-primary").addEventListener("click", () => {
    const nome = document.getElementById("nome").value;
    const email = document.getElementById("email").value;
    const senhaAtual = document.getElementById("senhaAtual").value;
    const novaSenha = document.getElementById("novaSenha").value;
    const confirmarSenha = document.getElementById("confirmarSenha").value;

    if (novaSenha !== confirmarSenha) {
        alert("❌ As senhas não coincidem!");
        return;
    }

    // salvar no localStorage
    localStorage.setItem("toki_nome", nome);
    localStorage.setItem("toki_email", email);

    if (novaSenha.trim() !== "") {
        localStorage.setItem("toki_senha", novaSenha);
    }

    alert("✔ Perfil salvo!");
});

// =========================
//   TEMA DO SITE
// =========================
const radiosTema = document.querySelectorAll("input[name='tema']");
const selectCor = document.getElementById("corPrincipal");

function carregarTema() {
    const tema = localStorage.getItem("toki_tema");
    const cor = localStorage.getItem("toki_cor");

    if (tema) {
        document.documentElement.setAttribute("data-tema", tema);
        radiosTema.forEach(r => r.checked = r.value === tema);
    }

    if (cor) {
        selectCor.value = cor;
        document.documentElement.style.setProperty("--primary", cor);
    }
}

document.querySelector("#tema .btn-primary").addEventListener("click", () => {
    const temaSelecionado = document.querySelector("input[name='tema']:checked").value;
    const corSelecionada = selectCor.value;

    localStorage.setItem("toki_tema", temaSelecionado);
    localStorage.setItem("toki_cor", corSelecionada);

    document.documentElement.setAttribute("data-tema", temaSelecionado);
    document.documentElement.style.setProperty("--primary", corSelecionada);

    alert("✔ Tema salvo!");
});

// =========================
//   PREFERÊNCIAS DO CALENDÁRIO
// =========================
function carregarPreferenciasCalendario() {
    document.getElementById("inicioSemana").value =
        localStorage.getItem("toki_inicio_semana") || "domingo";

    document.getElementById("feriados").checked =
        localStorage.getItem("toki_feriados") === "true";

    document.getElementById("aniversarios").checked =
        localStorage.getItem("toki_aniversarios") === "true";

    document.getElementById("concluidos").checked =
        localStorage.getItem("toki_concluidos") === "true";
}

document.querySelector("#calendario .btn-primary").addEventListener("click", () => {
    localStorage.setItem("toki_inicio_semana", inicioSemana.value);
    localStorage.setItem("toki_feriados", feriados.checked);
    localStorage.setItem("toki_aniversarios", aniversarios.checked);
    localStorage.setItem("toki_concluidos", concluidos.checked);

    alert("✔ Preferências salvas!");
});

// =========================
//   CARREGAR TUDO AO ABRIR
// =========================
window.addEventListener("load", () => {
    carregarFoto();
    carregarPerfil();
    carregarTema();
    carregarPreferenciasCalendario();
});
