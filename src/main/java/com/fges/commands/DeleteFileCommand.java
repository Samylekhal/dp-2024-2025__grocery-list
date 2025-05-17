package com.fges.commands;

import com.fges.core.Command;

/**
 * Commande pour supprimer le fichier
 */
public class DeleteFileCommand implements Command<DeleteFileCommand.Payload> {
    @Override
    public Payload getPayload() {
        return new Payload();
    }

    public static class Payload {
        // Pas de données spécifiques pour cette commande
    }
}