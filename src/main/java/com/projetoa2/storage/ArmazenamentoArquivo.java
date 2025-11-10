package com.projetoa2.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

// Singleton responsável por leitura/gravação de arquivos
public class ArmazenamentoArquivo {
    private static ArmazenamentoArquivo instancia;

    private ArmazenamentoArquivo() {}

    public static synchronized ArmazenamentoArquivo getInstancia() {
        if (instancia == null) instancia = new ArmazenamentoArquivo();
        return instancia;
    }

    public synchronized List<String> lerTudo(String caminho) {
        try {
            Path p = Paths.get(caminho);
            if (!Files.exists(p)) {
                Files.createDirectories(p.getParent());
                Files.createFile(p);
                return new ArrayList<>();
            }
            return Files.readAllLines(p);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public synchronized void escreverTudo(String caminho, List<String> linhas) {
        try {
            Path p = Paths.get(caminho);
            if (!Files.exists(p)) {
                Files.createDirectories(p.getParent());
                Files.createFile(p);
            }
            Files.write(p, linhas, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void anexar(String caminho, String linha) {
        try {
            Path p = Paths.get(caminho);
            if (!Files.exists(p)) {
                Files.createDirectories(p.getParent());
                Files.createFile(p);
            }
            Files.write(p, (linha+System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
