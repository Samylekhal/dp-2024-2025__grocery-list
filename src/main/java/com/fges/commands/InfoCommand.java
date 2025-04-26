package com.fges.commands;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class InfoCommand implements CommandInterface {
    @Override
    public int execute(List<String> args) throws IOException {
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
}