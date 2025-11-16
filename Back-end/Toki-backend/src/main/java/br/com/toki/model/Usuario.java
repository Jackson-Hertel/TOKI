package br.com.toki.model;

public class Usuario {
    private int id;
    private String nome;
    private String email;
    private String senha;
    private String fotoPerfil;

    // NOVOS CAMPOS
    private String tema;          // ex: "claro" ou "escuro"
    private String corPrincipal;  // ex: "azul", "verde", etc.
    private String inicioSemana;  // ex: "domingo", "segunda"
    private boolean feriados;
    private boolean aniversarios;
    private boolean concluidos;

    // ============================
    // Construtores
    // ============================
    public Usuario() {
    }

    public Usuario(int id, String nome, String email, String senha, String fotoPerfil,
                   String tema, String corPrincipal, String inicioSemana,
                   boolean feriados, boolean aniversarios, boolean concluidos) {
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
    }

    public Usuario(int id, String nome, String email, String senha) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    // ============================
    // Getters e Setters
    // ============================

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

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
}
