package br.com.toki.servlet;

import br.com.toki.service.UsuarioService;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@WebServlet("/toki/usuario/redefinirSenha")
public class RedefinirSenhaServlet extends HttpServlet {

    private final UsuarioService usuarioService = new UsuarioService();
    private final Gson gson = new Gson();

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setCors(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setCors(resp);
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        // ===========================
        // LER JSON
        // ===========================
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
        }

        Map<String, Object> dados;
        try {
            dados = gson.fromJson(sb.toString(), Map.class);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"erro\":\"JSON inválido\"}");
            return;
        }

        String email = dados.get("email") != null ? dados.get("email").toString().trim() : "";
        String codigo = dados.get("codigo") != null ? dados.get("codigo").toString().trim() : "";
        String senha = dados.get("senha") != null ? dados.get("senha").toString().trim() : "";
        System.out.println("Email: " + email + ", Codigo: " + codigo + ", Senha: " + senha);

        if (email.isBlank() || codigo.isBlank() || senha.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"erro\":\"Todos os campos são obrigatórios\"}");
            return;
        }


        // ===========================
        // Redefinir senha
        // ===========================
        boolean sucesso = usuarioService.redefinirSenha(email, codigo, senha);

        if (sucesso) {
            // Limpa sessão se houver
            HttpSession session = req.getSession(false);
            if (session != null) session.invalidate();

            resp.setStatus(HttpServletResponse.SC_OK);
            out.print("{\"sucesso\":true,\"mensagem\":\"Senha redefinida com sucesso!\"}");
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"erro\":\"Código inválido ou expirado\"}");
        }
    }

    private void setCors(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }

}
