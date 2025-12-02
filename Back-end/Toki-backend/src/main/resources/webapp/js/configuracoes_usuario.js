(() => {
  const API = (window.API_BASE || "").replace(/\/$/, "");
  let usuarioLogado = null;

  document.addEventListener("DOMContentLoaded", async () => {
    await carregarUsuarioLogado();
    initTabs();
    initPerfil();
    initConta();
    initNotificacoes();
    initAparencia();
    initLogout();
    initCancelar();
  });

  /* ==============================
     TOAST
  ============================== */
  function toast(msg, opts = {}) {
    const n = document.createElement("div");
    n.className = "toki-toast";
    n.innerText = msg;
    document.body.appendChild(n);
    setTimeout(() => n.remove(), opts.duration || 3000);
  }

  /* ==============================
     CARREGAR USUÁRIO LOGADO
  ============================== */
  async function carregarUsuarioLogado() {
    try {
      const res = await fetch(`${API}/usuario/logado`, { credentials: "include" });
      if (!res.ok) throw new Error("Nenhum usuário logado");
      usuarioLogado = await res.json();
      atualizarInterfaceUsuario();
    } catch (err) {
      console.error("Erro ao carregar usuário:", err);
      toast("Erro ao carregar usuário logado");
      window.location.href = "../login_cadastro/login.html";
    }
  }

  /* ==============================
     ATUALIZAR INTERFACE
  ============================== */
  function atualizarInterfaceUsuario() {
    if (!usuarioLogado) return;

    // Fotos
    document.querySelectorAll(".profile-img").forEach(el => {
      if (!el) return;
      el.src = usuarioLogado.fotoPerfil
        ? (usuarioLogado.fotoPerfil.startsWith("data:")
            ? usuarioLogado.fotoPerfil
            : "data:image/png;base64," + usuarioLogado.fotoPerfil)
        : `https://ui-avatars.com/api/?name=${encodeURIComponent(usuarioLogado.nome || "Usuário TOKI")}&background=137fec&color=fff&size=128`;
    });

    // Textos sidebar
    const setText = (id, t) => {
      const el = document.getElementById(id);
      if (el) el.innerText = t || "";
    };
    setText("nomeSidebar", usuarioLogado.nome);
    setText("emailSidebar", usuarioLogado.email);

    // Inputs
    const setInput = (id, v) => {
      const el = document.getElementById(id);
      if (el) el.value = v || "";
    };
    setInput("nome", usuarioLogado.nome);
    setInput("email", usuarioLogado.email);
    setInput("telefone", usuarioLogado.telefone);
    setInput("metodoLembrete", usuarioLogado.metodoLembrete);
    setInput("antecedencia", usuarioLogado.antecedencia);

    // Toggle de notificações
    const notifToggle = document.getElementById("notifToggle");
    if (notifToggle) notifToggle.checked = !!usuarioLogado.receberLembretes;

    // Tema e cor
    const tema = usuarioLogado.tema || localStorage.getItem("toki-tema") || "claro";
    const cor = usuarioLogado.corPrincipal || localStorage.getItem("toki-cor") || "#137fec";

    if (tema === "auto") {
      const sysDark = window.matchMedia("(prefers-color-scheme: dark)").matches;
      document.documentElement.setAttribute("data-theme", sysDark ? "dark" : "light");
    } else {
      document.documentElement.setAttribute("data-theme", tema === "escuro" ? "dark" : "light");
    }

    document.documentElement.style.setProperty("--toki-primary", cor);

    // Seleção de cores
    document.querySelectorAll(".color-dot").forEach(dot => {
      dot.classList.toggle("selected",
        dot.getAttribute("data-color").toLowerCase() === cor.toLowerCase()
      );
    });

    // Seleção do tema
    document.querySelectorAll("input[name='tema']").forEach(r => {
      r.checked = r.value === tema;
    });
  }

  /* ==============================
     PERFIL
  ============================== */
  function initPerfil() {
    const inputFoto = document.getElementById("inputFoto");
    const imgs = document.querySelectorAll(".profile-img");

    const setLoading = (v) => {
      imgs.forEach(img => {
        img.classList.toggle("opacity-50", v);
        img.classList.toggle("animate-pulse", v);
      });
    };

    imgs.forEach(img => img?.addEventListener("click", () => inputFoto.click()));

    inputFoto?.addEventListener("change", async () => {
      const file = inputFoto.files[0];
      if (!file) return;

      const preview = URL.createObjectURL(file);
      imgs.forEach(el => el.src = preview);

      setLoading(true);
      try {
        const form = new FormData();
        form.append("foto", file);

        const res = await fetch(`${API}/usuario/uploadFoto`, {
          method: "POST",
          body: form,
          credentials: "include"
        });

        if (!res.ok) throw new Error("Upload falhou");

        await carregarUsuarioLogado();
        toast("Foto atualizada com sucesso!");
      } catch {
        toast("Erro ao atualizar foto");
      } finally {
        setLoading(false);
      }
    });
  }

  /* ==============================
     CONTA
  ============================== */
  function initConta() {
    const btn = document.getElementById("salvarConta");

    btn?.addEventListener("click", async () => {
      if (!usuarioLogado) return toast("Usuário não logado");

      const data = {
        nome: document.getElementById("nome").value,
        email: document.getElementById("email").value,
        telefone: document.getElementById("telefone").value
      };

      try {
        const res = await fetch(`${API}/usuario/atualizarConta`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(data),
          credentials: "include"
        });

        if (!res.ok) throw new Error();

        toast("Conta atualizada");
        await carregarUsuarioLogado();
      } catch {
        toast("Erro ao atualizar conta");
      }
    });
  }

  /* ==============================
     NOTIFICAÇÕES
  ============================== */
  function initNotificacoes() {
    const btn = document.getElementById("salvarNot");

    btn?.addEventListener("click", async () => {
      if (!usuarioLogado) return toast("Usuário não logado");

      const data = {
        receberLembretes: document.getElementById("notifToggle").checked,
        metodoLembrete: document.getElementById("metodoLembrete").value,
        antecedencia: document.getElementById("antecedencia").value
      };

      try {
        const res = await fetch(`${API}/usuario/atualizarNotificacoes`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(data),
          credentials: "include"
        });

        if (!res.ok) throw new Error();

        toast("Notificações salvas");
        await carregarUsuarioLogado();
      } catch {
        toast("Erro ao salvar notificações");
      }
    });
  }

  /* ==============================
     APARÊNCIA
  ============================== */
  function initAparencia() {
    const btn = document.getElementById("salvarApar");
    const dots = document.querySelectorAll(".color-dot");

    // Clicar nas cores
    dots.forEach(dot => {
      dot.addEventListener("click", () => {
        const c = dot.dataset.color;
        document.documentElement.style.setProperty("--toki-primary", c);
        dots.forEach(d => d.classList.remove("selected"));
        dot.classList.add("selected");
      });
    });

    // Salvar
    btn?.addEventListener("click", async () => {
      if (!usuarioLogado) return toast("Usuário não logado");

      const tema = document.querySelector("input[name='tema']:checked")?.value || "claro";
      const cor = getComputedStyle(document.documentElement).getPropertyValue("--toki-primary").trim();

      const data = { tema, corPrincipal: cor };

      try {
        const res = await fetch(`${API}/usuario/atualizarAparencia`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(data),
          credentials: "include"
        });

        if (!res.ok) throw new Error();

        document.documentElement.setAttribute("data-theme", tema === "escuro" ? "dark" : "light");
        localStorage.setItem("toki-tema", tema);
        localStorage.setItem("toki-cor", cor);

        window.dispatchEvent(new CustomEvent("tokiTemaAtualizado", { detail: { tema, cor } }));

        toast("Aparência salva");
        await carregarUsuarioLogado();
      } catch {
        toast("Erro ao salvar aparência");
      }
    });
  }

  /* ==============================
     LOGOUT
  ============================== */
  function initLogout() {
    const btn = document.getElementById("logoutBtn");

    btn?.addEventListener("click", () => {
      const modal = document.createElement("div");
      modal.className = "confirm-modal show";
      modal.innerHTML = `
        <div class="confirm-box">
          <h3>Deseja realmente sair?</h3>
          <div class="confirm-buttons mt-3">
            <button id="logoutCancel" class="confirm-no">Cancelar</button>
            <button id="logoutTelaPrincipal" class="btn-sec">Voltar à Tela Principal</button>
            <button id="logoutYes" class="confirm-yes">Sair</button>
          </div>
        </div>`;
      document.body.appendChild(modal);

      document.getElementById("logoutCancel").onclick = () => modal.remove();

      document.getElementById("logoutTelaPrincipal").onclick = () =>
        window.location.href = "../tela_principal/tela_principal.html";

      document.getElementById("logoutYes").onclick = async () => {
        try {
          const res = await fetch(`${API}/usuario/logout`, {
            method: "DELETE",
            credentials: "include"
          });
          if (res.ok) window.location.href = "../login_cadastro/login.html";
          else toast("Erro ao sair");
        } catch {
          toast("Erro ao sair");
        }
      };
    });
  }

  /* ==============================
     TABS
  ============================== */
  function initTabs() {
    const tabs = document.querySelectorAll(".tab-btn");
    const panels = document.querySelectorAll(".tab-panel");

    tabs.forEach(tab => {
      tab.addEventListener("click", () => {
        tabs.forEach(t => t.classList.remove("tab-active"));
        tab.classList.add("tab-active");

        const target = tab.dataset.tab;
        panels.forEach(p => p.classList.toggle("hidden", p.id !== target));
      });
    });
  }

  /* ==============================
     CANCELAR (RESETAR)
  ============================== */
  function initCancelar() {
    ["cancelarConta", "cancelarNot", "cancelarApar", "cancelarPriv"]
      .forEach(id => {
        const btn = document.getElementById(id);
        btn?.addEventListener("click", () => atualizarInterfaceUsuario());
      });
  }

  /* ==============================
     APLICAR TEMA/COR DO LOCALSTORAGE
  ============================== */
  const temaLS = localStorage.getItem("toki-tema");
  const corLS = localStorage.getItem("toki-cor");
  if (temaLS) document.documentElement.setAttribute("data-theme", temaLS === "escuro" ? "dark" : "light");
  if (corLS) document.documentElement.style.setProperty("--toki-primary", corLS);
})();
