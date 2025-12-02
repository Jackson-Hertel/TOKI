package br.com.toki.service;

import br.com.toki.dao.EventoDAO;
import br.com.toki.model.Evento;

import java.util.List;

public class EventoService {

    private final EventoDAO dao = new EventoDAO();

    public void adicionarEvento(Evento e, int usuarioId) {
        dao.adicionarEvento(e, usuarioId);
    }

    public List<Evento> listarPorDataEUsuario(String data, int usuarioId) {
        return dao.listarPorDataEUsuario(data, usuarioId);
    }

    public List<Evento> listarPorUsuario(int usuarioId) {
        return dao.listarPorUsuario(usuarioId);
    }

    public List<Evento> listarTodos() {
        return dao.listarTodos();
    }

    // ========================
    // Atualizar evento
    // ========================
    public void atualizarEvento(Evento e, int usuarioId) {
        // Garante que o evento pertence ao usuário antes de atualizar
        List<Evento> eventosDoUsuario = dao.listarPorUsuario(usuarioId);
        boolean existe = eventosDoUsuario.stream().anyMatch(ev -> ev.getId() == e.getId());
        if (existe) {
            dao.atualizarEvento(e);
        } else {
            throw new RuntimeException("Evento não encontrado ou não pertence ao usuário");
        }
    }

    // ========================
    // Deletar evento
    // ========================
    public void deletarEvento(int id, int usuarioId) {
        // Garante que o evento pertence ao usuário antes de deletar
        List<Evento> eventosDoUsuario = dao.listarPorUsuario(usuarioId);
        boolean existe = eventosDoUsuario.stream().anyMatch(ev -> ev.getId() == id);
        if (existe) {
            dao.deletarEvento(id);
        } else {
            throw new RuntimeException("Evento não encontrado ou não pertence ao usuário");
        }
    }

    // ========================
    // Buscar evento por ID e usuário
    // ========================
    public Evento buscarPorId(int id, int usuarioId) {
        List<Evento> eventosDoUsuario = dao.listarPorUsuario(usuarioId);
        return eventosDoUsuario.stream().filter(ev -> ev.getId() == id).findFirst().orElse(null);
    }
}
