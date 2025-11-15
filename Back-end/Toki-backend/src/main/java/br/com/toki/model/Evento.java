package br.com.toki.model;

import java.time.LocalDate;

public class Evento {
    private int id;
    private String titulo;
    private String descricao;
    private LocalDate data;
    private int prioridade; // 1=Normal, 2=Alta, 3=Urgente
    private String repeticao; // "diario", "semanal", "mensal" ou null
    private boolean lembreteEnviado; // para controle de envio

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public int getPrioridade() { return prioridade; }
    public void setPrioridade(int prioridade) { this.prioridade = prioridade; }

    public String getRepeticao() { return repeticao; }
    public void setRepeticao(String repeticao) { this.repeticao = repeticao; }

    public boolean isLembreteEnviado() { return lembreteEnviado; }
    public void setLembreteEnviado(boolean lembreteEnviado) { this.lembreteEnviado = lembreteEnviado; }
}

