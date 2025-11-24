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
        String sql = """
            CREATE TABLE IF NOT EXISTS USUARIO (
                ID INT AUTO_INCREMENT PRIMARY KEY,
                NOME VARCHAR(255),
                EMAIL VARCHAR(255) UNIQUE,
                SENHA VARCHAR(255),
                FOTO_PERFIL VARCHAR(1000),
                TEMA VARCHAR(20) DEFAULT 'CLARO',
                COR_PRINCIPAL VARCHAR(20) DEFAULT 'AZUL',
                INICIO_SEMANA VARCHAR(20) DEFAULT 'DOMINGO',
                FERIADOS BOOLEAN DEFAULT FALSE,
                ANIVERSARIOS BOOLEAN DEFAULT FALSE,
                CONCLUIDOS BOOLEAN DEFAULT FALSE,
                VERIFICADO BOOLEAN DEFAULT FALSE,
                ATIVO BOOLEAN DEFAULT FALSE,
                CODIGO_VERIFICACAO VARCHAR(10),
                EXPIRACAO_CODIGO TIMESTAMP
            )
        """;
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ===========================
    // CRUD de usuário
    // ===========================
    public void adicionarUsuarioComHash(Usuario u) {
        String sql = """
            INSERT INTO USUARIO (
                NOME, EMAIL, SENHA, FOTO_PERFIL, TEMA, COR_PRINCIPAL, INICIO_SEMANA, 
                FERIADOS, ANIVERSARIOS, CONCLUIDOS
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
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
        String sql = "SELECT * FROM USUARIO WHERE EMAIL = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapearUsuario(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Usuario loginUsuario(String email, String senha) {
        Usuario u = buscarUsuarioPorEmail(email);
        if (u != null && BCrypt.checkpw(senha, u.getSenha())) {
            return u;
        }
        return null;
    }

    public List<Usuario> listarUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM USUARIO";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }

    public void atualizarUsuario(Usuario u) {
        String sql = """
            UPDATE USUARIO SET 
                NOME = ?, EMAIL = ?, SENHA = ?, FOTO_PERFIL = ?, TEMA = ?, COR_PRINCIPAL = ?, INICIO_SEMANA = ?, 
                FERIADOS = ?, ANIVERSARIOS = ?, CONCLUIDOS = ?, VERIFICADO = ?, ATIVO = ? 
            WHERE ID = ?
        """;
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            String senha = u.getSenha();
            String senhaHash = (senha != null && senha.startsWith("$2")) ? senha : BCrypt.hashpw(senha, BCrypt.gensalt());

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
            ps.setBoolean(11, u.isVerificado());
            ps.setBoolean(12, u.isAtivo());
            ps.setInt(13, u.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ===========================
    // Recuperação de senha
    // ===========================
    public void salvarCodigo(String email, String codigo) {
        String sql = "UPDATE USUARIO SET CODIGO_VERIFICACAO = ?, EXPIRACAO_CODIGO = ? WHERE EMAIL = ?";
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
        String sql = "SELECT CODIGO_VERIFICACAO, EXPIRACAO_CODIGO FROM USUARIO WHERE EMAIL = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String codigoBanco = rs.getString("CODIGO_VERIFICACAO");
                Timestamp expiracao = rs.getTimestamp("EXPIRACAO_CODIGO");
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

    public boolean redefinirSenha(String email, String codigo, String novaSenha) {
        if (validarCodigo(email, codigo)) {
            String sql = "UPDATE USUARIO SET SENHA = ?, CODIGO_VERIFICACAO = NULL, EXPIRACAO_CODIGO = NULL WHERE EMAIL = ?";
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

    public void ativarUsuario(String email) {
        String sql = "UPDATE USUARIO SET ATIVO = TRUE WHERE EMAIL = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ===========================
    // Mapper ResultSet → Usuario
    // ===========================
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("ID"));
        u.setNome(rs.getString("NOME"));
        u.setEmail(rs.getString("EMAIL"));
        u.setSenha(rs.getString("SENHA"));
        u.setFotoPerfil(rs.getString("FOTO_PERFIL"));
        u.setTema(rs.getString("TEMA"));
        u.setCorPrincipal(rs.getString("COR_PRINCIPAL"));
        u.setInicioSemana(rs.getString("INICIO_SEMANA"));
        u.setFeriados(rs.getBoolean("FERIADOS"));
        u.setAniversarios(rs.getBoolean("ANIVERSARIOS"));
        u.setConcluidos(rs.getBoolean("CONCLUIDOS"));
        u.setVerificado(rs.getBoolean("VERIFICADO"));
        u.setAtivo(rs.getBoolean("ATIVO"));
        return u;
    }
}
