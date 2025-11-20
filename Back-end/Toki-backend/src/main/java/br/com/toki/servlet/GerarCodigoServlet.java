package br.com.toki.servlet;

import br.com.toki.service.UsuarioService;
import jakarta.mail.MessagingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/usuario/gerar-codigo")
public class GerarCodigoServlet extends HttpServlet {

    private final UsuarioService service = new UsuarioService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");

        try {
            service.gerarCodigoRecuperacao(email); // <- método correto
            request.getSession().setAttribute("usuarioEmail", email);
            response.setStatus(200);
            response.getWriter().write("✔ Código de verificação enviado!");
        } catch (MessagingException e) {
            e.printStackTrace();
            response.setStatus(500);
            response.getWriter().write("Erro ao enviar código.");
        }
    }
}
