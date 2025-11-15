package br.com.toki.servlet;

import br.com.toki.dao.EventoDAO;
import br.com.toki.model.Evento;
import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/toki/evento/*")
public class EventoServlet extends HttpServlet {
    private final EventoDAO dao = new EventoDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String dataEvento = req.getParameter("data");

        List<Evento> eventos;
        if (dataEvento != null && !dataEvento.isEmpty()) {
            eventos = dao.listarPorData(dataEvento);
        } else {
            eventos = dao.listarTodos();
        }

        String json = gson.toJson(eventos);
        resp.getWriter().write(json);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        Evento novo = gson.fromJson(req.getReader(), Evento.class);

        if (novo.getTitulo() == null || novo.getTitulo().isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"erro\": \"O título é obrigatório.\"}");
            return;
        }

        if (novo.getDescricao() == null || novo.getDescricao().isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"erro\": \"A descrição é obrigatória.\"}");
            return;
        }

        if (novo.getData() == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"erro\": \"A data do evento é obrigatória.\"}");
            return;
        }

        if (novo.getPrioridade() < 1 || novo.getPrioridade() > 3) {
            novo.setPrioridade(1);
        }

        if (novo.getRepeticao() == null || novo.getRepeticao().isEmpty()) {
            novo.setRepeticao(null);
        }

        novo.setLembreteEnviado(false);

        dao.adicionarEvento(novo);

        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.getWriter().write("{\"mensagem\": \"Evento salvo com sucesso.\"}");
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String pathInfo = req.getPathInfo(); // /{id}
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"erro\": \"ID do evento é obrigatório para edição.\"}");
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            Evento eventoAtualizado = gson.fromJson(req.getReader(), Evento.class);
            eventoAtualizado.setId(id);

            dao.atualizarEvento(eventoAtualizado); // você precisa criar esse método no DAO

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("{\"mensagem\": \"Evento atualizado com sucesso.\"}");
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"erro\": \"ID inválido.\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String pathInfo = req.getPathInfo(); // /{id}
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"erro\": \"ID do evento é obrigatório para exclusão.\"}");
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            dao.deletarEvento(id); // você precisa criar esse método no DAO

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("{\"mensagem\": \"Evento deletado com sucesso.\"}");
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"erro\": \"ID inválido.\"}");
        }
    }
}
