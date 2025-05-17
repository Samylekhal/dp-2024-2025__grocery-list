package com.fges.queries;

import com.fges.core.Query;

/**
 * Requête pour lister tous les articles
 */
public class ListItemsQuery implements Query<ListItemsQuery.Parameters> {
    @Override
    public Parameters getParameters() {
        return new Parameters();
    }

    public static class Parameters {
        // Pas de paramètres spécifiques pour cette requête
    }
}