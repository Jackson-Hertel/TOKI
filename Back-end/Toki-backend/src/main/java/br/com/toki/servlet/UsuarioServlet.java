package br.com.toki.servlet;

import br.com.toki.model.Usuario;
import br.com.toki.service.UsuarioService;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

@WebServlet("/toki/usuario/*")
@MultipartConfig
public class UsuarioServlet extends HttpServlet {

    private final UsuarioService service = new UsuarioService();
    private final Gson gson = new Gson();

    private void configurarCORS(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:5500");
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
        resp.setContentType("application/json");
        String path = req.getPathInfo();

        if (path == null || path.equals("/")) {
            List<Usuario> usuarios = service.listarUsuarios();
            resp.getWriter().write(gson.toJson(usuarios));
            return;
        }

        if (path.equals("/logado")) {
            HttpSession session = req.getSession(false);
            if (session != null && session.getAttribute("usuarioLogado") != null) {
                resp.getWriter().write(gson.toJson(session.getAttribute("usuarioLogado")));
            } else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("{\"erro\":\"Nenhum usuário logado\"}");
            }
            return;
        }

        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        resp.getWriter().write("{\"erro\":\"Endpoint inválido: " + path + "\"}");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        configurarCORS(resp);
        String path = req.getPathInfo();

        if (path == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Endpoint inválido");
            return;
        }

        switch (path) {
            case "/cadastrar" -> cadastrarUsuario(req, resp);
            case "/login" -> loginUsuario(req, resp);
            case "/atualizar" -> atualizarUsuario(req, resp);
            case "/upload" -> uploadFotoPerfil(req, resp);
            case "/gerarCodigo" -> gerarCodigo(req, resp);
            case "/redefinirSenha" -> redefinirSenha(req, resp);
            default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Caminho não encontrado: " + path);
        }
    }

    private void cadastrarUsuario(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String nome = req.getParameter("nome");
        String email = req.getParameter("email");
        String senha = req.getParameter("senha");

        if (nome == null || email == null || senha == null ||
                nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"erro\":\"Preencha todos os campos.\"}");
            return;
        }

        if (service.buscarUsuarioPorEmail(email) != null) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            resp.getWriter().write("{\"erro\":\"E-mail já cadastrado.\"}");
            return;
        }

        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(senha);

        service.adicionarUsuario(usuario);

        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.getWriter().write("{\"mensagem\":\"Usuário cadastrado com sucesso\"}");
    }

    private void loginUsuario(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String email = req.getParameter("email");
        String senha = req.getParameter("senha");

        if (email == null || senha == null || email.isEmpty() || senha.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"erro\":\"Preencha e-mail e senha.\"}");
            return;
        }

        Usuario usuario = service.buscarUsuarioPorEmailESenha(email, senha);
        if (usuario != null) {
            HttpSession session = req.getSession(true);
            session.setAttribute("usuarioLogado", usuario);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            resp.getWriter().write(gson.toJson(usuario));
        } else {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"erro\":\"Credenciais inválidas ou usuário não ativo\"}");
        }
    }

    private void atualizarUsuario(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("usuarioLogado") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"erro\":\"Usuário não logado\"}");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        String nome = req.getParameter("nome");
        String email = req.getParameter("email");
        String senha = req.getParameter("senha");

        if (nome != null) usuario.setNome(nome);
        if (email != null) usuario.setEmail(email);
        if (senha != null && !senha.isEmpty()) usuario.setSenha(senha);

        service.atualizarUsuario(usuario);
        session.setAttribute("usuarioLogado", usuario);

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(usuario));
    }

    private void uploadFotoPerfil(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("usuarioLogado") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"erro\":\"Usuário não logado\"}");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        Part filePart = req.getPart("fotoPerfil");

        if (filePart == null || filePart.getSize() == 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Nenhum arquivo enviado");
            return;
        }

        String nomeArquivo = "usuario" + usuario.getId() + "_" + System.currentTimeMillis() + ".jpg";
        String uploadDir = req.getServletContext().getRealPath("/uploads/perfil/");
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        String caminhoCompleto = uploadDir + File.separator + nomeArquivo;
        filePart.write(caminhoCompleto);

        usuario.setFotoPerfil("uploads/perfil/" + nomeArquivo);
        service.atualizarUsuario(usuario);
        session.setAttribute("usuarioLogado", usuario);

        resp.setContentType("application/json");
        resp.getWriter().write("{\"caminho\":\"" + usuario.getFotoPerfil() + "\"}");
    }

    private void gerarCodigo(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String email = req.getParameter("email");

        if (email == null || email.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"erro\":\"Informe o e-mail.\"}");
            return;
        }

        try {
            service.gerarCodigoRecuperacao(email);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"mensagem\":\"Código de recuperação enviado para o e-mail\"}");
        } catch (jakarta.mail.MessagingException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"erro\":\"Não foi possível enviar o código por e-mail\"}");
        }
    }

    private void redefinirSenha(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String email = req.getParameter("email");
        String codigo = req.getParameter("codigo");
        String senha = req.getParameter("senha");

        if (email == null || codigo == null || senha == null ||
                email.isEmpty() || codigo.isEmpty() || senha.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"erro\":\"Preencha todos os campos.\"}");
            return;
        }

        boolean sucesso = service.redefinirSenha(email, codigo, senha);
        if (sucesso) {
            resp.setContentType("application/json");
            resp.getWriter().write("{\"mensagem\":\"Senha redefinida com sucesso!\"}");
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"erro\":\"Código inválido ou expirado\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        configurarCORS(resp);
        String path = req.getPathInfo();

        if ("/logout".equals(path)) {
            HttpSession session = req.getSession(false);
            if (session != null) session.invalidate();

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("{\"mensagem\":\"Logout realizado com sucesso\"}");
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Endpoint inválido para DELETE");
        }
    }
}
