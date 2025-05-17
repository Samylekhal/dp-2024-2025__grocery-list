package com.fges.handlers;

import com.fges.commands.DeleteFileCommand;
import com.fges.core.CommandHandler;
import com.fges.repository.GroceryRepository;

import java.io.IOException;

/**
 * Gestionnaire pour la commande de suppression du fichier
 */
public class DeleteFileCommandHandler implements CommandHandler<DeleteFileCommand, DeleteFileCommand.Payload> {
    private final GroceryRepository repository;

    public DeleteFileCommandHandler(GroceryRepository repository) {
        this.repository = repository;
    }

    @Override
    public int handle(DeleteFileCommand command) throws IOException {
        try {
            repository.deleteFile();
            System.out.println("File deleted successfully.");
            return 0;
        } catch (Exception e) {
            System.err.println("Error deleting file: " + e.getMessage());
            return 1;
        }
    }
}
