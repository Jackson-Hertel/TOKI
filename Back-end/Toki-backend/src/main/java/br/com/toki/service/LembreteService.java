package br.com.toki.service;

import br.com.toki.dao.EventoDAO;
import br.com.toki.model.Evento;

import java.time.LocalDate;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LembreteService {

    private final EventoDAO dao = new EventoDAO();
    private final Timer timer = new Timer(true); // roda em background

    public void iniciar() {
        // Roda a cada minuto para teste (60 * 1000ms). Depois pode ser 24h.
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                verificarEventos();
            }
        }, 0, 60 * 1000);
    }

    private void verificarEventos() {
        List<Evento> eventos = dao.listarTodos();
        LocalDate hoje = LocalDate.now();

        for (Evento e : eventos) {

            if (deveDisparar(e, hoje)) {
                // Dispara notificação
                exibirNotificacao(e);

                // Marca como enviado
                e.setLembreteEnviado(true);
                dao.atualizarLembreteEnviado(e);
            } else {
                // Resetar lembreteEnviado para eventos repetitivos
                if (e.getRepeticao() != null && !e.getRepeticao().isEmpty() && e.isLembreteEnviado()) {
                    boolean resetar = false;

                    switch (e.getRepeticao()) {
                        case "diario":
                            resetar = true; // todo dia pode disparar novamente
                            break;
                        case "semanal":
                            resetar = hoje.getDayOfWeek() != e.getData().getDayOfWeek();
                            break;
                        case "mensal":
                            resetar = hoje.getDayOfMonth() != e.getData().getDayOfMonth();
                            break;
                    }

                    if (resetar) {
                        e.setLembreteEnviado(false);
                        dao.atualizarLembreteEnviado(e);
                    }
                }
            }
        }
    }

    // Lógica para decidir se o lembrete deve ser disparado
    private boolean deveDisparar(Evento e, LocalDate hoje) {
        if (e.isLembreteEnviado()) return false;

        switch (e.getRepeticao() != null ? e.getRepeticao() : "") {
            case "diario":
                return true;
            case "semanal":
                return hoje.getDayOfWeek() == e.getData().getDayOfWeek();
            case "mensal":
                return hoje.getDayOfMonth() == e.getData().getDayOfMonth();
            default:
                return e.getData().isEqual(hoje);
        }
    }

    // Exibe a notificação (console ou JSON)
    private void exibirNotificacao(Evento e) {
        String prioridadeTexto;
        switch (e.getPrioridade()) {
            case 3: prioridadeTexto = "URGENTE"; break;
            case 2: prioridadeTexto = "ALTA"; break;
            default: prioridadeTexto = "NORMAL";
        }

        System.out.println("🔔 Lembrete (" + prioridadeTexto + "): " + e.getTitulo() +
                " - " + e.getDescricao());
    }
}
