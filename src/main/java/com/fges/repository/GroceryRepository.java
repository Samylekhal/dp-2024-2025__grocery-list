package com.fges.repository;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Repository pour accéder aux données (remplace StorageInterface)
 */
public interface GroceryRepository {
    // Commandes
    void addItem(String itemName, int quantity, String category) throws IOException;
    void removeItem(String itemName) throws IOException;
    void deleteFile() throws IOException;

    // Requêtes
    Map<String, List<String>> getAllItems() throws IOException;
    boolean fileExists() throws IOException;
}