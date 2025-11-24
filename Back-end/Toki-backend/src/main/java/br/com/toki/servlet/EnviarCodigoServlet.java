package br.com.toki.servlet;

import br.com.toki.dao.UsuarioDAO;
import br.com.toki.service.CodigoService;
import br.com.toki.service.EmailService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/usuario/enviarCodigo")
public class EnviarCodigoServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final EmailService emailService = new EmailService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        response.setContentType("application/json");

        if (email == null || !email.endsWith("@gmail.com")) {
            response.setStatus(400);
            response.getWriter().write("{\"erro\": \"E-mail inválido. Use um Gmail.\"}");
            return;
        }

        String codigo = CodigoService.gerarCodigo(6);
        usuarioDAO.salvarCodigo(email, codigo);

        try {
            emailService.enviarCodigo(email, codigo);
            response.setStatus(200);
            response.getWriter().write("{\"sucesso\": \"Código enviado para " + email + "\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
            response.getWriter().write("{\"erro\": \"Erro ao enviar código. Verifique configuração do e-mail.\"}");
        }
    }
}

