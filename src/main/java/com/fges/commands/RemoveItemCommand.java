
package com.fges.commands;

import com.fges.core.Command;

/**
 * Commande pour supprimer un article
 */
public class RemoveItemCommand implements Command<RemoveItemCommand.Payload> {
    private final String itemName;

    public RemoveItemCommand(String itemName) {
        this.itemName = itemName;
    }

    @Override
    public Payload getPayload() {
        return new Payload(itemName);
    }

    public String getItemName() {
        return itemName;
    }

    public static class Payload {
        private final String itemName;

        public Payload(String itemName) {
            this.itemName = itemName;
        }

        public String getItemName() {
            return itemName;
        }
    }
}