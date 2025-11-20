package br.com.toki.dao;

import br.com.toki.model.Usuario;
import org.mindrot.jbcrypt.BCrypt;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class UsuarioDAO {

    private String url;
    private String user;
    private String password;

    public UsuarioDAO() {
        loadProperties();
    }

    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Não foi possível encontrar application.properties");
                return;
            }
            prop.load(input);
            url = prop.getProperty("h2.url");
            user = prop.getProperty("h2.user");
            password = prop.getProperty("h2.password");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public void criarTabela() {
        String sql = "CREATE TABLE IF NOT EXISTS usuario (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "nome VARCHAR(255)," +
                "email VARCHAR(255) UNIQUE," +
                "senha VARCHAR(255)," +
                "fotoPerfil VARCHAR(1000)," +
                "tema VARCHAR(20) DEFAULT 'claro'," +
                "corPrincipal VARCHAR(20) DEFAULT 'azul'," +
                "inicioSemana VARCHAR(20) DEFAULT 'domingo'," +
                "feriados BOOLEAN DEFAULT FALSE," +
                "aniversarios BOOLEAN DEFAULT FALSE," +
                "concluidos BOOLEAN DEFAULT FALSE," +
                "verificado BOOLEAN DEFAULT FALSE," +
                "ativo BOOLEAN DEFAULT FALSE," +
                "codigo_verificacao VARCHAR(10)," +
                "expiracao_codigo TIMESTAMP" +
                ")";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void adicionarUsuarioComHash(Usuario u) {
        String sql = "INSERT INTO usuario(nome,email,senha,fotoPerfil,tema,corPrincipal,inicioSemana,feriados,aniversarios,concluidos) " +
                "VALUES(?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            String senhaHash = BCrypt.hashpw(u.getSenha(), BCrypt.gensalt());
            ps.setString(1, u.getNome());
            ps.setString(2, u.getEmail());
            ps.setString(3, senhaHash);
            ps.setString(4, u.getFotoPerfil());
            ps.setString(5, u.getTema());
            ps.setString(6, u.getCorPrincipal());
            ps.setString(7, u.getInicioSemana());
            ps.setBoolean(8, u.isFeriados());
            ps.setBoolean(9, u.isAniversarios());
            ps.setBoolean(10, u.isConcluidos());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Usuario buscarUsuarioPorEmail(String email) {
        String sql = "SELECT * FROM usuario WHERE email = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setNome(rs.getString("nome"));
                u.setEmail(rs.getString("email"));
                u.setSenha(rs.getString("senha"));
                u.setFotoPerfil(rs.getString("fotoPerfil"));
                u.setTema(rs.getString("tema"));
                u.setCorPrincipal(rs.getString("corPrincipal"));
                u.setInicioSemana(rs.getString("inicioSemana"));
                u.setFeriados(rs.getBoolean("feriados"));
                u.setAniversarios(rs.getBoolean("aniversarios"));
                u.setConcluidos(rs.getBoolean("concluidos"));
                u.setAtivo(rs.getBoolean("ativo"));
                u.setVerificado(rs.getBoolean("verificado"));
                return u;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean login(String email, String senha) {
        Usuario u = buscarUsuarioPorEmail(email);
        if (u != null && u.isAtivo() && u.isVerificado()) {
            return BCrypt.checkpw(senha, u.getSenha());
        }
        return false;
    }

    public void salvarCodigo(String email, String codigo) {
        String sql = "UPDATE usuario SET codigo_verificacao = ?, expiracao_codigo = ? WHERE email = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            Timestamp expiracao = Timestamp.valueOf(java.time.LocalDateTime.now().plusMinutes(10));
            ps.setString(1, codigo);
            ps.setTimestamp(2, expiracao);
            ps.setString(3, email);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean validarCodigo(String email, String codigo) {
        String sql = "SELECT codigo_verificacao, expiracao_codigo FROM usuario WHERE email = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String codigoBanco = rs.getString("codigo_verificacao");
                Timestamp expiracao = rs.getTimestamp("expiracao_codigo");
                return codigoBanco != null &&
                        codigoBanco.equals(codigo) &&
                        expiracao != null &&
                        expiracao.toLocalDateTime().isAfter(java.time.LocalDateTime.now());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ==========================
// Ativar usuário (marca ativo = true)
// ==========================
    public void ativarUsuario(String email) {
        String sql = "UPDATE usuario SET ativo = TRUE WHERE email = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public Usuario loginUsuario(String email, String senha) {
        Usuario u = buscarUsuarioPorEmail(email);
        if (u != null && BCrypt.checkpw(senha, u.getSenha())) {
            return u;
        }
        return null;
    }


    // ==========================
// Atualizar usuário (nome, email, senha, ativo, etc.)
// ==========================
    public void atualizarUsuario(Usuario u) {
        String sql = "UPDATE usuario SET " +
                "nome = ?, " +
                "email = ?, " +
                "senha = ?, " +
                "fotoPerfil = ?, " +
                "tema = ?, " +
                "corPrincipal = ?, " +
                "inicioSemana = ?, " +
                "feriados = ?, " +
                "aniversarios = ?, " +
                "concluidos = ?, " +
                "verificado = ?, " +
                "ativo = ? " +
                "WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getNome());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getSenha());
            ps.setString(4, u.getFotoPerfil());
            ps.setString(5, u.getTema());
            ps.setString(6, u.getCorPrincipal());
            ps.setString(7, u.getInicioSemana());
            ps.setBoolean(8, u.isFeriados());
            ps.setBoolean(9, u.isAniversarios());
            ps.setBoolean(10, u.isConcluidos());
            ps.setBoolean(11, u.isVerificado());
            ps.setBoolean(12, u.isAtivo());
            ps.setInt(13, u.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Usuario> listarUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuario";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setNome(rs.getString("nome"));
                u.setEmail(rs.getString("email"));
                u.setSenha(rs.getString("senha"));
                u.setFotoPerfil(rs.getString("fotoPerfil"));
                u.setTema(rs.getString("tema"));
                u.setCorPrincipal(rs.getString("corPrincipal"));
                u.setInicioSemana(rs.getString("inicioSemana"));
                u.setFeriados(rs.getBoolean("feriados"));
                u.setAniversarios(rs.getBoolean("aniversarios"));
                u.setConcluidos(rs.getBoolean("concluidos"));
                u.setAtivo(rs.getBoolean("ativo"));
                u.setVerificado(rs.getBoolean("verificado"));
                usuarios.add(u);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }


    public boolean redefinirSenha(String email, String codigo, String novaSenha) {
        if (validarCodigo(email, codigo)) {
            String sql = "UPDATE usuario SET senha = ?, codigo_verificacao = NULL, expiracao_codigo = NULL WHERE email = ?";
            try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                String hash = BCrypt.hashpw(novaSenha, BCrypt.gensalt());
                ps.setString(1, hash);
                ps.setString(2, email);
                ps.executeUpdate();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
