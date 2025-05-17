package com.fges.handlers;

import com.fges.core.QueryHandler;
import com.fges.queries.InfoQuery;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Gestionnaire pour la requête d'information système
 */
public class InfoQueryHandler implements QueryHandler<InfoQuery, InfoQuery.Parameters, Integer> {

    @Override
    public Integer handle(InfoQuery query) {
        try {
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
        } catch (Exception e) {
            System.err.println("Error displaying info: " + e.getMessage());
            return 1;
        }
    }
}