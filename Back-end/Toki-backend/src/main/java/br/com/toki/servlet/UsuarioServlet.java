package br.com.toki.servlet;

import br.com.toki.model.Usuario;
import br.com.toki.service.UsuarioService;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;

@WebServlet("/toki/usuario/*")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 1MB
        maxFileSize = 5 * 1024 * 1024,   // 5MB
        maxRequestSize = 10 * 1024 * 1024 // 10MB
)
public class UsuarioServlet extends HttpServlet {

    private final UsuarioService service = new UsuarioService();
    private final Gson gson = new Gson();

    private void configurarCORS(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:5500");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        configurarCORS(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        configurarCORS(resp);
        resp.setContentType("application/json;charset=UTF-8");

        String path = req.getPathInfo();
        if ("/logado".equals(path)) {
            HttpSession session = req.getSession(false);
            if (session != null && session.getAttribute("usuarioLogado") != null) {
                Usuario u = (Usuario) session.getAttribute("usuarioLogado");
                u.setSenha(null);
                resp.getWriter().write(gson.toJson(u));
            } else {
                resp.setStatus(401);
                resp.getWriter().write("{\"erro\":\"Nenhum usuário logado\"}");
            }
            return;
        }

        if (path == null || path.equals("/")) {
            List<Usuario> usuarios = service.listarUsuarios();
            resp.getWriter().write(gson.toJson(usuarios));
            return;
        }

        resp.setStatus(404);
        resp.getWriter().write("{\"erro\":\"Endpoint inválido\"}");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        configurarCORS(resp);
        resp.setContentType("application/json;charset=UTF-8");

        String path = req.getPathInfo();
        if (path == null) {
            resp.sendError(400, "Endpoint inválido");
            return;
        }

        switch (path) {
            case "/cadastrar" -> cadastrarUsuario(req, resp);
            case "/login" -> loginUsuario(req, resp);
            case "/atualizarConta" -> atualizarConta(req, resp);
            case "/atualizarAparencia" -> atualizarAparencia(req, resp);
            case "/atualizarNotificacoes" -> atualizarNotificacoes(req, resp);
            case "/uploadFoto" -> uploadFoto(req, resp);
            case "/gerarCodigo" -> gerarCodigo(req, resp);
            case "/redefinirSenha" -> redefinirSenha(req, resp);
            default -> resp.sendError(404, "Endpoint não encontrado: " + path);
        }
    }

    // ====================== CADASTRO ======================
    private void cadastrarUsuario(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Usuario u;
        try {
            u = gson.fromJson(req.getReader(), Usuario.class);
        } catch (Exception e) {
            resp.setStatus(400);
            resp.getWriter().write("{\"erro\":\"JSON inválido\"}");
            return;
        }
        if (u == null || u.getNome() == null || u.getEmail() == null || u.getSenha() == null ||
                u.getNome().isBlank() || u.getEmail().isBlank() || u.getSenha().isBlank()) {
            resp.setStatus(400);
            resp.getWriter().write("{\"erro\":\"Preencha todos os campos\"}");
            return;
        }

        if (service.buscarUsuarioPorEmail(u.getEmail()) != null) {
            resp.setStatus(409);
            resp.getWriter().write("{\"erro\":\"E-mail já cadastrado\"}");
            return;
        }

        service.adicionarUsuario(u);
        resp.setStatus(201);
        resp.getWriter().write("{\"mensagem\":\"Usuário cadastrado com sucesso!\"}");
    }

    // ====================== LOGIN ======================
    private void loginUsuario(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Usuario usuarioReq;
        try {
            usuarioReq = gson.fromJson(req.getReader(), Usuario.class);
        } catch (Exception e) {
            resp.setStatus(400);
            resp.getWriter().write("{\"erro\":\"JSON inválido\"}");
            return;
        }
        if (usuarioReq == null || usuarioReq.getEmail() == null || usuarioReq.getSenha() == null) {
            resp.setStatus(400);
            resp.getWriter().write("{\"erro\":\"Todos os campos são obrigatórios\"}");
            return;
        }

        Usuario usuario = service.login(usuarioReq.getEmail(), usuarioReq.getSenha());
        if (usuario == null) {
            resp.setStatus(401);
            resp.getWriter().write("{\"erro\":\"Credenciais inválidas ou usuário inativo\"}");
            return;
        }

        HttpSession session = req.getSession(true);
        session.setAttribute("usuarioLogado", usuario);

        usuario.setSenha(null);
        resp.setStatus(200);
        resp.getWriter().write(gson.toJson(usuario));
    }

    // ====================== ATUALIZAÇÃO DE CONTA ======================
    private void atualizarConta(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("usuarioLogado") == null) {
            resp.setStatus(401);
            resp.getWriter().write("{\"erro\":\"Usuário não logado\"}");
            return;
        }

        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        Usuario u;
        try {
            u = gson.fromJson(req.getReader(), Usuario.class);
        } catch (Exception e) {
            resp.setStatus(400);
            resp.getWriter().write("{\"erro\":\"JSON inválido\"}");
            return;
        }
        if (u == null) {
            resp.setStatus(400);
            resp.getWriter().write("{\"erro\":\"Requisição vazia\"}");
            return;
        }

