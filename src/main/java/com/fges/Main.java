package com.fges;

import fr.anthonyquere.GroceryShopServer;
import fr.anthonyquere.MyGroceryShop;

import com.fges.commands.AddItemCommand;
import com.fges.commands.DeleteFileCommand;
import com.fges.commands.RemoveItemCommand;
import com.fges.core.CommandBus;
import com.fges.core.EventBus;
import com.fges.core.QueryBus;
import com.fges.handlers.AddItemCommandHandler;
import com.fges.handlers.DeleteFileCommandHandler;
import com.fges.handlers.InfoQueryHandler;
import com.fges.handlers.ListItemsQueryHandler;
import com.fges.handlers.RemoveItemCommandHandler;
import com.fges.logger.LoggingEventSubscriber;
import com.fges.queries.InfoQuery;
import com.fges.queries.ListItemsQuery;
import com.fges.repository.GroceryRepository;
import com.fges.repository.GroceryRepositoryFactory;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        MyGroceryShop groceryShop = new SimpleGroceryShop();
        GroceryShopServer server = new GroceryShopServer(groceryShop);
        server.start(8080);

        //System.exit(exec(args));

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

    public static int exec(String[] args) throws IOException {
        // Initialiser le système
        setupSystem();

        // Parse options
        MyOptions options = new MyOptions();
        if (!options.parse(args)) {
            return 1;
        }

        try {
            return executeCommand(options);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return 1;
        }
    }

    private static void setupSystem() {
        // Enregistrer les abonnés aux événements
        EventBus.subscribe("AddItemEvent", new LoggingEventSubscriber());
        EventBus.subscribe("RemoveItemEvent", new LoggingEventSubscriber());
        EventBus.subscribe("DeleteFileEvent", new LoggingEventSubscriber());

        // Enregistrer les gestionnaires de requêtes
        QueryBus.register(InfoQuery.class, new InfoQueryHandler());
    }

    private static int executeCommand(MyOptions options) throws Exception {
        String command = options.getCommand();
        List<String> commandArgs = options.getCommandArgs();

        // Créer le repository approprié
        GroceryRepository repository = GroceryRepositoryFactory.createRepository(
                options.getSourceFile(),
                options.getFormat()
        );

        // Enregistrer les gestionnaires qui ont besoin du repository
        CommandBus.register(AddItemCommand.class, new AddItemCommandHandler(repository));
        CommandBus.register(RemoveItemCommand.class, new RemoveItemCommandHandler(repository));
        CommandBus.register(DeleteFileCommand.class, new DeleteFileCommandHandler(repository));
        QueryBus.register(ListItemsQuery.class, new ListItemsQueryHandler(repository));

        // Exécuter la commande ou la requête appropriée
        switch (command.toLowerCase()) {
            case "add":
                // Vérification des arguments
                if (commandArgs.size() < 2) {
                    System.err.println("Missing arguments. Usage: add <item_name> <quantity>");
                    return 1;
                }

                String itemName = commandArgs.get(0);
                int quantity;
                try {
                    quantity = Integer.parseInt(commandArgs.get(1));
                } catch (NumberFormatException e) {
                    System.err.println("Quantity must be a number");
                    return 1;
                }

                return CommandBus.dispatch(
                        new AddItemCommand(itemName, quantity, options.getCategory())
                );

            case "remove":
                if (commandArgs.isEmpty()) {
                    System.err.println("Missing arguments. Usage: remove <item_name>");
                    return 1;
                }

                return CommandBus.dispatch(
                        new RemoveItemCommand(commandArgs.get(0).toLowerCase())
                );

            case "list":
                return QueryBus.dispatch(new ListItemsQuery());

            case "delete":
                return CommandBus.dispatch(new DeleteFileCommand());

            case "info":
                return QueryBus.dispatch(new InfoQuery());

            default:
                System.err.println("Unknown command: " + command);
                return 1;
        }
    }
}