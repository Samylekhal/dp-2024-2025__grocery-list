package com.fges.storage;

import java.io.IOException;

public interface StorageInterface {
    int addItem(String itemName, int quantity, String category) throws IOException;
    int listItems() throws IOException;
    int removeItem(String itemName) throws IOException;
    int deleteFile() throws IOException;
}
