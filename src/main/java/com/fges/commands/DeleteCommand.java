package com.fges.commands;

import com.fges.storage.StorageInterface;
import java.io.IOException;
import java.util.List;

public class DeleteCommand implements CommandInterface {
    private final StorageInterface storage;

    public DeleteCommand(StorageInterface storage) {
        this.storage = storage;
    }

    @Override
    public int execute(List<String> args) throws IOException {
        return storage.deleteFile();
    }
}
