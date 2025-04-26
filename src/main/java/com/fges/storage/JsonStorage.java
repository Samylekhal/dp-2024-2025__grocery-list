package com.fges.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonStorage implements StorageInterface {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final String fileName;

    public JsonStorage(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public int addItem(String itemName, int quantity, String category) throws IOException {
        Path filePath = Paths.get(fileName);
        Map<String, List<String>> categoryMap = new HashMap<>();
        
        if (Files.exists(filePath)) {
            try {
                String fileContent = Files.readString(filePath);
                categoryMap = OBJECT_MAPPER.readValue(fileContent, new TypeReference<>() {});
            } catch(Exception e) {
                // En cas d'erreur de parsing, on réinitialise la map
                categoryMap = new HashMap<>();
            }
        }
        
        // Ajoute l'article dans la catégorie spécifiée
        categoryMap.putIfAbsent(category, new ArrayList<>());
        categoryMap.get(category).add(itemName + ", " + quantity);
        
        // Sauvegarde la map complète dans le fichier
        OBJECT_MAPPER.writeValue(new File(fileName), categoryMap);
        return 0;
    }

    @Override
    public int listItems() throws IOException {
        Path filePath = Paths.get(fileName);
        if (!Files.exists(filePath)) {
            System.out.println("No items found.");
            return 0;
        }
        
        Map<String, List<String>> categoryMap;
        try {
            String fileContent = Files.readString(filePath);
            categoryMap = OBJECT_MAPPER.readValue(fileContent, new TypeReference<Map<String, List<String>>>() {});
        } catch(Exception e) {
            System.err.println("Error reading JSON: " + e.getMessage());
            return 1;
        }
        
        for (var entry : categoryMap.entrySet()) {
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
        
        Map<String, List<String>> categoryMap;
        try {
            String fileContent = Files.readString(filePath);
            categoryMap = OBJECT_MAPPER.readValue(fileContent, new TypeReference<Map<String, List<String>>>() {});
        } catch (Exception e) {
            System.err.println("Error reading JSON: " + e.getMessage());
            return 1;
        }
        
        // Pour chaque catégorie, on filtre les articles dont le nom correspond exactement à itemName
        for (Map.Entry<String, List<String>> entry : categoryMap.entrySet()) {
            List<String> updatedList = entry.getValue().stream()
                .filter(item -> {
                    // On attend un format "article, quantity"
                    String[] parts = item.split(",");
                    if (parts.length > 0) {
                        String article = parts[0].trim().toLowerCase();
                        return !article.equals(itemName);
                    }
                    return true;
                })
                .collect(Collectors.toList());
            // On met à jour la liste pour la catégorie, même si elle devient vide
            entry.setValue(updatedList);
        }
        
        // Sauvegarde de la map mise à jour
        OBJECT_MAPPER.writeValue(new File(fileName), categoryMap);
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