package com.fges.handlers;

import com.fges.commands.RemoveItemCommand;
import com.fges.core.CommandHandler;
import com.fges.repository.GroceryRepository;


import java.io.IOException;

/**
 * Gestionnaire pour la commande de suppression d'article
 */
public class RemoveItemCommandHandler implements CommandHandler<RemoveItemCommand, RemoveItemCommand.Payload> {
    private final GroceryRepository repository;

    public RemoveItemCommandHandler(GroceryRepository repository) {
        this.repository = repository;
    }

    @Override
    public int handle(RemoveItemCommand command) throws IOException {
        try {
            repository.removeItem(command.getItemName());
            return 0;
        } catch (Exception e) {
            System.err.println("Error removing item: " + e.getMessage());
            return 1;
        }
    }
}