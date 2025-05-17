package com.fges.repository;

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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Implémentation JSON du repository pour les courses
 */
public class JsonGroceryRepository implements GroceryRepository {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final String fileName;

    public JsonGroceryRepository(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void addItem(String itemName, int quantity, String category) throws IOException {
        Map<String, List<String>> categoryMap = getAllItemsInternal();

        // Ajoute l'article dans la catégorie spécifiée
        categoryMap.putIfAbsent(category, new ArrayList<>());
        categoryMap.get(category).add(itemName + ", " + quantity);

        // Sauvegarde la map complète dans le fichier
        OBJECT_MAPPER.writeValue(new File(fileName), categoryMap);
    }

    @Override
    public void removeItem(String itemName) throws IOException {
        Map<String, List<String>> categoryMap = getAllItemsInternal();

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
    }

    @Override
    public void deleteFile() throws IOException {
        Path filePath = Paths.get(fileName);
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        } else {
            throw new IOException("File not found: " + fileName);
        }
    }

    @Override
    public Map<String, List<String>> getAllItems() throws IOException {
        return getAllItemsInternal();
    }

    @Override
    public boolean fileExists() throws IOException {
        return Files.exists(Paths.get(fileName));
    }

    private Map<String, List<String>> getAllItemsInternal() throws IOException {
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

        return categoryMap;
    }
}