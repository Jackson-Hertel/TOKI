// =============================
// TOKI - Configurações do Usuário
// =============================

// === Inicializa tema salvo ===
(function applySavedTheme() {
  const savedTheme = localStorage.getItem("toki-theme") || "system";
  applyTheme(savedTheme);
  marcarTemaAtivo(savedTheme);
})();

// === Aplica o tema ===
function applyTheme(theme) {
  let finalTheme = theme;

  if (theme === "system") {
    const prefersDark = window.matchMedia("(prefers-color-scheme: dark)").matches;
    finalTheme = prefersDark ? "dark" : "light";
  }

  document.documentElement.setAttribute("data-theme", finalTheme);
  localStorage.setItem("toki-theme", theme);
}

// === Marca o tema ativo visualmente ===
function marcarTemaAtivo(theme) {
  document.querySelectorAll(".theme-card").forEach(card => {
    card.classList.toggle("active", card.dataset.theme === theme);
  });
}

// === Clique nas opções de tema ===
document.querySelectorAll(".theme-card").forEach(card => {
  card.addEventListener("click", () => {
    const selectedTheme = card.dataset.theme;
    applyTheme(selectedTheme);
    marcarTemaAtivo(selectedTheme);
  });
});

// === Reage a mudanças do sistema em tempo real ===
window.matchMedia("(prefers-color-scheme: dark)").addEventListener("change", (e) => {
  const currentSetting = localStorage.getItem("toki-theme") || "system";
  if (currentSetting === "system") {
    const newTheme = e.matches ? "dark" : "light";
    document.documentElement.setAttribute("data-theme", newTheme);
  }
});

// === Outras configurações ===
const notifToggle = document.getElementById("notifToggle");
if (notifToggle) {
  notifToggle.addEventListener("change", () => {
    alert(notifToggle.checked ? "🔔 Lembretes ativados!" : "🔕 Lembretes desativados.");
  });
}

const languageSelect = document.getElementById("languageSelect");
if (languageSelect) {
  const idiomaSalvo = localStorage.getItem("idioma");
  if (idiomaSalvo) languageSelect.value = idiomaSalvo;

  languageSelect.addEventListener("change", () => {
    const idioma = languageSelect.value;
    localStorage.setItem("idioma", idioma);
    alert("🌐 Idioma alterado para: " + idioma.toUpperCase());
  });
}

const voltarBtn = document.getElementById("voltarBtn");
if (voltarBtn) {
  voltarBtn.addEventListener("click", () => {
    window.location.href = "./tela_principal.html";
  });
}

