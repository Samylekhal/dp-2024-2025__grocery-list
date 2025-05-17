package com.fges.handlers;

import com.fges.core.QueryHandler;
import com.fges.queries.ListItemsQuery;
import com.fges.repository.GroceryRepository;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Gestionnaire pour la requête de liste des articles
 */
public class ListItemsQueryHandler implements QueryHandler<ListItemsQuery, ListItemsQuery.Parameters, Integer> {
    private final GroceryRepository repository;

    public ListItemsQueryHandler(GroceryRepository repository) {
        this.repository = repository;
    }

    @Override
    public Integer handle(ListItemsQuery query) throws IOException {
        try {
            if (!repository.fileExists()) {
                System.out.println("No items found.");
                return 0;
            }

            Map<String, List<String>> items = repository.getAllItems();

            if (items.isEmpty()) {
                System.out.println("No items found.");
                return 0;
            }

            // Affichage groupé par catégorie
            for (var entry : items.entrySet()) {
                System.out.println("# " + entry.getKey() + ":");
                for (String item : entry.getValue()) {
                    System.out.println("  " + item);
                }
            }

            return 0;
        } catch (Exception e) {
            System.err.println("Error listing items: " + e.getMessage());
            return 1;
        }
    }
}