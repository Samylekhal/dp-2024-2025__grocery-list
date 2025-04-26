package com.fges;

import com.fges.commands.CommandFactory;
import com.fges.commands.CommandInterface;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        System.exit(exec(args));
    }

    public static int exec(String[] args) throws IOException {
        // Parse options
        MyOptions options = new MyOptions();
        if (!options.parse(args)) {
            return 1;
        }

        try {
            // Créer la commande appropriée via la factory
            CommandInterface command = CommandFactory.createCommand(
                    options.getCommand(),
                    options.getSourceFile(),
                    options.getFormat(),
                    options.getCategory()
            );
            
            if (command == null) {
                System.err.println("Unknown command: " + options.getCommand());
                return 1;
            }
            
            // Exécuter la commande
            return command.execute(options.getCommandArgs());

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return 1;
        }
    }
}