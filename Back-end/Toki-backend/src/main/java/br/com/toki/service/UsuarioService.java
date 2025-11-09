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

    // 🔹 NOVO MÉTODO: usado no login
    public Usuario buscarUsuarioPorEmailESenha(String email, String senha) {
        return dao.buscarUsuarioPorEmailESenha(email, senha);
    }
}
