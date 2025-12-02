package br.com.toki.servlet;

import br.com.toki.service.EventoService;
import br.com.toki.model.Evento;
import br.com.toki.model.Usuario;
import com.google.gson.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@WebServlet("/toki/evento/*")
public class EventoServlet extends HttpServlet {

    private final EventoService service = new EventoService();

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, type, ctx) -> LocalDate.parse(json.getAsString()))
            .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, type, ctx) -> new JsonPrimitive(src.toString()))
            .registerTypeAdapter(LocalTime.class, (JsonDeserializer<LocalTime>) (json, type, ctx) -> {
                String s = json.getAsString();
                return (s == null || s.isEmpty()) ? null : LocalTime.parse(s);
            })
            .registerTypeAdapter(LocalTime.class, (JsonSerializer<LocalTime>) (src, type, ctx) -> new JsonPrimitive(src == null ? "" : src.toString()))
            .create();

    private void habilitarCors(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:5500");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        habilitarCors(resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        habilitarCors(resp);
        resp.setContentType("application/json;charset=UTF-8");

        Usuario user = getUsuarioLogado(req);
        if(user == null){ unauthorized(resp); return; }

        String path = req.getPathInfo();
        String data = req.getParameter("data");

        try {
            if(path == null || path.equals("/")) {
                List<Evento> eventos = (data != null && !data.isEmpty())
                        ? service.listarPorDataEUsuario(data, user.getId())
                        : service.listarPorUsuario(user.getId());
                resp.getWriter().write(gson.toJson(eventos));
            } else {
                int id = Integer.parseInt(path.substring(1));
                Evento e = service.buscarPorId(id, user.getId());
                if(e == null){
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("{\"erro\":\"Evento não encontrado\"}");
                    return;
                }
                resp.getWriter().write(gson.toJson(e));
            }
        } catch (Exception ex){
            ex.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"erro\":\"Erro ao buscar eventos\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        habilitarCors(resp);
        resp.setContentType("application/json;charset=UTF-8");

        Usuario user = getUsuarioLogado(req);
        if(user == null){ unauthorized(resp); return; }

        Evento novo = gson.fromJson(req.getReader(), Evento.class);
        novo.setId(0);

        preencherCamposOpcionais(novo);

        if(!validarEvento(novo)){
            badRequest(resp,"Dados inválidos");
            return;
        }

        service.adicionarEvento(novo, user.getId());
        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.getWriter().write("{\"mensagem\":\"Evento criado\"}");
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        habilitarCors(resp);
        resp.setContentType("application/json;charset=UTF-8");

        Usuario user = getUsuarioLogado(req);
        if(user == null){ unauthorized(resp); return; }

        String path = req.getPathInfo();
        if(path == null || path.length() <= 1){ badRequest(resp,"ID é obrigatório"); return; }

        int id = Integer.parseInt(path.substring(1));
        Evento e = gson.fromJson(req.getReader(), Evento.class);
        e.setId(id);

        preencherCamposOpcionais(e);
        service.atualizarEvento(e, user.getId());

        resp.getWriter().write("{\"mensagem\":\"Evento atualizado\"}");
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        habilitarCors(resp);
        resp.setContentType("application/json;charset=UTF-8");

        Usuario user = getUsuarioLogado(req);
        if(user == null){ unauthorized(resp); return; }

        String path = req.getPathInfo();
        if(path == null || path.length() <= 1){ badRequest(resp,"ID é obrigatório"); return; }

        int id = Integer.parseInt(path.substring(1));
        service.deletarEvento(id, user.getId());

        resp.getWriter().write("{\"mensagem\":\"Evento deletado\"}");
    }

    private Usuario getUsuarioLogado(HttpServletRequest req){
        HttpSession session = req.getSession(false);
        return session != null ? (Usuario) session.getAttribute("usuarioLogado") : null;
    }

    private void unauthorized(HttpServletResponse resp) throws IOException{
        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        resp.getWriter().write("{\"erro\":\"Usuário não logado\"}");
    }

    private void badRequest(HttpServletResponse resp, String msg) throws IOException{
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        resp.getWriter().write("{\"erro\":\""+msg+"\"}");
    }

    private boolean validarEvento(Evento e){
        if(e.getTitulo() == null || e.getTitulo().isBlank()) return false;
        if(e.getDescricao() == null || e.getDescricao().isBlank()) return false;
        if(e.getData() == null) return false;
        if(e.getPrioridade() == null ||
                !(e.getPrioridade().equals("baixa") || e.getPrioridade().equals("media") || e.getPrioridade().equals("alta"))){
            e.setPrioridade("baixa");
        }
        if(e.getCor() == null) e.setCor("#4285F4");
        if(e.getHora() == null) e.setHora(LocalTime.of(0,0));
        if(e.getHoraFim() == null) e.setHoraFim(LocalTime.of(0,0));
        return true;
    }

    private void preencherCamposOpcionais(Evento e){
        if(e.getHora() == null) e.setHora(LocalTime.of(0,0));
        if(e.getHoraFim() == null) e.setHoraFim(LocalTime.of(0,0));
        if(e.getLocal() == null) e.setLocal("");
        if(e.getAlerta() == null) e.setAlerta("none");
        if(e.getCor() == null) e.setCor("#4285F4");
        if(e.getPrioridade() == null) e.setPrioridade("baixa");
        if(e.getRepeticao() == null) e.setRepeticao("none");
    }
}
