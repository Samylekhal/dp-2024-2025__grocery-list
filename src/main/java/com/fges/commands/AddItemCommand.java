package com.fges.commands;

import com.fges.core.Command;
import java.util.List;

/**
 * Commande pour ajouter un article
 */
public class AddItemCommand implements Command<AddItemCommand.Payload> {
    private final String itemName;
    private final int quantity;
    private final String category;

    public AddItemCommand(String itemName, int quantity, String category) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.category = category;
    }

    @Override
    public Payload getPayload() {
        return new Payload(itemName, quantity, category);
    }

    public String getItemName() {
        return itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getCategory() {
        return category;
    }

    public static class Payload {
        private final String itemName;
        private final int quantity;
        private final String category;

        public Payload(String itemName, int quantity, String category) {
            this.itemName = itemName;
            this.quantity = quantity;
            this.category = category;
        }

        public String getItemName() {
            return itemName;
        }

        public int getQuantity() {
            return quantity;
        }

        public String getCategory() {
            return category;
        }
    }
}