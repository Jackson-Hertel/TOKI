// Pega os dados do usuário salvos no login
const usuarioLogado = JSON.parse(localStorage.getItem("usuarioLogado"));

if (usuarioLogado) {
  document.getElementById("nomeUsuario").textContent = `Olá, ${usuarioLogado.nome}! 👋`;
} else {
  // Se não tiver usuário logado, volta pro login
  alert("Você precisa estar logado para acessar essa página.");
  window.location.href = "../login.cadastro/login.html";
}
