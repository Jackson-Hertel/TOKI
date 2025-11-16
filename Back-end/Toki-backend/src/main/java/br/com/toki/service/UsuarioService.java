package br.com.toki.service;

import br.com.toki.dao.UsuarioDAO;
import br.com.toki.model.Usuario;
import java.util.List;

public class UsuarioService {
    private UsuarioDAO dao = new UsuarioDAO();

    public void criarTabela() {
        dao.criarTabela();
    }

    public void adicionarUsuario(Usuario u) {
        dao.adicionarUsuario(u);
    }

    public List<Usuario> listarUsuarios() {
        return dao.listarUsuarios();
    }

    // 🔹 usado no login
    public Usuario buscarUsuarioPorEmailESenha(String email, String senha) {
        return dao.buscarUsuarioPorEmailESenha(email, senha);
    }

    // 🔹 novo método para atualizar a foto de perfil
    public void atualizarFotoPerfil(int usuarioId, String fotoBase64) {
        dao.atualizarFotoPerfil(usuarioId, fotoBase64);
    }

    // =====> NOVO MÉTODO
    public Usuario atualizarUsuario(Usuario usuario) {
        // Chama o DAO para atualizar os campos no banco
        dao.atualizarUsuario(usuario);
        return usuario;
    }
}
