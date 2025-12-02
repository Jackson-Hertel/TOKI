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
        dao.adicionarUsuarioComHash(usuario);
    }

    public Usuario buscarUsuarioPorId(int id) {
        return dao.buscarUsuarioPorId(id);
    }

    public List<Usuario> listarUsuarios() {
        return dao.listarUsuarios();
    }

    public Usuario buscarUsuarioPorEmail(String email) {
        return dao.buscarUsuarioPorEmail(email);
    }

    public Usuario login(String email, String senha) {
        return dao.loginUsuario(email, senha);
    }

    public Usuario atualizarUsuario(Usuario usuario) {
        dao.atualizarUsuario(usuario);
        return usuario;
    }

    // ===========================
    // Atualizações específicas
    // ===========================

    public Usuario atualizarAparencia(int id, String tema, String cor, String inicioSemana) {

        Usuario u = dao.buscarUsuarioPorId(id);
        if (u == null) return null;

        if (tema != null) u.setTema(tema);
        if (cor != null) u.setCorPrincipal(cor);
        if (inicioSemana != null) u.setInicioSemana(inicioSemana);

        dao.atualizarUsuario(u);
        return u;
    }

    public Usuario atualizarNotificacoes(int id, boolean receber, String metodo, String antecedencia) {

        Usuario u = dao.buscarUsuarioPorId(id);
        if (u == null) return null;

        u.setReceberLembretes(receber);
        u.setMetodoLembrete(metodo);
        u.setAntecedencia(antecedencia);

        dao.atualizarUsuario(u);
        return u;
    }

    public Usuario atualizarConta(
            int id,
            String nome,
            String email,
            String telefone,
            String fotoPerfil
    ) {

        Usuario u = dao.buscarUsuarioPorId(id);
        if (u == null) return null;

        if (nome != null) u.setNome(nome);
        if (email != null) u.setEmail(email);
        if (telefone != null) u.setTelefone(telefone);
        if (fotoPerfil != null) u.setFotoPerfil(fotoPerfil);

        dao.atualizarUsuario(u);
        return u;
    }

    // ===========================
    // Recuperação de senha
    // ===========================

    public void gerarCodigoRecuperacao(String email) throws MessagingException {
        String codigo = String.valueOf((int) (Math.random() * 899999 + 100000));
        dao.salvarCodigo(email, codigo);
        emailService.enviarCodigo(email, codigo);
    }

    public boolean redefinirSenha(String email, String codigo, String novaSenha) {
        return dao.redefinirSenha(email, codigo, novaSenha);
    }

    public boolean validarCodigo(String email, String codigo) {
        return dao.validarCodigo(email, codigo);
    }
}
