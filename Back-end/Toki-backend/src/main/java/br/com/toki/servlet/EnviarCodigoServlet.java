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

@WebServlet("/enviarCodigo")
public class EnviarCodigoServlet extends HttpServlet {

    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private EmailService emailService = new EmailService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Recebe o e-mail do formulário
        String email = request.getParameter("email");

        // Validação simples de Gmail
        if (email == null || !email.endsWith("@gmail.com")) {
            response.getWriter().write("E-mail inválido. Use um Gmail.");
            return;
        }

        // Gerar código de 6 dígitos
        String codigo = CodigoService.gerarCodigo(6);

        // Salvar código + expiração no banco
        usuarioDAO.salvarCodigo(email, codigo);

        // Enviar código para o e-mail
        try {
            emailService.enviarCodigo(email, codigo);
            response.getWriter().write("Código enviado para " + email);
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("Erro ao enviar código. Verifique configuração do e-mail.");
        }
    }
}
