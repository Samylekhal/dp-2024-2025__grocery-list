package com.fges.queries;

import com.fges.core.Query;

/**
 * Requête pour afficher les informations du système
 */
public class InfoQuery implements Query<InfoQuery.Parameters> {
    @Override
    public Parameters getParameters() {
        return new Parameters();
    }

    public static class Parameters {
        // Pas de paramètres spécifiques pour cette requête
    }
}