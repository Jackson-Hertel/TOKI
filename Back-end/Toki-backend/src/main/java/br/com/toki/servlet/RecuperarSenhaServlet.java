package br.com.toki.servlet;

import br.com.toki.service.UsuarioService;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/usuario/recuperar-senha")
public class RecuperarSenhaServlet extends HttpServlet {

    private final UsuarioService usuarioService = new UsuarioService();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json; charset=UTF-8");

        // Lê JSON do corpo
        BufferedReader reader = request.getReader();
        EmailRequest reqBody = gson.fromJson(reader, EmailRequest.class);

        String email = reqBody.getEmail();

        if (email == null || email.isEmpty()) {
            response.setStatus(400);
            response.getWriter().write("{\"erro\":\"E-mail é obrigatório\"}");
            return;
        }

        try {
            usuarioService.gerarCodigoRecuperacao(email);
            response.setStatus(200);
            response.getWriter().write("{\"mensagem\":\"✔ Código de recuperação enviado!\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
            response.getWriter().write("{\"erro\":\"❌ Erro ao enviar código de recuperação.\"}");
        }
    }

    // Classe auxiliar para mapear JSON
    private static class EmailRequest {
        private String email;
        public String getEmail() { return email; }
    }
}
