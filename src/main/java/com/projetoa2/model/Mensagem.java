package com.projetoa2.model;

public class Mensagem {
    private int id;
    private int idRemetente;
    private int idDestinatario;
    private String conteudo;
    private long momento;
    private boolean lida;

    public Mensagem(int id, int idRemetente, int idDestinatario, String conteudo, long momento) {
        this.id = id;
        this.idRemetente = idRemetente;
        this.idDestinatario = idDestinatario;
        this.conteudo = conteudo;
        this.momento = momento;
        this.lida = false;
    }

    public int getId() { return id; }
    public int getIdRemetente() { return idRemetente; }
    public int getIdDestinatario() { return idDestinatario; }
    public String getConteudo() { return conteudo; }
    public long getMomento() { return momento; }

    public boolean isLida() { return lida; }
    public void setLida(boolean lida) { this.lida = lida; }

    public String serializar() {
        // formato: id|idRemetente|idDestinatario|conteudo|momento|lida(1/0)
        return id + "|" + idRemetente + "|" + idDestinatario + "|" + conteudo.replace("|"," ") + "|" + momento + "|" + (lida ? "1" : "0");
    }

    public static Mensagem desserializar(String line) {
        if (line == null || line.isBlank()) return null;
        String[] parts = line.split("\\|");
        try {
            int id = Integer.parseInt(parts[0]);
            int s = Integer.parseInt(parts[1]);
            int r = Integer.parseInt(parts[2]);
            String content = parts[3];
            long ts = Long.parseLong(parts[4]);
            Mensagem m = new Mensagem(id, s, r, content, ts);
            // compatibilidade: linhas antigas têm 5 partes (sem flag lida)
            if (parts.length >= 6) {
                String f = parts[5];
                boolean l = !(f.equals("0") || f.equalsIgnoreCase("false"));
                m.setLida(l);
            } else {
                m.setLida(false);
            }
            return m;
        } catch (Exception e) {
            return null;
        }
    }
}
