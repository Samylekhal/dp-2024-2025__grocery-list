package com.fges.handlers;

import com.fges.commands.AddItemCommand;
import com.fges.core.CommandHandler;
import com.fges.repository.GroceryRepository;

import java.io.IOException;

/**
 * Gestionnaire pour la commande d'ajout d'article
 */
public class AddItemCommandHandler implements CommandHandler<AddItemCommand, AddItemCommand.Payload> {
    private final GroceryRepository repository;

    public AddItemCommandHandler(GroceryRepository repository) {
        this.repository = repository;
    }

    @Override
    public int handle(AddItemCommand command) throws IOException {
        try {
            repository.addItem(
                    command.getItemName(),
                    command.getQuantity(),
                    command.getCategory()
            );
            return 0;
        } catch (Exception e) {
            System.err.println("Error adding item: " + e.getMessage());
            return 1;
        }
    }
}