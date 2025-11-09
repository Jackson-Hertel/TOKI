document.getElementById("formLogin").addEventListener("submit", async (e) => {
  e.preventDefault();

  const email = document.getElementById("email").value;
  const senha = document.getElementById("senha").value;

  try {
    const resposta = await fetch("http://localhost:8080/api/usuarios/login", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ email, senha }),
    });

    if (resposta.ok) {
      const usuario = await resposta.json();

      // 💾 Salvar usuário logado no navegador
      localStorage.setItem("usuarioLogado", JSON.stringify(usuario));

      alert("✅ Login realizado com sucesso!");
      window.location.href = "../tela_principal/tela_principal.html";
    } else {
      alert("❌ Email ou senha incorretos!");
    }
  } catch (erro) {
    console.error("Erro:", erro);
    alert("❌ Falha na conexão com o servidor.");
  }
});
