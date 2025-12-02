package br.com.toki.servlet;

import br.com.toki.model.Usuario;
import br.com.toki.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/usuario/cadastrar")
public class CadastroServlet extends HttpServlet {

    private final UsuarioService usuarioService = new UsuarioService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nome = request.getParameter("nome");
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");

        // validar campos
        if (nome == null || email == null || senha == null ||
                nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            response.setStatus(400);
            response.getWriter().write("{\"erro\":\"Preencha todos os campos.\"}");
            return;
        }

        // verificar se usuário já existe
        if (usuarioService.buscarUsuarioPorEmail(email) != null) {
            response.setStatus(409);
            response.getWriter().write("{\"erro\":\"E-mail já cadastrado.\"}");
            return;
        }

        // criar usuário
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(senha);
        usuarioService.adicionarUsuario(usuario); // chama service, não DAO direto

        // resposta direta, sem código de verificação
        response.setStatus(201);
        response.getWriter().write("{\"mensagem\":\"✔ Usuário cadastrado com sucesso!\"}");
    }
}
