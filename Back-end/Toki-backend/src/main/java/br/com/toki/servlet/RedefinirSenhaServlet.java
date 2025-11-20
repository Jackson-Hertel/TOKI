package br.com.toki.servlet;

import br.com.toki.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/usuario/redefinirSenha")
public class RedefinirSenhaServlet extends HttpServlet {

    private final UsuarioService usuarioService = new UsuarioService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String codigo = req.getParameter("codigo");
        String novaSenha = req.getParameter("senha");

        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        if (email == null || codigo == null || novaSenha == null ||
                email.isEmpty() || codigo.isEmpty() || novaSenha.isEmpty()) {
            out.print("{\"erro\": \"Todos os campos são obrigatórios.\"}");
            return;
        }

        boolean sucesso = usuarioService.redefinirSenha(email, codigo, novaSenha);

        if (sucesso) {
            out.print("{\"sucesso\": true}");
        } else {
            out.print("{\"erro\": \"Código inválido ou expirado.\"}");
        }
    }
}
