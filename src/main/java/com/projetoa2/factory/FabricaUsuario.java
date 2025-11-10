package com.projetoa2.factory;

import com.projetoa2.model.Usuario;
import com.projetoa2.storage.ArmazenamentoArquivo;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FabricaUsuario {
    private static final AtomicInteger CONTADOR = new AtomicInteger(initializeCounter());

    private static int initializeCounter() {
        try {
            List<String> lines = ArmazenamentoArquivo.getInstancia().lerTudo("data/users.txt");
            int max = 0;
            for (String l : lines) {
                if (l == null || l.isBlank()) continue;
                String[] parts = l.split("\\|");
                try { int id = Integer.parseInt(parts[0]); if (id > max) max = id; } catch (Exception ignored) {}
            }
            return max + 1;
        } catch (Exception e) {
            return 1;
        }
    }

    public static Usuario criar(String nome, String senha) {
        int id = CONTADOR.getAndIncrement();
        return new Usuario(id, nome, senha, nome.equals("admin"));
    }
}
