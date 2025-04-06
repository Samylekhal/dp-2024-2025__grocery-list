package com.fges;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class GroceryServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void should_load_items_from_json_category() throws IOException {
        // arrange
        Path jsonFile = tempDir.resolve("test.json");
        String jsonContent = """
                {
                    "default": ["Milk, 10", "Bread, 2"]
                }
                """;
        Files.writeString(jsonFile, jsonContent);

        GroceryService service = new GroceryService(jsonFile.toString(), "json", "default");

        // act
        List<String> items = service.getItems();

        // assert
        assertThat(items).hasSize(2);
        assertThat(items).containsExactly("Milk, 10", "Bread, 2");
    }

    @Test
    void should_load_items_from_csv() throws IOException {
        Path csvFile = tempDir.resolve("test.csv");
        Files.writeString(csvFile, "article,nombre,categorie\nMilk,10,default\nBread,2,default");

        GroceryService service = new GroceryService(csvFile.toString(), "csv", "default");

        List<String> items = service.getItems();

        assertThat(items).hasSize(3); // header + 2 lines
        assertThat(items).contains("article,nombre,categorie", "Milk,10,default", "Bread,2,default");
    }

    @Test
    void should_save_items_to_json() throws IOException {
        Path jsonFile = tempDir.resolve("test.json");
        GroceryService service = new GroceryService(jsonFile.toString(), "json", "default");
        List<String> items = List.of("Milk, 10", "Bread, 2");

        service.saveItems(items);

        String content = Files.readString(jsonFile);
        assertThat(content).contains("Milk, 10");
        assertThat(content).contains("Bread, 2");
    }

    @Test
    void should_save_items_to_csv() throws IOException {
        Path csvFile = tempDir.resolve("test.csv");
        GroceryService service = new GroceryService(csvFile.toString(), "csv", "default");
        List<String> items = List.of("Milk,10,default", "Bread,2,default");

        service.saveItems(items);

        List<String> lines = Files.readAllLines(csvFile);
        assertThat(lines).hasSize(2);
        assertThat(lines).containsExactly("Milk,10,default", "Bread,2,default");
    }

    @Test
    void should_add_item_to_json_with_category() throws IOException {
        Path jsonFile = tempDir.resolve("test.json");
        Files.writeString(jsonFile, "{}");

        GroceryService service = new GroceryService(jsonFile.toString(), "json", "snacks");

        int result = service.executeCommand("add", List.of("Chips", "5"));

        assertThat(result).isEqualTo(0);

        String content = Files.readString(jsonFile);
        assertThat(content).contains("Chips, 5");
        assertThat(content).contains("snacks");
    }

    @Test
    void should_remove_item_from_json() throws IOException {
        Path jsonFile = tempDir.resolve("test.json");
        String jsonContent = """
                {
                    "default": ["Milk, 10", "Bread, 2"]
                }
                """;
        Files.writeString(jsonFile, jsonContent);

        GroceryService service = new GroceryService(jsonFile.toString(), "json", "default");

        int result = service.executeCommand("remove", List.of("Milk"));

        assertThat(result).isEqualTo(0);
        List<String> items = service.getItems();
        assertThat(items).hasSize(1);
        assertThat(items.get(0)).isEqualTo("Bread, 2");
    }

    @Test
    void should_list_items_from_json() throws IOException {
        Path jsonFile = tempDir.resolve("test.json");
        String jsonContent = """
                {
                    "default": ["Milk, 10", "Bread, 2"]
                }
                """;
        Files.writeString(jsonFile, jsonContent);

        GroceryService service = new GroceryService(jsonFile.toString(), "json", "default");

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        int result = service.executeCommand("list", List.of());

        assertThat(result).isEqualTo(0);
        assertThat(outContent.toString()).contains("Milk, 10", "Bread, 2");

        System.setOut(System.out);
    }

    @Test
    void should_delete_file() throws IOException {
        Path jsonFile = tempDir.resolve("test.json");
        Files.writeString(jsonFile, "{}");

        GroceryService service = new GroceryService(jsonFile.toString(), "json", "default");

        assertThat(Files.exists(jsonFile)).isTrue();

        int result = service.executeCommand("delete", List.of());

        assertThat(result).isEqualTo(0);
        assertThat(Files.exists(jsonFile)).isFalse();
    }
}
