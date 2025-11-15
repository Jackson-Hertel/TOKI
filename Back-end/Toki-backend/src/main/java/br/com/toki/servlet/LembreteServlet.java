package br.com.toki.servlet;

import br.com.toki.dao.EventoDAO;
import br.com.toki.model.Evento;
import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/toki/lembretes")
public class LembreteServlet extends HttpServlet {

    private final EventoDAO dao = new EventoDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        resp.setContentType("application/json;charset=UTF-8");

        LocalDate hoje = LocalDate.now();
        List<Evento> eventos = dao.listarTodos();

        // Filtra os eventos que devem ser disparados hoje
        List<Evento> lembretesHoje = eventos.stream()
                .filter(e -> deveDisparar(e, hoje))
                .collect(Collectors.toList());

        String json = gson.toJson(lembretesHoje);
        resp.getWriter().write(json);
    }

    private boolean deveDisparar(Evento e, LocalDate hoje) {
        if (e.isLembreteEnviado()) return false;

        switch (e.getRepeticao() != null ? e.getRepeticao() : "") {
            case "diario": return true;
            case "semanal": return hoje.getDayOfWeek() == e.getData().getDayOfWeek();
            case "mensal": return hoje.getDayOfMonth() == e.getData().getDayOfMonth();
            default: return e.getData().isEqual(hoje);
        }
    }
}
