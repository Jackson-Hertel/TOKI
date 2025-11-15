package br.com.toki.dao;

import br.com.toki.model.Evento;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventoDAO {

    private Connection connection;

    public EventoDAO() {
        this.connection = new Conexao().getConnection();
    }

    // Adiciona um evento com todos os campos
    public void adicionarEvento(Evento evento) {
        String sql = "INSERT INTO eventos (titulo, descricao, data, prioridade, repeticao, lembreteEnviado) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, evento.getTitulo());
            stmt.setString(2, evento.getDescricao());
            stmt.setDate(3, Date.valueOf(evento.getData()));
            stmt.setInt(4, evento.getPrioridade());
            stmt.setString(5, evento.getRepeticao());
            stmt.setBoolean(6, evento.isLembreteEnviado());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Lista todos os eventos
    public List<Evento> listarTodos() {
        List<Evento> eventos = new ArrayList<>();
        String sql = "SELECT * FROM eventos";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Evento e = new Evento();
                e.setId(rs.getInt("id"));
                e.setTitulo(rs.getString("titulo"));
                e.setDescricao(rs.getString("descricao"));
                e.setData(rs.getDate("data").toLocalDate());
                e.setPrioridade(rs.getInt("prioridade"));
                e.setRepeticao(rs.getString("repeticao"));
                e.setLembreteEnviado(rs.getBoolean("lembreteEnviado"));
                eventos.add(e);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return eventos;
    }

    // Lista eventos por data
    public List<Evento> listarPorData(String data) {
        List<Evento> eventos = new ArrayList<>();
        String sql = "SELECT * FROM eventos WHERE data = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(data));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Evento e = new Evento();
                e.setId(rs.getInt("id"));
                e.setTitulo(rs.getString("titulo"));
                e.setDescricao(rs.getString("descricao"));
                e.setData(rs.getDate("data").toLocalDate());
                e.setPrioridade(rs.getInt("prioridade"));
                e.setRepeticao(rs.getString("repeticao"));
                e.setLembreteEnviado(rs.getBoolean("lembreteEnviado"));
                eventos.add(e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return eventos;
    }

    // Atualiza campo lembreteEnviado
    public void atualizarLembreteEnviado(Evento evento) {
        String sql = "UPDATE eventos SET lembreteEnviado = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, evento.isLembreteEnviado());
            stmt.setInt(2, evento.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Atualiza evento completo
    public void atualizarEvento(Evento e) {
        String sql = "UPDATE eventos SET titulo=?, descricao=?, data=?, prioridade=?, repeticao=? WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, e.getTitulo());
            stmt.setString(2, e.getDescricao());
            stmt.setDate(3, Date.valueOf(e.getData()));
            stmt.setInt(4, e.getPrioridade());
            stmt.setString(5, e.getRepeticao());
            stmt.setInt(6, e.getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Deleta evento
    public void deletarEvento(int id) {
        String sql = "DELETE FROM eventos WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

}
