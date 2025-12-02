package br.com.toki.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Evento {

    private int id;
    private String titulo;
    private String descricao;
    private LocalDate data;
    private LocalTime hora;        // hora principal do evento
    private LocalTime horaFim;     // hora final do evento
    private String prioridade;
    private String repeticao;      // corresponde a eventRecurrence
    private boolean lembreteEnviado;
    private String alerta;         // novo campo
    private String local;          // novo campo
    private String cor;
    private int usuarioId;

    // ==================================================
    // CONSTRUTOR VAZIO
    // ==================================================
    public Evento() {}

    // ==================================================
    // CONSTRUTOR COMPLETO
    // ==================================================
    public Evento(int id, String titulo, String descricao, LocalDate data, LocalTime hora, LocalTime horaFim,
                  String prioridade, String repeticao, boolean lembreteEnviado, String alerta, String local,
                  String cor, int usuarioId) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.data = data;
        this.hora = hora;
        this.horaFim = horaFim;
        this.prioridade = prioridade;
        this.repeticao = repeticao;
        this.lembreteEnviado = lembreteEnviado;
        this.alerta = alerta;
        this.local = local;
        this.cor = cor;
        this.usuarioId = usuarioId;
    }

    // ======================
    // GETTERS E SETTERS
    // ======================

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }

    public LocalTime getHoraFim() { return horaFim; }
    public void setHoraFim(LocalTime horaFim) { this.horaFim = horaFim; }

    public String getPrioridade() { return prioridade; }
    public void setPrioridade(String prioridade) { this.prioridade = prioridade; }

    public String getRepeticao() { return repeticao; }
    public void setRepeticao(String repeticao) { this.repeticao = repeticao; }

    public boolean isLembreteEnviado() { return lembreteEnviado; }
    public void setLembreteEnviado(boolean lembreteEnviado) { this.lembreteEnviado = lembreteEnviado; }

    public String getAlerta() { return alerta; }
    public void setAlerta(String alerta) { this.alerta = alerta; }

    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }

    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
}
