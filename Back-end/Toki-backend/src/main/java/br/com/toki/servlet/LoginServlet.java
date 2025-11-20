package br.com.toki.servlet;

import br.com.toki.model.Usuario;
import br.com.toki.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/usuario/login")
public class LoginServlet extends HttpServlet {

    private final UsuarioService usuarioService = new UsuarioService(); // usar Service

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String senha = request.getParameter("senha");

        // chama o método correto do UsuarioService
        Usuario usuario = usuarioService.buscarUsuarioPorEmailESenha(email, senha);

        if (usuario != null) {
            request.getSession().setAttribute("usuarioLogado", usuario);
            response.setStatus(200);
            response.getWriter().write("✔ Login realizado!");
        } else {
            response.setStatus(401);
            response.getWriter().write("E-mail ou senha incorretos ou conta não ativada.");
        }
    }
}