        Usuario atualizado = service.atualizarConta(
                usuarioLogado.getId(),
                u.getNome(),
                u.getEmail(),
                u.getTelefone(),
                usuarioLogado.getFotoPerfil()
        );
        session.setAttribute("usuarioLogado", atualizado);
        resp.getWriter().write(gson.toJson(atualizado));
    }

    // ====================== APARÊNCIA ======================
    private void atualizarAparencia(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.setStatus(401);
            resp.getWriter().write("{\"erro\":\"Usuário não logado\"}");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        Usuario u;
        try {
            u = gson.fromJson(req.getReader(), Usuario.class);
        } catch (Exception e) {
            resp.setStatus(400);
            resp.getWriter().write("{\"erro\":\"JSON inválido\"}");
            return;
        }
        if (u == null) {
            resp.setStatus(400);
            resp.getWriter().write("{\"erro\":\"Requisição vazia\"}");
            return;
        }

        Usuario atualizado = service.atualizarAparencia(
                usuario.getId(),
                u.getTema(),
                u.getCorPrincipal(),
                u.getInicioSemana()
        );

        session.setAttribute("usuarioLogado", atualizado);
        resp.getWriter().write(gson.toJson(atualizado));
    }

    // ====================== NOTIFICAÇÕES ======================
    private void atualizarNotificacoes(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.setStatus(401);
            resp.getWriter().write("{\"erro\":\"Usuário não logado\"}");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        Usuario u;
        try {
            u = gson.fromJson(req.getReader(), Usuario.class);
        } catch (Exception e) {
            resp.setStatus(400);
            resp.getWriter().write("{\"erro\":\"JSON inválido\"}");
            return;
        }
        if (u == null) {
            resp.setStatus(400);
            resp.getWriter().write("{\"erro\":\"Requisição vazia\"}");
            return;
        }

        Usuario atualizado = service.atualizarNotificacoes(
                usuario.getId(),
                u.isReceberLembretes(),
                u.getMetodoLembrete(),
                u.getAntecedencia()
        );
        session.setAttribute("usuarioLogado", atualizado);
        resp.getWriter().write(gson.toJson(atualizado));
    }

    // ====================== UPLOAD DE FOTO ======================
    private void uploadFoto(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.setStatus(401);
            resp.getWriter().write("{\"erro\":\"Usuário não logado\"}");
            return;
        }

        Usuario u = (Usuario) session.getAttribute("usuarioLogado");
        Part file = req.getPart("foto");

        if (file != null && file.getSize() > 0) {
            String contentType = file.getContentType();
            if (!contentType.startsWith("image/")) {
                resp.setStatus(400);
                resp.getWriter().write("{\"erro\":\"Arquivo deve ser uma imagem\"}");
                return;
            }

            try (InputStream is = file.getInputStream()) {
                byte[] bytes = is.readAllBytes();
                String base64 = Base64.getEncoder().encodeToString(bytes);

                u.setFotoPerfil(base64);
                service.atualizarUsuario(u);
                session.setAttribute("usuarioLogado", u);

                resp.getWriter().write("{\"fotoPerfil\":\"data:image/png;base64," + base64 + "\"}");
                return;
            }
        }

        resp.setStatus(400);
        resp.getWriter().write("{\"erro\":\"Nenhuma imagem enviada\"}");
    }

    // ====================== RECUPERAÇÃO DE SENHA ======================
    private void gerarCodigo(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Usuario u;
        try {
            u = gson.fromJson(req.getReader(), Usuario.class);
        } catch (Exception e) {
            resp.setStatus(400);
            resp.getWriter().write("{\"erro\":\"JSON inválido\"}");
            return;
        }
        if (u == null || u.getEmail() == null || u.getEmail().isBlank()) {
            resp.setStatus(400);
            resp.getWriter().write("{\"erro\":\"Email obrigatório\"}");
            return;
        }

        try {
            service.gerarCodigoRecuperacao(u.getEmail());
            resp.getWriter().write("{\"mensagem\":\"Código enviado\"}");
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"erro\":\"Erro ao enviar código\"}");
        }
    }

    private void redefinirSenha(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        class Req { String email; String codigo; String senha; }
        Req r;
        try {
            r = gson.fromJson(req.getReader(), Req.class);
        } catch (Exception e) {
            resp.setStatus(400);
            resp.getWriter().write("{\"erro\":\"JSON inválido\"}");
            return;
        }

        if (r == null || r.email == null || r.codigo == null || r.senha == null ||
                r.email.isBlank() || r.codigo.isBlank() || r.senha.isBlank()) {
            resp.setStatus(400);
            resp.getWriter().write("{\"erro\":\"Todos os campos são obrigatórios\"}");
            return;
        }

        boolean sucesso = service.redefinirSenha(r.email, r.codigo, r.senha);
        if (sucesso) {
            resp.getWriter().write("{\"mensagem\":\"Senha redefinida\"}");
        } else {
            resp.setStatus(400);
            resp.getWriter().write("{\"erro\":\"Código inválido ou expirado\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        configurarCORS(resp);

        if ("/logout".equals(req.getPathInfo())) {
            HttpSession session = req.getSession(false);
            if (session != null) session.invalidate();
            resp.getWriter().write("{\"mensagem\":\"Logout realizado\"}");
            return;
        }

        resp.sendError(404, "Endpoint inválido");
    }
}
