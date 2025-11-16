package br.com.toki.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Evento {
    private int id;
    private String titulo;
    private String descricao;
    private LocalDate data;
    private LocalTime hora;          // Adicionado para hora do evento
    private int prioridade;          // 1=Normal, 2=Alta, 3=Urgente
    private String repeticao;        // "diario", "semanal", "mensal" ou null
    private boolean lembreteEnviado; // Controle de envio
    private String cor;              // "blue", "purple", "green", etc.

    // Getters e Setters
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

    public int getPrioridade() { return prioridade; }
    public void setPrioridade(int prioridade) { this.prioridade = prioridade; }

    public String getRepeticao() { return repeticao; }
    public void setRepeticao(String repeticao) { this.repeticao = repeticao; }

    public boolean isLembreteEnviado() { return lembreteEnviado; }
    public void setLembreteEnviado(boolean lembreteEnviado) { this.lembreteEnviado = lembreteEnviado; }

    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }
}
