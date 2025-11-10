package com.projetoa2.repository.txt;

import com.projetoa2.model.Usuario;
import com.projetoa2.repository.RepositorioUsuario;
import com.projetoa2.storage.ArmazenamentoArquivo;

import java.util.ArrayList;
import java.util.List;

public class RepositorioUsuarioTxt implements RepositorioUsuario {
    private final String path;
    private final ArmazenamentoArquivo armazenamento;

    public RepositorioUsuarioTxt(String path) {
        this.path = path;
        this.armazenamento = ArmazenamentoArquivo.getInstancia();
    }

    @Override
    public List<Usuario> buscarTodos() {
        List<Usuario> out = new ArrayList<>();
        List<String> lines = armazenamento.lerTudo(path);
        for (String l : lines) {
            Usuario u = Usuario.desserializar(l);
            if (u != null) out.add(u);
        }
        return out;
    }

    @Override
    public Usuario buscarPorId(int id) {
        for (Usuario u : buscarTodos()) if (u.getId() == id) return u;
        return null;
    }

    @Override
    public void salvar(Usuario entity) {
        List<Usuario> all = buscarTodos();
        boolean found = false;
        for (int i=0;i<all.size();i++) {
            if (all.get(i).getId() == entity.getId()) { all.set(i, entity); found = true; break; }
        }
        if (!found) all.add(entity);
        List<String> lines = new ArrayList<>();
        for (Usuario u : all) lines.add(u.serializar());
        armazenamento.escreverTudo(path, lines);
    }

    @Override
    public void remover(int id) {
        List<Usuario> all = buscarTodos();
        all.removeIf(u -> u.getId() == id);
        List<String> lines = new ArrayList<>();
        for (Usuario u : all) lines.add(u.serializar());
        armazenamento.escreverTudo(path, lines);
    }

    @Override
    public Usuario buscarPorNome(String nome) {
        for (Usuario u : buscarTodos()) if (u.getNome().equalsIgnoreCase(nome)) return u;
        return null;
    }
}
