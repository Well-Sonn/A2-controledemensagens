package com.projetoa2.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

// Singleton responsible for file read/write
public class FileStorage {
    private static FileStorage instance;

    private FileStorage() {}

    public static synchronized FileStorage getInstance() {
        if (instance == null) instance = new FileStorage();
        return instance;
    }

    public synchronized List<String> readAll(String filePath) {
        try {
            Path p = Paths.get(filePath);
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

    public synchronized void writeAll(String filePath, List<String> lines) {
        try {
            Path p = Paths.get(filePath);
            if (!Files.exists(p)) {
                Files.createDirectories(p.getParent());
                Files.createFile(p);
            }
            Files.write(p, lines, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void append(String filePath, String line) {
        try {
            Path p = Paths.get(filePath);
            if (!Files.exists(p)) {
                Files.createDirectories(p.getParent());
                Files.createFile(p);
            }
            Files.write(p, (line+System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
