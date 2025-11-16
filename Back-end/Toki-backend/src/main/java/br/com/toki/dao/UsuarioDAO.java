package br.com.toki.dao;

import br.com.toki.model.Usuario;
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
                "email VARCHAR(255)," +
                "senha VARCHAR(255)," +
                "fotoPerfil VARCHAR(1000)," +
                "tema VARCHAR(20) DEFAULT 'claro'," +
                "corPrincipal VARCHAR(20) DEFAULT 'azul'," +
                "inicioSemana VARCHAR(20) DEFAULT 'domingo'," +
                "feriados BOOLEAN DEFAULT FALSE," +
                "aniversarios BOOLEAN DEFAULT FALSE," +
                "concluidos BOOLEAN DEFAULT FALSE" +
                ")";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void atualizarFotoPerfil(int usuarioId, String fotoBase64) {
        String sql = "UPDATE usuario SET fotoPerfil = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fotoBase64);
            stmt.setInt(2, usuarioId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void adicionarUsuario(Usuario u) {
        String sql = "INSERT INTO usuario(nome, email, senha, fotoPerfil, tema, corPrincipal, inicioSemana, feriados, aniversarios, concluidos) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public List<Usuario> listarUsuarios() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuario";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
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
                lista.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }


    public Usuario buscarUsuarioPorEmailESenha(String email, String senha) {
        String sql = "SELECT * FROM usuario WHERE email = ? AND senha = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, senha);
            ResultSet rs = stmt.executeQuery();

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
                return u;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void atualizarUsuario(Usuario usuario) {
        String sql = "UPDATE usuario SET nome = ?, email = ?, senha = ?, fotoPerfil = ?, tema = ?, corPrincipal = ?, inicioSemana = ?, feriados = ?, aniversarios = ?, concluidos = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, usuario.getSenha());
            ps.setString(4, usuario.getFotoPerfil());
            ps.setString(5, usuario.getTema());
            ps.setString(6, usuario.getCorPrincipal());
            ps.setString(7, usuario.getInicioSemana());
            ps.setBoolean(8, usuario.isFeriados());
            ps.setBoolean(9, usuario.isAniversarios());
            ps.setBoolean(10, usuario.isConcluidos());
            ps.setInt(11, usuario.getId());

            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
