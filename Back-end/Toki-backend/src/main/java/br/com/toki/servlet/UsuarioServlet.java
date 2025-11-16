package br.com.toki.servlet;

import br.com.toki.model.Usuario;
import br.com.toki.service.UsuarioService;
import com.google.gson.Gson;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/usuarios/*")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, // 1MB
        maxFileSize = 10 * 1024 * 1024,  // 10MB
        maxRequestSize = 20 * 1024 * 1024) // 20MB
public class UsuarioServlet extends HttpServlet {

    private final UsuarioService service = new UsuarioService();
    private final Gson gson = new Gson();

    private void configurarCORS(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:5500"); // porta do frontend
        resp.setHeader("Access-Control-Allow-Credentials", "true");
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
            List<Usuario> usuarios = service.listarUsuarios();
            resp.setContentType("application/json");
            resp.getWriter().write(gson.toJson(usuarios));
            return;
        }

        if (path.equals("/logado")) {
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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, jakarta.servlet.ServletException {
        configurarCORS(resp);
        String path = req.getPathInfo();
        if (path == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Endpoint inválido");
            return;
        }

        if (path.equals("/upload")) {
            // Upload de foto
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("usuarioLogado") == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("{\"erro\":\"Usuário não logado\"}");
                return;
            }

            Usuario logado = (Usuario) session.getAttribute("usuarioLogado");
            Part filePart = req.getPart("fotoPerfil");
            if (filePart == null || filePart.getSize() == 0) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Nenhum arquivo enviado");
                return;
            }

            String nomeArquivo = "usuario" + logado.getId() + "_" + System.currentTimeMillis() + ".jpg";
            String uploadDir = req.getServletContext().getRealPath("/uploads/perfil/");
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String caminhoCompleto = uploadDir + File.separator + nomeArquivo;
            filePart.write(caminhoCompleto);

            // Atualiza apenas o caminho no banco
            String caminhoRelativo = "uploads/perfil/" + nomeArquivo;
            logado.setFotoPerfil(caminhoRelativo);
            service.atualizarUsuario(logado);
            session.setAttribute("usuarioLogado", logado);

            resp.setContentType("application/json");
            resp.getWriter().write("{\"caminho\":\"" + caminhoRelativo + "\"}");
            return;
        }

        // Para os demais endpoints (cadastrar, login, atualizar)
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
                    HttpSession session = req.getSession(true);
                    session.setAttribute("usuarioLogado", existente);
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.setContentType("application/json");
                    resp.getWriter().write(gson.toJson(existente));
                } else {
                    resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Credenciais inválidas");
                }
                break;

            case "/atualizar":
                HttpSession session = req.getSession(false);
                if (session == null || session.getAttribute("usuarioLogado") == null) {
                    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    resp.getWriter().write("{\"erro\":\"Usuário não logado\"}");
                    return;
                }

                Usuario logado = (Usuario) session.getAttribute("usuarioLogado");

                // Atualiza campos
                if (usuario.getNome() != null) logado.setNome(usuario.getNome());
                if (usuario.getEmail() != null) logado.setEmail(usuario.getEmail());
                if (usuario.getSenha() != null && !usuario.getSenha().isEmpty()) logado.setSenha(usuario.getSenha());
                if (usuario.getFotoPerfil() != null) logado.setFotoPerfil(usuario.getFotoPerfil());
                if (usuario.getTema() != null) logado.setTema(usuario.getTema());
                if (usuario.getCorPrincipal() != null) logado.setCorPrincipal(usuario.getCorPrincipal());
                logado.setInicioSemana(usuario.getInicioSemana());
                logado.setFeriados(usuario.isFeriados());
                logado.setAniversarios(usuario.isAniversarios());
                logado.setConcluidos(usuario.isConcluidos());

                service.atualizarUsuario(logado);
                session.setAttribute("usuarioLogado", logado);

                resp.setStatus(HttpServletResponse.SC_OK);
                resp.setContentType("application/json");
                resp.getWriter().write(gson.toJson(logado));
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
