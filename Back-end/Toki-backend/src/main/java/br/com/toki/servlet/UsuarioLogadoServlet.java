package br.com.toki.servlet;

import br.com.toki.model.Usuario;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import com.google.gson.Gson;

@WebServlet("/toki/usuario/logado")
public class UsuarioLogadoServlet extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json;charset=UTF-8");

        HttpSession session = request.getSession(false);
        if (session != null) {
            Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
            if (usuario != null) {
                response.getWriter().write(gson.toJson(usuario));
                return;
            }
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"erro\":\"Nenhum usuário logado\"}");
    }
}
