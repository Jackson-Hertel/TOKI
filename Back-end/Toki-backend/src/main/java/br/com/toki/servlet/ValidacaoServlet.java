package br.com.toki.servlet;

import br.com.toki.model.Usuario;
import br.com.toki.service.UsuarioService; // <- IMPORTAÇÃO CORRETA
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/usuario/validar")
public class ValidacaoServlet extends HttpServlet {

    private final UsuarioService service = new UsuarioService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String codigoDigitado = request.getParameter("codigo");
        String codigoGerado = (String) request.getSession().getAttribute("codigoVerificacao");
        String email = (String) request.getSession().getAttribute("usuarioEmail");

        if (codigoGerado == null || email == null) {
            response.setStatus(400);
            response.getWriter().write("Sessão expirada ou inválida.");
            return;
        }

        if (codigoDigitado.equals(codigoGerado)) {
            // ✅ Código válido, ativar usuário
            service.ativarUsuario(email); // <- método existe na sua classe UsuarioService

            // Limpar sessão
            request.getSession().removeAttribute("codigoVerificacao");
            request.getSession().removeAttribute("usuarioEmail");

            response.setStatus(200);
            response.getWriter().write("✔ Conta validada com sucesso!");
        } else {
            response.setStatus(401);
            response.getWriter().write("Código incorreto.");
        }
    }
}
