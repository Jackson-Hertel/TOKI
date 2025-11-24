package br.com.toki.dao;

import br.com.toki.model.Evento;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventoDAO {

    public EventoDAO() {
        criarTabelaSeNaoExistir();
    }

    private Connection getConn() throws SQLException {
        return new Conexao().getConnection();
    }

    private void criarTabelaSeNaoExistir() {
        String sql = """
        CREATE TABLE IF NOT EXISTS eventos (
            ID INT AUTO_INCREMENT PRIMARY KEY,
            TITULO VARCHAR(255) NOT NULL,
            DESCRICAO VARCHAR(255) NOT NULL,
            DATA DATE NOT NULL,
            HORA TIME,
            COR VARCHAR(20),
            PRIORIDADE VARCHAR(10) NOT NULL,
            REPETICAO VARCHAR(50),
            LEMBRETE_ENVIADO BOOLEAN DEFAULT FALSE,
            USUARIO_ID INT NOT NULL
        )
        """;

        try (Connection c = getConn();
             Statement st = c.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void adicionarEvento(Evento e, int usuarioId) {
        String sql = """
        INSERT INTO eventos
        (TITULO, DESCRICAO, DATA, HORA, COR, PRIORIDADE, REPETICAO, LEMBRETE_ENVIADO, USUARIO_ID)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;

        try (Connection c = getConn();
             PreparedStatement stmt = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, e.getTitulo());
            stmt.setString(2, e.getDescricao());
            stmt.setDate(3, Date.valueOf(e.getData()));
            stmt.setTime(4, e.getHora() != null ? Time.valueOf(e.getHora()) : null);
            stmt.setString(5, e.getCor());
            stmt.setString(6, e.getPrioridade());
            stmt.setString(7, e.getRepeticao());
            stmt.setBoolean(8, e.isLembreteEnviado());
            stmt.setInt(9, usuarioId);

            stmt.executeUpdate();

            // Atualiza o ID do objeto com o ID gerado pelo banco
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if(rs.next()) e.setId(rs.getInt(1));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

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

    public List<Evento> listarPorUsuario(int usuarioId) {
        List<Evento> lista = new ArrayList<>();
        String sql = "SELECT * FROM eventos WHERE USUARIO_ID = ?";

        try (Connection c = getConn();
             PreparedStatement stmt = c.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) lista.add(map(rs));

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return lista;
    }

    public void atualizarEvento(Evento e) {
        String sql = """
            UPDATE eventos SET
            TITULO=?, DESCRICAO=?, DATA=?, HORA=?, COR=?, PRIORIDADE=?, REPETICAO=?
            WHERE ID=?
        """;

        try (Connection c = getConn();
             PreparedStatement stmt = c.prepareStatement(sql)) {

            stmt.setString(1, e.getTitulo());
            stmt.setString(2, e.getDescricao());
            stmt.setDate(3, Date.valueOf(e.getData()));
            stmt.setTime(4, e.getHora() != null ? Time.valueOf(e.getHora()) : null);
            stmt.setString(5, e.getCor());
            stmt.setString(6, e.getPrioridade());
            stmt.setString(7, e.getRepeticao());
            stmt.setInt(8, e.getId());

            stmt.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void deletarEvento(int id) {
        String sql = "DELETE FROM eventos WHERE ID=?";

        try (Connection c = getConn();
             PreparedStatement stmt = c.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public List<Evento> listarPorData(String data) {
        List<Evento> lista = new ArrayList<>();
        String sql = "SELECT * FROM eventos WHERE DATA = ? ORDER BY HORA ASC";

        try (Connection c = getConn();
             PreparedStatement stmt = c.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(data));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) lista.add(map(rs));

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return lista;
    }

    public List<Evento> listarTodos() {
        List<Evento> lista = new ArrayList<>();
        String sql = "SELECT * FROM eventos ORDER BY DATA ASC, HORA ASC";

        try (Connection c = getConn();
             Statement st = c.createStatement()) {

            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) lista.add(map(rs));

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return lista;
    }

    public List<Evento> listarPorDataEUsuario(String data, int usuarioId) {
        List<Evento> lista = new ArrayList<>();
        String sql = """
            SELECT * FROM eventos
            WHERE DATA = ? AND USUARIO_ID = ?
            ORDER BY HORA ASC
        """;

        try (Connection c = getConn();
             PreparedStatement stmt = c.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(data));
            stmt.setInt(2, usuarioId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) lista.add(map(rs));

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return lista;
    }

    private Evento map(ResultSet rs) throws SQLException {
        Evento e = new Evento();
        e.setId(rs.getInt("ID"));
        e.setTitulo(rs.getString("TITULO"));
        e.setDescricao(rs.getString("DESCRICAO"));
        e.setData(rs.getDate("DATA").toLocalDate());

        Time t = rs.getTime("HORA");
        e.setHora(t != null ? t.toLocalTime() : null);

        e.setCor(rs.getString("COR"));
        e.setPrioridade(rs.getString("PRIORIDADE"));
        e.setRepeticao(rs.getString("REPETICAO"));
        e.setLembreteEnviado(rs.getBoolean("LEMBRETE_ENVIADO"));

        return e;
    }
}
