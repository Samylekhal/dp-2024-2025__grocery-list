package com.fges.commands;

import com.fges.storage.StorageInterface;
import java.io.IOException;
import java.util.List;

public class ListCommand implements CommandInterface {
    private final StorageInterface storage;

    public ListCommand(StorageInterface storage) {
        this.storage = storage;
    }

    @Override
    public int execute(List<String> args) throws IOException {
        return storage.listItems();
    }
}
