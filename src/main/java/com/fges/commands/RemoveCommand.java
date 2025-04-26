// RemoveCommand.java
package com.fges.commands;

import com.fges.storage.StorageInterface;
import java.io.IOException;
import java.util.List;

public class RemoveCommand implements CommandInterface {
    private final StorageInterface storage;

    public RemoveCommand(StorageInterface storage) {
        this.storage = storage;
    }

    @Override
    public int execute(List<String> args) throws IOException {
        if (args.isEmpty()) {
            System.err.println("Missing arguments. Usage: remove <item_name>");
            return 1;
        }
        
        String itemName = args.get(0).toLowerCase();
        return storage.removeItem(itemName);
    }
}