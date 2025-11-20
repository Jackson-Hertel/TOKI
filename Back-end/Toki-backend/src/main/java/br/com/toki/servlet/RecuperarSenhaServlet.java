package br.com.toki.servlet;

import br.com.toki.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/usuario/recuperar-senha")
public class RecuperarSenhaServlet extends HttpServlet {

    private final UsuarioService usuarioService = new UsuarioService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");

        try {
            // chama o método correto do service
            usuarioService.gerarCodigoRecuperacao(email);

            response.setStatus(200);
            response.getWriter().write("✔ Código de recuperação enviado!");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
            response.getWriter().write("❌ Erro ao enviar código de recuperação.");
        }
    }
}
