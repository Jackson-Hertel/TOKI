package br.com.toki.service;

import br.com.toki.dao.EventoDAO;
import br.com.toki.model.Evento;
import java.util.List;

public class EventoService {
    private final EventoDAO dao = new EventoDAO();

    public void adicionarEvento(Evento e) {
        dao.adicionarEvento(e);
    }

    public List<Evento> listarPorData(String data) {
        return dao.listarPorData(data);
    }

    public List<Evento> listarTodos() {
        return dao.listarTodos();
    }
}
