package br.com.toki.servlet;

import br.com.toki.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/validar-codigo")
public class ValidarCodigoServlet extends HttpServlet {

    private final UsuarioService service = new UsuarioService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String codigoDigitado = request.getParameter("codigo");
        String email = (String) request.getSession().getAttribute("usuarioEmail");

        if (codigoDigitado == null || email == null) {
            response.setStatus(400);
            response.getWriter().write("Sessão expirada ou inválida.");
            return;
        }

        boolean valido = service.redefinirSenha(email, codigoDigitado, null); // só valida
        if (valido) {
            service.ativarUsuario(email); // ativa usuário

            request.getSession().removeAttribute("usuarioEmail");

            response.setStatus(200);
            response.getWriter().write("✅ Código válido! Você pode redefinir a senha.");
        } else {
            response.setStatus(401);
            response.getWriter().write("❌ Código inválido ou expirado!");
        }
    }
}

