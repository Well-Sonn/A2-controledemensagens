package com.projetoa2.repository.txt;

import com.projetoa2.model.Mensagem;
import com.projetoa2.repository.RepositorioMensagem;
import com.projetoa2.storage.ArmazenamentoArquivo;

import java.util.ArrayList;
import java.util.List;

public class RepositorioMensagemTxt implements RepositorioMensagem {
    private final String path;
    private final ArmazenamentoArquivo armazenamento;

    public RepositorioMensagemTxt(String path) {
        this.path = path;
        this.armazenamento = ArmazenamentoArquivo.getInstancia();
    }

    @Override
    public List<Mensagem> buscarTodos() {
        List<Mensagem> out = new ArrayList<>();
        List<String> lines = armazenamento.lerTudo(path);
        for (String l : lines) {
            Mensagem m = Mensagem.desserializar(l);
            if (m != null) out.add(m);
        }
        return out;
    }

    @Override
    public Mensagem buscarPorId(int id) {
        for (Mensagem m : buscarTodos()) if (m.getId() == id) return m;
        return null;
    }

    @Override
    public void salvar(Mensagem entity) {
        List<Mensagem> all = buscarTodos();
        boolean found = false;
        for (int i=0;i<all.size();i++) {
            if (all.get(i).getId() == entity.getId()) { all.set(i, entity); found = true; break; }
        }
        if (!found) all.add(entity);
        List<String> lines = new ArrayList<>();
        for (Mensagem m : all) lines.add(m.serializar());
        armazenamento.escreverTudo(path, lines);
    }

    @Override
    public void remover(int id) {
        List<Mensagem> all = buscarTodos();
        all.removeIf(m -> m.getId() == id);
        List<String> lines = new ArrayList<>();
        for (Mensagem m : all) lines.add(m.serializar());
        armazenamento.escreverTudo(path, lines);
    }

    @Override
    public List<Mensagem> buscarPorDestinatario(int idDestinatario) {
        List<Mensagem> out = new ArrayList<>();
        for (Mensagem m : buscarTodos()) if (m.getIdDestinatario() == idDestinatario) out.add(m);
        return out;
    }
}
