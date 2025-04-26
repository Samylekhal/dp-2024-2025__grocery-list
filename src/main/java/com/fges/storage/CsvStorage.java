
package com.fges.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CsvStorage implements StorageInterface {
    private final String fileName;

    public CsvStorage(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public int addItem(String itemName, int quantity, String category) throws IOException {
        Path filePath = Paths.get(fileName);
        List<String> lines = new ArrayList<>();
        
        // S'il n'existe pas, on initialise le fichier avec un en-tête général
        if (!Files.exists(filePath)) {
            lines.add("article,nombre,categorie");
        } else {
            lines = Files.readAllLines(filePath);
        }
        
        // On ajoute la nouvelle ligne avec la catégorie
        lines.add(itemName + "," + quantity + "," + category);
        Files.write(filePath, lines);
        return 0;
    }

    @Override
    public int listItems() throws IOException {
        Path filePath = Paths.get(fileName);
        if (!Files.exists(filePath)) {
            System.out.println("No items found.");
            return 0;
        }
        
        List<String> lines = Files.readAllLines(filePath);
        if (lines.isEmpty()) {
            System.out.println("No items found.");
            return 0;
        }
        
        // On suppose que la première ligne est l'en-tête
        List<String[]> items = lines.stream()
                .skip(1)
                .map(line -> line.split(","))
                .collect(Collectors.toList());
        
        // Regroupement par catégorie (3ème colonne, trim pour éviter les espaces)
        Map<String, List<String>> grouped = new HashMap<>();
        for (String[] parts : items) {
            if (parts.length < 3) continue;
            String cat = parts[2].trim();
            String articleLine = parts[0].trim() + ", " + parts[1].trim();
            grouped.putIfAbsent(cat, new ArrayList<>());
            grouped.get(cat).add(articleLine);
        }
        
        // Affichage groupé
        for (var entry : grouped.entrySet()) {
            System.out.println("# " + entry.getKey() + ":");
            for (String item : entry.getValue()) {
                System.out.println("  " + item);
            }
        }
        return 0;
    }

    @Override
    public int removeItem(String itemName) throws IOException {
        Path filePath = Paths.get(fileName);
        if (!Files.exists(filePath)) {
            System.err.println("File not found: " + fileName);
            return 1;
        }
        
        List<String> lines = Files.readAllLines(filePath);
        if (lines.isEmpty()) {
            System.err.println("File is empty: " + fileName);
            return 1;
        }
        
        // La première ligne est l'en-tête
        String header = lines.get(0);
        List<String> updatedLines = new ArrayList<>();
        updatedLines.add(header);
        
        // Pour chaque ligne, on supprime uniquement celle dont le premier champ (l'article) correspond à itemName
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] parts = line.split(",");
            if (parts.length > 0) {
                String article = parts[0].trim().toLowerCase();
                if (article.equals(itemName)) {
                    // On ne l'ajoute pas, on la supprime
                    continue;
                }
            }
            updatedLines.add(line);
        }
        
        Files.write(filePath, updatedLines);
        return 0;
    }

    @Override
    public int deleteFile() throws IOException {
        Path filePath = Paths.get(fileName);
        if (Files.exists(filePath)) {
            Files.delete(filePath);
            System.out.println("File deleted: " + fileName);
            return 0;
        } else {
            System.err.println("File not found: " + fileName);
            return 1;
        }
    }
}