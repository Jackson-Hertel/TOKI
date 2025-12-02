package br.com.toki.servlet;

import br.com.toki.model.Usuario;
import br.com.toki.service.UsuarioService;
import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/toki/usuario/login")
public class LoginServlet extends HttpServlet {

    private final UsuarioService usuarioService = new UsuarioService();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        // Configurações CORS
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:5500");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setContentType("application/json;charset=UTF-8");

        // Lendo JSON do corpo da requisição
        Usuario usuarioReq = gson.fromJson(req.getReader(), Usuario.class);
        String email = usuarioReq.getEmail();
        String senha = usuarioReq.getSenha();

        // Validação básica
        if (email == null || senha == null || email.isBlank() || senha.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"erro\":\"Preencha e-mail e senha\"}");
            return;
        }

        // Tentativa de login
        Usuario usuario = usuarioService.login(email, senha);

        if (usuario != null) {
            // Cria sessão e salva o usuário logado
            HttpSession session = req.getSession(true);
            session.setAttribute("usuarioLogado", usuario);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(usuario));
        } else {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"erro\":\"Credenciais inválidas\"}");
        }
    }

    // Pré-voo do CORS
    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:5500");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
