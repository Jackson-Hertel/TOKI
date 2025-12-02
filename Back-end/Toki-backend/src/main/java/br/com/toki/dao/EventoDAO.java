package br.com.toki.dao;

import br.com.toki.model.Evento;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventoDAO {

    public EventoDAO() {
        // Nada aqui! Antes isso duplicava o banco.
    }

    public void criarTabela() {
        String sql = """
        CREATE TABLE IF NOT EXISTS EVENTOS (
            ID INT AUTO_INCREMENT PRIMARY KEY,
            TITULO VARCHAR(255) NOT NULL,
            DESCRICAO CLOB,
            DATA DATE NOT NULL,
            HORA TIME,
            HORA_FIM TIME,
            PRIORIDADE VARCHAR(20),
            REPETICAO VARCHAR(50),
            LEMBRETE_ENVIADO BOOLEAN DEFAULT FALSE,
            ALERTA VARCHAR(255),
            LOCAL VARCHAR(255),
            COR VARCHAR(20),
            USUARIO_ID INT NOT NULL,
            FOREIGN KEY (USUARIO_ID) REFERENCES USUARIO(ID) ON DELETE CASCADE
        )
    """;

        try (Connection conn = getConn();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabela EVENTOS criada ou já existente.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private Connection getConn() throws SQLException {
        return new Conexao().getConnection();
    }


    // ============================
    //   CRUD — ADICIONAR
    // ============================
    // ============================
    //   ADICIONAR EVENTO
    // ============================
    public void adicionarEvento(Evento e, int usuarioId) {
        String sql = """
        INSERT INTO EVENTOS
        (TITULO, DESCRICAO, DATA, HORA, HORA_FIM, LOCAL, ALERTA, COR, PRIORIDADE, REPETICAO, LEMBRETE_ENVIADO, USUARIO_ID)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection c = getConn();
             PreparedStatement stmt = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, e.getTitulo());
            stmt.setString(2, e.getDescricao());
            stmt.setDate(3, Date.valueOf(e.getData()));
            stmt.setTime(4, e.getHora() != null ? Time.valueOf(e.getHora()) : null);
            stmt.setTime(5, e.getHoraFim() != null ? Time.valueOf(e.getHoraFim()) : null);
            stmt.setString(6, e.getLocal());
            stmt.setString(7, e.getAlerta());
            stmt.setString(8, e.getCor());
            stmt.setString(9, e.getPrioridade());
            stmt.setString(10, e.getRepeticao());
            stmt.setBoolean(11, e.isLembreteEnviado());
            stmt.setInt(12, usuarioId);

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) e.setId(rs.getInt(1));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // ============================
    //   CRUD — ATUALIZAR LEMBRETE
    // ============================
    public void atualizarLembreteEnviado(Evento e) {
        String sql = "UPDATE eventos SET LEMBRETE_ENVIADO = ? WHERE ID = ?";

        try (Connection c = getConn();
             PreparedStatement stmt = c.prepareStatement(sql)) {

            stmt.setBoolean(1, e.isLembreteEnviado());
            stmt.setInt(2, e.getId());
            stmt.executeUpdate();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Erro ao atualizar lembrete enviado", ex);
        }
    }

    // ============================
    //   LISTAR POR USUÁRIO
    // ============================
    public List<Evento> listarPorUsuario(int usuarioId) {
        List<Evento> lista = new ArrayList<>();
        String sql = """
            SELECT * FROM EVENTOS
            WHERE USUARIO_ID = ?
            ORDER BY DATA ASC, HORA ASC
        """;

        try (Connection c = getConn();
             PreparedStatement stmt = c.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next())
                lista.add(map(rs, usuarioId));

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return lista;
    }

    // ============================
    //   ATUALIZAR EVENTO
    // ============================
    public void atualizarEvento(Evento e) {
        String sql = """
            UPDATE EVENTOS SET
            TITULO=?, DESCRICAO=?, DATA=?, HORA=?, HORA_FIM=?, LOCAL=?, ALERTA=?, COR=?, PRIORIDADE=?, REPETICAO=?
            WHERE ID=?
        """;

        try (Connection c = getConn();
             PreparedStatement stmt = c.prepareStatement(sql)) {

            stmt.setString(1, e.getTitulo());
            stmt.setString(2, e.getDescricao());
            stmt.setDate(3, Date.valueOf(e.getData()));
            stmt.setTime(4, e.getHora() != null ? Time.valueOf(e.getHora()) : null);
            stmt.setTime(5, e.getHoraFim() != null ? Time.valueOf(e.getHoraFim()) : null);
            stmt.setString(6, e.getLocal());
            stmt.setString(7, e.getAlerta());
            stmt.setString(8, e.getCor());
            stmt.setString(9, e.getPrioridade());
            stmt.setString(10, e.getRepeticao());
            stmt.setInt(11, e.getId());

            stmt.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // ============================
    //   DELETAR EVENTO
    // ============================
    public void deletarEvento(int id) {
        String sql = "DELETE FROM EVENTOS WHERE ID=?";

        try (Connection c = getConn();
             PreparedStatement stmt = c.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // ============================
    //   LISTAR POR DATA (TODOS)
    // ============================
    public List<Evento> listarPorData(String data) {
        List<Evento> lista = new ArrayList<>();
        String sql = """
            SELECT * FROM EVENTOS
            WHERE DATA = ?
            ORDER BY HORA ASC
        """;

        try (Connection c = getConn();
             PreparedStatement stmt = c.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(data));
            ResultSet rs = stmt.executeQuery();

            while (rs.next())
                lista.add(map(rs, rs.getInt("USUARIO_ID")));

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return lista;
    }

    // ============================
    //   LISTAR TODOS
    // ============================
    public List<Evento> listarTodos() {
        List<Evento> lista = new ArrayList<>();
        String sql = """
            SELECT * FROM EVENTOS
            ORDER BY DATA ASC, HORA ASC
        """;

        try (Connection c = getConn();
             Statement st = c.createStatement()) {

            ResultSet rs = st.executeQuery(sql);
            while (rs.next())
                lista.add(map(rs, rs.getInt("USUARIO_ID")));

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return lista;
    }

    // ============================
    //   LISTAR POR DATA + USUÁRIO
    // ============================
    public List<Evento> listarPorDataEUsuario(String data, int usuarioId) {
        List<Evento> lista = new ArrayList<>();
        String sql = """
            SELECT * FROM EVENTOS
            WHERE DATA = ? AND USUARIO_ID = ?
            ORDER BY HORA ASC
        """;

        try (Connection c = getConn();
             PreparedStatement stmt = c.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(data));
            stmt.setInt(2, usuarioId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next())
                lista.add(map(rs, usuarioId));

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return lista;
    }

    // ============================
    //   MAP FINAL — SEM FALHAS
    // ============================
    private Evento map(ResultSet rs, int usuarioId) throws SQLException {
        Evento e = new Evento();
        e.setId(rs.getInt("ID"));
        e.setTitulo(rs.getString("TITULO"));
        e.setDescricao(rs.getString("DESCRICAO"));
        e.setData(rs.getDate("DATA").toLocalDate());
        Time t = rs.getTime("HORA");
        e.setHora(t != null ? t.toLocalTime() : null);
        Time tFim = rs.getTime("HORA_FIM");
        e.setHoraFim(tFim != null ? tFim.toLocalTime() : null);
        e.setLocal(rs.getString("LOCAL"));
        e.setAlerta(rs.getString("ALERTA"));
        e.setCor(rs.getString("COR"));
        e.setPrioridade(rs.getString("PRIORIDADE"));
        e.setRepeticao(rs.getString("REPETICAO"));
        e.setLembreteEnviado(rs.getBoolean("LEMBRETE_ENVIADO"));
        e.setUsuarioId(rs.getInt("USUARIO_ID"));

        return e;
    }
}
