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
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Permite CORS para requisições OPTIONS (pré-flight)
        setCors(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setCors(resp);
        resp.setContentType("application/json; charset=UTF-8");
        PrintWriter out = resp.getWriter();

        // Captura parâmetros do front-end
        String email = req.getParameter("email");
        String codigo = req.getParameter("codigo");
        String novaSenha = req.getParameter("senha");

        // Validação básica
        if (email == null || codigo == null || novaSenha == null ||
                email.isEmpty() || codigo.isEmpty() || novaSenha.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"erro\": \"Todos os campos são obrigatórios.\"}");
            return;
        }

        // Chama o service para redefinir a senha
        boolean sucesso = usuarioService.redefinirSenha(email, codigo, novaSenha);

        if (sucesso) {
            // Remove email da sessão, se existir
            HttpSession session = req.getSession(false);
            if (session != null) {
                session.removeAttribute("usuarioEmail");
            }

            resp.setStatus(HttpServletResponse.SC_OK);
            out.print("{\"sucesso\": true, \"mensagem\": \"Senha redefinida com sucesso.\"}");
        } else {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"erro\": \"Código inválido ou expirado.\"}");
        }
    }

    // Método auxiliar para permitir CORS
    private void setCors(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*"); // Ajuste conforme seu front-end
        resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }
}
