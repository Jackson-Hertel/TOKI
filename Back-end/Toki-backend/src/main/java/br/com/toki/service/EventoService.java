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

    public void atualizarEvento(Evento e) {
        dao.atualizarEvento(e);
    }

    public void deletarEvento(int id) {
        dao.deletarEvento(id);
    }
}
