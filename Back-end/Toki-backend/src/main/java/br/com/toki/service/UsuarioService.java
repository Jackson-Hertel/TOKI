package br.com.toki.service;

import br.com.toki.dao.UsuarioDAO;
import br.com.toki.model.Usuario;
import jakarta.mail.MessagingException;

import java.util.List;

public class UsuarioService {

    private final UsuarioDAO dao = new UsuarioDAO();
    private final EmailService emailService = new EmailService();

    // ===========================
    // CRUD e login
    // ===========================

    public void criarTabela() {
        dao.criarTabela();
    }

    public void adicionarUsuario(Usuario u) {
        dao.adicionarUsuarioComHash(u);
    }

    public List<Usuario> listarUsuarios() {
        return dao.listarUsuarios();
    }

    public Usuario buscarUsuarioPorEmail(String email) {
        return dao.buscarUsuarioPorEmail(email);
    }

    public Usuario buscarUsuarioPorEmailESenha(String email, String senha) {
        return dao.loginUsuario(email, senha); // método que retorna Usuario ou null
    }

    public Usuario atualizarUsuario(Usuario u) {
        dao.atualizarUsuario(u);
        return u;
    }

    // ===========================
    // Recuperação de senha
    // ===========================

    public void gerarCodigoRecuperacao(String email) throws MessagingException {
        String codigo = String.valueOf((int)(Math.random() * 899999 + 100000));
        dao.salvarCodigo(email, codigo);
        emailService.enviarCodigo(email, codigo);
    }

    public boolean redefinirSenha(String email, String codigo, String novaSenha) {
        return dao.redefinirSenha(email, codigo, novaSenha);
    }

    public void ativarUsuario(String email) {
        dao.ativarUsuario(email);
    }
}
