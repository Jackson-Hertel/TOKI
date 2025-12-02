package br.com.toki.dao;

import br.com.toki.model.Usuario;
import org.mindrot.jbcrypt.BCrypt;

import java.io.InputStream;
import java.io.StringReader;
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

    // ========================================================
    // CRIAR TABELA USUARIO
    // ========================================================
    public void criarTabela() {
        String sql = """
            CREATE TABLE IF NOT EXISTS USUARIO (
                ID INT AUTO_INCREMENT PRIMARY KEY,
                NOME VARCHAR(255),
                EMAIL VARCHAR(255) UNIQUE,
                SENHA VARCHAR(255),
                FOTO_PERFIL CLOB,
                TEMA VARCHAR(20) DEFAULT 'CLARO',
                COR_PRINCIPAL VARCHAR(20) DEFAULT 'AZUL',
                INICIO_SEMANA VARCHAR(20) DEFAULT 'DOMINGO',
                FERIADOS BOOLEAN DEFAULT FALSE,
                ANIVERSARIOS BOOLEAN DEFAULT FALSE,
                CONCLUIDOS BOOLEAN DEFAULT FALSE,
                RECEBER_LEMBRETES BOOLEAN DEFAULT FALSE,
                METODO_LEMBRETE VARCHAR(20) DEFAULT 'app',
                ANTECEDENCIA VARCHAR(20) DEFAULT '10min',
                TELEFONE VARCHAR(30),
                CODIGO_VERIFICACAO VARCHAR(10),
                EXPIRACAO_CODIGO TIMESTAMP
            )
        """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ========================================================
    // INSERIR USUARIO COM HASH
    // ========================================================
    public void adicionarUsuarioComHash(Usuario u) {
        String sql = """
            INSERT INTO USUARIO (
                NOME, EMAIL, SENHA, FOTO_PERFIL, TEMA, COR_PRINCIPAL,
                INICIO_SEMANA, FERIADOS, ANIVERSARIOS, CONCLUIDOS,
                RECEBER_LEMBRETES, METODO_LEMBRETE, ANTECEDENCIA, TELEFONE
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String senhaHash = BCrypt.hashpw(u.getSenha(), BCrypt.gensalt());

            ps.setString(1, u.getNome());
            ps.setString(2, u.getEmail());
            ps.setString(3, senhaHash);

            // FOTO_PERFIL como CLOB usando Reader
            if (u.getFotoPerfil() != null) {
                ps.setCharacterStream(4, new StringReader(u.getFotoPerfil()), u.getFotoPerfil().length());
            } else {
                ps.setNull(4, Types.CLOB);
            }

            ps.setString(5, u.getTema());
            ps.setString(6, u.getCorPrincipal());
            ps.setString(7, u.getInicioSemana());
            ps.setBoolean(8, u.isFeriados());
            ps.setBoolean(9, u.isAniversarios());
            ps.setBoolean(10, u.isConcluidos());
            ps.setBoolean(11, u.isReceberLembretes());
            ps.setString(12, u.getMetodoLembrete());
            ps.setString(13, u.getAntecedencia());
            ps.setString(14, u.getTelefone());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ========================================================
    // BUSCAR POR EMAIL
    // ========================================================
    public Usuario buscarUsuarioPorEmail(String email) {
        String sql = "SELECT * FROM USUARIO WHERE EMAIL = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return mapearUsuario(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Usuario buscarUsuarioPorId(int id) {
        String sql = "SELECT * FROM USUARIO WHERE ID = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapearUsuario(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // ========================================================
    // LOGIN
    // ========================================================
    public Usuario loginUsuario(String email, String senha) {
        Usuario u = buscarUsuarioPorEmail(email);

        if (u != null && BCrypt.checkpw(senha, u.getSenha())) {
            return u;
        }
        return null;
    }

    // ========================================================
    // LISTAR USUARIOS
    // ========================================================
    public List<Usuario> listarUsuarios() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM USUARIO";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(mapearUsuario(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // ========================================================
    // ATUALIZAR USUARIO
    // ========================================================
    public void atualizarUsuario(Usuario u) {
        String sql = """
            UPDATE USUARIO SET 
                NOME=?, EMAIL=?, SENHA=?, FOTO_PERFIL=?, TEMA=?, COR_PRINCIPAL=?, 
                INICIO_SEMANA=?, FERIADOS=?, ANIVERSARIOS=?, CONCLUIDOS=?, 
                RECEBER_LEMBRETES=?, METODO_LEMBRETE=?, ANTECEDENCIA=?,
                TELEFONE=?
            WHERE ID=?
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String hash;

            if (u.getSenha() == null || u.getSenha().isEmpty()) {
                hash = buscarUsuarioPorId(u.getId()).getSenha();
            } else if (u.getSenha().startsWith("$2")) {
                hash = u.getSenha();
            } else {
                hash = BCrypt.hashpw(u.getSenha(), BCrypt.gensalt());
            }

            ps.setString(1, u.getNome());
            ps.setString(2, u.getEmail());
            ps.setString(3, hash);

            if (u.getFotoPerfil() != null) {
                ps.setCharacterStream(4, new StringReader(u.getFotoPerfil()), u.getFotoPerfil().length());
            } else {
                ps.setNull(4, Types.CLOB);
            }

            ps.setString(5, u.getTema());
            ps.setString(6, u.getCorPrincipal());
            ps.setString(7, u.getInicioSemana());
            ps.setBoolean(8, u.isFeriados());
            ps.setBoolean(9, u.isAniversarios());
            ps.setBoolean(10, u.isConcluidos());
            ps.setBoolean(11, u.isReceberLembretes());
            ps.setString(12, u.getMetodoLembrete());
            ps.setString(13, u.getAntecedencia());
            ps.setString(14, u.getTelefone());
            ps.setInt(15, u.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ========================================================
    // SALVAR CÓDIGO DE RECUPERAÇÃO
    // ========================================================
    public void salvarCodigo(String email, String codigo) {
        String sql = "UPDATE USUARIO SET CODIGO_VERIFICACAO=?, EXPIRACAO_CODIGO=? WHERE EMAIL=?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, codigo);
            Timestamp expira = Timestamp.valueOf(
                    java.time.LocalDateTime.now().plusMinutes(10)
            );
            ps.setTimestamp(2, expira);
            ps.setString(3, email);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean validarCodigo(String email, String codigo) {
        String sql = "SELECT CODIGO_VERIFICACAO, EXPIRACAO_CODIGO FROM USUARIO WHERE EMAIL=?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return false;

            String banco = rs.getString("CODIGO_VERIFICACAO");
            Timestamp expira = rs.getTimestamp("EXPIRACAO_CODIGO");

            return banco != null &&
                    banco.equals(codigo) &&
                    expira != null &&
                    expira.toLocalDateTime().isAfter(java.time.LocalDateTime.now());

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean redefinirSenha(String email, String codigo, String novaSenha) {
        if (!validarCodigo(email, codigo)) {
            System.out.println("Código inválido ou expirado");
            return false;
        }

        String sql = "UPDATE USUARIO SET SENHA=?, CODIGO_VERIFICACAO=NULL, EXPIRACAO_CODIGO=NULL WHERE EMAIL=?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String hash = BCrypt.hashpw(novaSenha, BCrypt.gensalt());

            ps.setString(1, hash);
            ps.setString(2, email);

            ps.executeUpdate();

            System.out.println("Senha redefinida com sucesso!");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // ========================================================
    // MAPEAR USUARIO (CARREGAR DO BANCO)
    // ========================================================
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();

        u.setId(rs.getInt("ID"));
        u.setNome(rs.getString("NOME"));
        u.setEmail(rs.getString("EMAIL"));
        u.setSenha(rs.getString("SENHA"));

        Clob clob = rs.getClob("FOTO_PERFIL");
        if (clob != null) {
            u.setFotoPerfil(clob.getSubString(1, (int) clob.length()));
        } else {
            u.setFotoPerfil(null);
        }

        u.setTema(rs.getString("TEMA"));
        u.setCorPrincipal(rs.getString("COR_PRINCIPAL"));
        u.setInicioSemana(rs.getString("INICIO_SEMANA"));
        u.setFeriados(rs.getBoolean("FERIADOS"));
        u.setAniversarios(rs.getBoolean("ANIVERSARIOS"));
        u.setConcluidos(rs.getBoolean("CONCLUIDOS"));
        u.setReceberLembretes(rs.getBoolean("RECEBER_LEMBRETES"));
        u.setMetodoLembrete(rs.getString("METODO_LEMBRETE"));
        u.setAntecedencia(rs.getString("ANTECEDENCIA"));
        u.setTelefone(rs.getString("TELEFONE"));

        return u;
    }


}
