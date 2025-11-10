package com.projetoa2.model;

public class Usuario {
    private int id;
    private String nome;
    private String senha;
    private boolean administrador;

    public Usuario(int id, String nome, String senha, boolean administrador) {
        this.id = id;
        this.nome = nome;
        this.senha = senha;
        this.administrador = administrador;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getSenha() { return senha; }
    public boolean ehAdministrador() { return administrador; }

    public String serializar() {
        return id + "|" + nome + "|" + senha + "|" + (administrador?"1":"0");
    }

    public static Usuario desserializar(String line) {
        if (line == null || line.isBlank()) return null;
        String[] parts = line.split("\\|");
        try {
            int id = Integer.parseInt(parts[0]);
            String nome = parts[1];
            String senha = parts[2];
            boolean administrador = parts.length>3 && parts[3].equals("1");
            return new Usuario(id, nome, senha, administrador);
        } catch (Exception e) {
            return null;
        }
    }
}
