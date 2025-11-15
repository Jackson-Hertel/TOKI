package br.com.toki.servlet;

import br.com.toki.model.Usuario;
import br.com.toki.service.UsuarioService;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/api/usuarios/*")
public class UsuarioServlet extends HttpServlet {
    private final UsuarioService service = new UsuarioService();
    private final Gson gson = new Gson();

    private void configurarCORS(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        configurarCORS(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        configurarCORS(resp);
        String path = req.getPathInfo();

        if (path == null || path.equals("/")) {
            // Retorna todos os usuários (admin ou debug)
            List<Usuario> usuarios = service.listarUsuarios();
            resp.setContentType("application/json");
            resp.getWriter().write(gson.toJson(usuarios));
            return;
        }

        if (path.equals("/logado")) {
            // Retorna o usuário da sessão atual
            HttpSession session = req.getSession(false);
            resp.setContentType("application/json");
            if (session != null && session.getAttribute("usuarioLogado") != null) {
                Usuario logado = (Usuario) session.getAttribute("usuarioLogado");
                resp.getWriter().write(gson.toJson(logado));
            } else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("{\"erro\":\"Nenhum usuário logado\"}");
            }
            return;
        }

        resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Endpoint inválido: " + path);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        configurarCORS(resp);
        String path = req.getPathInfo();

        if (path == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Endpoint inválido");
            return;
        }

        Usuario usuario = gson.fromJson(req.getReader(), Usuario.class);

        switch (path) {
            case "/cadastrar":
                service.adicionarUsuario(usuario);
                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.getWriter().write("{\"mensagem\":\"Usuário cadastrado com sucesso\"}");
                break;

            case "/login":
                Usuario existente = service.buscarUsuarioPorEmailESenha(usuario.getEmail(), usuario.getSenha());
                if (existente != null) {
                    // Cria sessão e armazena o usuário logado
                    HttpSession session = req.getSession(true);
                    session.setAttribute("usuarioLogado", existente);

                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.setContentType("application/json");
                    resp.getWriter().write(gson.toJson(existente));
                } else {
                    resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Credenciais inválidas");
                }
                break;

            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Caminho não encontrado: " + path);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        configurarCORS(resp);
        String path = req.getPathInfo();

        if ("/logout".equals(path)) {
            HttpSession session = req.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("{\"mensagem\":\"Logout realizado com sucesso\"}");
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Endpoint inválido para DELETE");
        }
    }
}



