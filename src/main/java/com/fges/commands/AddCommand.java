
package com.fges.commands;

import com.fges.storage.StorageInterface;
import java.io.IOException;
import java.util.List;
public class AddCommand implements CommandInterface {
    private final StorageInterface storage;
    private final String category;

    public AddCommand(StorageInterface storage, String category) {
        this.storage = storage;
        this.category = category;
    }

    @Override
    public int execute(List<String> args) throws IOException {
        // Vérification des arguments : on ne considère que l'article et la quantité.
        if (args.size() < 2) {
            System.err.println("Missing arguments. Usage: add <item_name> <quantity>");
            return 1;
        }
        
        String itemName = args.get(0);
        int quantity;
        try {
            quantity = Integer.parseInt(args.get(1));
        } catch (NumberFormatException e) {
            System.err.println("Quantity must be a number");
            return 1;
        }

        return storage.addItem(itemName, quantity, category);
    }
}
