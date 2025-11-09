document.getElementById("formCadastro").addEventListener("submit", async (e) => {
  e.preventDefault();

  const nome = document.getElementById("nome").value;
  const email = document.getElementById("email").value;
  const senha = document.getElementById("senha").value;

  try {
    const resposta = await fetch("http://localhost:8080/api/usuarios/cadastrar", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ nome, email, senha }),
    });

    if (resposta.ok) {
      alert("✅ Usuário cadastrado com sucesso!");
      window.location.href = "login.html";
    } else {
      const erro = await resposta.text();
      alert("Erro ao cadastrar: " + erro);
    }
  } catch (erro) {
    console.error("Erro:", erro);
    alert("❌ Falha na conexão com o servidor.");
  }
});
