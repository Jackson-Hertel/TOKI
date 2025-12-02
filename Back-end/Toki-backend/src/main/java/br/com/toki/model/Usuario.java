package br.com.toki.model;

public class Usuario {

    private int id;
    private String nome;
    private String email;
    private String senha;
    private String fotoPerfil;
    private String tema;
    private String corPrincipal;
    private String inicioSemana;
    private boolean feriados;
    private boolean aniversarios;
    private boolean concluidos;
    private boolean receberLembretes;
    private String metodoLembrete;
    private String antecedencia;
    private String telefone;

    public Usuario() {}

    // ==================================================
    // CONSTRUTOR COMPLETO
    // ==================================================
    public Usuario(int id, String nome, String email, String senha,
                   String fotoPerfil, String tema, String corPrincipal,
                   String inicioSemana, boolean feriados, boolean aniversarios,
                   boolean concluidos, boolean receberLembretes,
                   String metodoLembrete, String antecedencia, String telefone) {

        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.fotoPerfil = fotoPerfil;
        this.tema = tema;
        this.corPrincipal = corPrincipal;
        this.inicioSemana = inicioSemana;
        this.feriados = feriados;
        this.aniversarios = aniversarios;
        this.concluidos = concluidos;
        this.receberLembretes = receberLembretes;
        this.metodoLembrete = metodoLembrete;
        this.antecedencia = antecedencia;
        this.telefone = telefone;
    }

    // ======================
    // GETTERS E SETTERS
    // ======================

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getFotoPerfil() { return fotoPerfil; }
    public void setFotoPerfil(String fotoPerfil) { this.fotoPerfil = fotoPerfil; }

    public String getTema() { return tema; }
    public void setTema(String tema) { this.tema = tema; }

    public String getCorPrincipal() { return corPrincipal; }
    public void setCorPrincipal(String corPrincipal) { this.corPrincipal = corPrincipal; }

    public String getInicioSemana() { return inicioSemana; }
    public void setInicioSemana(String inicioSemana) { this.inicioSemana = inicioSemana; }

    public boolean isFeriados() { return feriados; }
    public void setFeriados(boolean feriados) { this.feriados = feriados; }

    public boolean isAniversarios() { return aniversarios; }
    public void setAniversarios(boolean aniversarios) { this.aniversarios = aniversarios; }

    public boolean isConcluidos() { return concluidos; }
    public void setConcluidos(boolean concluidos) { this.concluidos = concluidos; }

    public boolean isReceberLembretes() { return receberLembretes; }
    public void setReceberLembretes(boolean receberLembretes) { this.receberLembretes = receberLembretes; }

    public String getMetodoLembrete() { return metodoLembrete; }
    public void setMetodoLembrete(String metodoLembrete) { this.metodoLembrete = metodoLembrete; }

    public String getAntecedencia() { return antecedencia; }
    public void setAntecedencia(String antecedencia) { this.antecedencia = antecedencia; }
}
