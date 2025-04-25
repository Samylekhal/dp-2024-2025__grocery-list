package com.fges;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GroceryService {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final String fileName;
    private final String format;
    private final String category;

    public GroceryService(String fileName, String format, String category) {
        this.fileName = fileName;
        this.format = format.toLowerCase();
        this.category = category.toLowerCase();
    }

    public List<String> getItems() throws IOException {
        Path filePath = Paths.get(fileName);

        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }

        if ("csv".equals(format)) {
            return Files.readAllLines(filePath);
        } else {
            // Default to JSON
            String fileContent = Files.readString(filePath);
            var categoryMap = OBJECT_MAPPER.readValue(fileContent, new TypeReference<java.util.Map<String, List<String>>>() {});
            
            // Return items from the default category or an empty list if it doesn't exist
            return categoryMap.getOrDefault(category, new ArrayList<>());
        }
    }

    public void saveItems(List<String> items) throws IOException {
        Path filePath = Paths.get(fileName);

        if ("csv".equals(format)) {
            Files.write(filePath, items);
        } else {
            // Default to JSON
            var outputFile = new File(fileName);
            OBJECT_MAPPER.writeValue(outputFile, items);
        }
    }

    public int executeCommand(String command, List<String> args) throws IOException {

        switch (command) {
            case "add" -> {
                // Vérification des arguments : on ne considère que l'article et la quantité.
                if (args.size() < 2) {
                    System.err.println("Missing arguments. Usage: add <item_name> <quantity>");
                    return 1;
                }
                String itemName = args.get(0);
                int quantity;
                try {
                    quantity = Integer.parseInt(args.get(1));
                } catch (NumberFormatException e) {
                    System.err.println("Quantity must be a number");
                    return 1;
                }
                // IMPORTANT : la catégorie n'est plus extraite des arguments positionnels,
                // elle est uniquement fournie via l'option -c.
                String currentCategory = this.category; 

                if ("csv".equals(format)) {
                    Path filePath = Paths.get(fileName);
                    List<String> lines = new ArrayList<>();
                    // S'il n'existe pas, on initialise le fichier avec un en-tête général
                    if (!Files.exists(filePath)) {
                        lines.add("article,nombre,categorie");
                    } else {
                        lines = Files.readAllLines(filePath);
                    }
                    // On ajoute la nouvelle ligne avec la catégorie provenant de -c (ou "default" par défaut)
                    lines.add(itemName + "," + quantity + "," + currentCategory);
                    Files.write(filePath, lines);
                } else {
                    // JSON : on lit le fichier en tant que Map (catégorie -> liste d'articles)
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
                    // Ajoute l'article dans la catégorie spécifiée par -c
                    categoryMap.putIfAbsent(currentCategory, new ArrayList<>());
                    categoryMap.get(currentCategory).add(itemName + ", " + quantity);
                    // Sauvegarde la map complète dans le fichier
                    OBJECT_MAPPER.writeValue(new File(fileName), categoryMap);
                }
                return 0;
            }
            
            case "list" -> {
                if ("csv".equals(format)) {
                    // Pour CSV : on lit toutes les lignes, on ignore la première ligne (l'en-tête)
                    // et on regroupe les articles par catégorie.
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
                } else {
                    // Pour JSON : on lit le fichier sous forme de Map et on affiche pour chaque catégorie
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
                }
                return 0;
            }

            case "remove" -> {
                if (args.isEmpty()) {
                    System.err.println("Missing arguments. Usage: remove <item_name>");
                    return 1;
                }
                String itemName = args.get(0).toLowerCase();
            
                if ("csv".equals(format)) {
                    // Pour CSV, on lit le fichier en gardant la ligne d'en-tête intacte
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
                } else {
                    // Pour JSON, le fichier est une map (catégorie -> liste d'articles)
                    Path filePath = Paths.get(fileName);
                    if (!Files.exists(filePath)) {
                        System.err.println("File not found: " + fileName);
                        System.out.println("test 2");
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
                                // On attend un format "article: quantity"
                                String[] parts = item.split(",");
                                if (parts.length > 0) {
                                    System.out.println("test 5");
                                    String article = parts[0].trim().toLowerCase();
                                    return !article.equals(itemName);
                                }
                                return true;
                            })
                            .collect(Collectors.toList());
                        // On met à jour la liste pour la catégorie, même si elle devient vide
                        entry.setValue(updatedList);
                        System.out.println(updatedList);
                    }
                    // Sauvegarde de la map mise à jour
                    OBJECT_MAPPER.writeValue(new File(fileName), categoryMap);
                }
                return 0;
            }

            case "delete" -> {
                if (!format.equals("json") && !format.equals("csv")) {
                    System.err.println("Delete command only supports json and csv formats.");
                    return 1;
                }
            
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

            case "info" -> {
                // Récupération et affichage de la date actuelle
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String currentDate = dateFormat.format(new Date());
                System.out.println("Date: " + currentDate);
                
                // Récupération et affichage du système d'exploitation
                String osName = System.getProperty("os.name");
                System.out.println("Operating System: " + osName);
                
                // Récupération et affichage de la version de Java
                String javaVersion = System.getProperty("java.version");
                System.out.println("Java Version: " + javaVersion);
                
                return 0;
            }

            default -> {
                System.err.println("Unknown command: " + command);
                return 1;
            }
        }
    }
}