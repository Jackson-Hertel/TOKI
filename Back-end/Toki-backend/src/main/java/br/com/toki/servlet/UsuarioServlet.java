package br.com.toki.servlet;

import br.com.toki.model.Usuario;
import br.com.toki.service.UsuarioService;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/api/usuarios/*")
public class UsuarioServlet extends HttpServlet {
    private final UsuarioService service = new UsuarioService();
    private final Gson gson = new Gson();

    private void configurarCORS(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        configurarCORS(resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        configurarCORS(resp);
        List<Usuario> usuarios = service.listarUsuarios();
        String json = gson.toJson(usuarios);
        resp.setContentType("application/json");
        resp.getWriter().write(json);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        configurarCORS(resp);

        String path = req.getPathInfo();
        Usuario usuario = gson.fromJson(req.getReader(), Usuario.class);

        if (path == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Endpoint inválido");
            return;
        }

        switch (path) {
            case "/cadastrar":
                service.adicionarUsuario(usuario);
                resp.setStatus(HttpServletResponse.SC_CREATED);
                break;

            case "/login":
                Usuario existente = service.buscarUsuarioPorEmailESenha(usuario.getEmail(), usuario.getSenha());
                if (existente != null) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write(gson.toJson(existente));
                } else {
                    resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Credenciais inválidas");
                }
                break;

            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Caminho não encontrado: " + path);
        }
    }
}

