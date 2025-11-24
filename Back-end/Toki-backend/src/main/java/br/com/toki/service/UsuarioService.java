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

    public void adicionarUsuario(Usuario usuario) {
        // Adiciona usuário com senha já hasheada internamente no DAO
        dao.adicionarUsuarioComHash(usuario);
    }

    public List<Usuario> listarUsuarios() {
        return dao.listarUsuarios();
    }

    public Usuario buscarUsuarioPorEmail(String email) {
        return dao.buscarUsuarioPorEmail(email);
    }

    public Usuario buscarUsuarioPorEmailESenha(String email, String senha) {
        // Retorna null se login inválido
        return dao.loginUsuario(email, senha);
    }

    public Usuario atualizarUsuario(Usuario usuario) {
        dao.atualizarUsuario(usuario);
        return usuario;
    }

    // ===========================
    // Recuperação de senha
    // ===========================

    public void gerarCodigoRecuperacao(String email) throws MessagingException {
        // Gera código de 6 dígitos
        String codigo = String.valueOf((int) (Math.random() * 899999 + 100000));
        dao.salvarCodigo(email, codigo);
        emailService.enviarCodigo(email, codigo);
    }

    public boolean redefinirSenha(String email, String codigo, String novaSenha) {
        // Retorna true se a senha foi alterada corretamente
        return dao.redefinirSenha(email, codigo, novaSenha);
    }

    public void ativarUsuario(String email) {
        dao.ativarUsuario(email);
    }

    public boolean validarCodigo(String email, String codigo) {
        // Apenas valida, não altera nada
        return dao.validarCodigo(email, codigo);
    }
}
