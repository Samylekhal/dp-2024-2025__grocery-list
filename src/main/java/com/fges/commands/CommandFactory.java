package com.fges.commands;

import com.fges.storage.CsvStorage;
import com.fges.storage.JsonStorage;
import com.fges.storage.StorageInterface;

public class CommandFactory {
    public static CommandInterface createCommand(String commandName, String fileName, String format, String category) {
        StorageInterface storage;
        
        if ("csv".equals(format.toLowerCase())) {
            storage = new CsvStorage(fileName);
        } else {
            storage = new JsonStorage(fileName);
        }
        
        return switch (commandName.toLowerCase()) {
            case "add" -> new AddCommand(storage, category);
            case "list" -> new ListCommand(storage);
            case "remove" -> new RemoveCommand(storage);
            case "delete" -> new DeleteCommand(storage);
            case "info" -> new InfoCommand();
            default -> null;
        };
    }
}
