package com.fges.repository;

/**
 * Factory pour créer le repository approprié selon le format
 */
public class GroceryRepositoryFactory {
    public static GroceryRepository createRepository(String fileName, String format) {
        if ("csv".equalsIgnoreCase(format)) {
            return new CsvGroceryRepository(fileName);
        } else {
            return new JsonGroceryRepository(fileName);
        }
    }
}