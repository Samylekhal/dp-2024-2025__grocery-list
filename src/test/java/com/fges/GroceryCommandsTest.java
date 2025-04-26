package com.fges;

import com.fges.commands.*;
import com.fges.storage.CsvStorage;
import com.fges.storage.JsonStorage;
import com.fges.storage.StorageInterface;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GroceryCommandsTest {

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

        StorageInterface storage = new JsonStorage(jsonFile.toString());
        CommandInterface listCommand = new ListCommand(storage);

        // Capture system output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        // act
        int result = listCommand.execute(List.of());

        // restore output
        System.setOut(originalOut);

        // assert
        assertThat(result).isEqualTo(0);
        assertThat(outContent.toString()).contains("Milk, 10", "Bread, 2");
    }

    @Test
    void should_load_items_from_csv() throws IOException {
        Path csvFile = tempDir.resolve("test.csv");
        Files.writeString(csvFile, "article,nombre,categorie\nMilk,10,default\nBread,2,default");

        StorageInterface storage = new CsvStorage(csvFile.toString());
        CommandInterface listCommand = new ListCommand(storage);

        // Capture system output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        // act
        int result = listCommand.execute(List.of());

        // restore output
        System.setOut(originalOut);

        // assert
        assertThat(result).isEqualTo(0);
        assertThat(outContent.toString()).contains("Milk, 10", "Bread, 2");
    }

    @Test
    void should_add_item_to_json_with_category() throws IOException {
        Path jsonFile = tempDir.resolve("test.json");
        Files.writeString(jsonFile, "{}");

        StorageInterface storage = new JsonStorage(jsonFile.toString());
        CommandInterface addCommand = new AddCommand(storage, "snacks");

        int result = addCommand.execute(List.of("Chips", "5"));

        assertThat(result).isEqualTo(0);

        String content = Files.readString(jsonFile);
        assertThat(content).contains("Chips, 5");
        assertThat(content).contains("snacks");
    }

    @Test
    void should_add_item_to_csv_with_category() throws IOException {
        Path csvFile = tempDir.resolve("test.csv");

        StorageInterface storage = new CsvStorage(csvFile.toString());
        CommandInterface addCommand = new AddCommand(storage, "snacks");

        int result = addCommand.execute(List.of("Chips", "5"));

        assertThat(result).isEqualTo(0);
        assertThat(Files.exists(csvFile)).isTrue();

        List<String> lines = Files.readAllLines(csvFile);
        assertThat(lines).contains("article,nombre,categorie", "Chips,5,snacks");
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

        StorageInterface storage = new JsonStorage(jsonFile.toString());
        CommandInterface removeCommand = new RemoveCommand(storage);

        int result = removeCommand.execute(List.of("Milk"));

        assertThat(result).isEqualTo(0);
        
        // Verify item was removed
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        
        CommandInterface listCommand = new ListCommand(storage);
        listCommand.execute(List.of());
        
        System.setOut(System.out);
        
        assertThat(outContent.toString()).doesNotContain("Milk, 10");
        assertThat(outContent.toString()).contains("Bread, 2");
    }

    @Test
    void should_remove_item_from_csv() throws IOException {
        Path csvFile = tempDir.resolve("test.csv");
        Files.writeString(csvFile, "article,nombre,categorie\nMilk,10,default\nBread,2,default");

        StorageInterface storage = new CsvStorage(csvFile.toString());
        CommandInterface removeCommand = new RemoveCommand(storage);

        int result = removeCommand.execute(List.of("Milk"));

        assertThat(result).isEqualTo(0);
        
        List<String> lines = Files.readAllLines(csvFile);
        assertThat(lines).doesNotContain("Milk,10,default");
        assertThat(lines).contains("article,nombre,categorie", "Bread,2,default");
    }

    @Test
    void should_list_items_from_json() throws IOException {
        Path jsonFile = tempDir.resolve("test.json");
        String jsonContent = """
                {
                    "default": ["Milk, 10", "Bread, 2"],
                    "snacks": ["Chips, 5"]
                }
                """;
        Files.writeString(jsonFile, jsonContent);

        StorageInterface storage = new JsonStorage(jsonFile.toString());
        CommandInterface listCommand = new ListCommand(storage);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        int result = listCommand.execute(List.of());

        assertThat(result).isEqualTo(0);
        assertThat(outContent.toString()).contains("# default:", "Milk, 10", "Bread, 2");
        assertThat(outContent.toString()).contains("# snacks:", "Chips, 5");

        System.setOut(System.out);
    }

    @Test
    void should_list_items_from_csv() throws IOException {
        Path csvFile = tempDir.resolve("test.csv");
        Files.writeString(csvFile, "article,nombre,categorie\nMilk,10,default\nBread,2,default\nChips,5,snacks");

        StorageInterface storage = new CsvStorage(csvFile.toString());
        CommandInterface listCommand = new ListCommand(storage);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        int result = listCommand.execute(List.of());

        assertThat(result).isEqualTo(0);
        assertThat(outContent.toString()).contains("# default:", "Milk, 10", "Bread, 2");
        assertThat(outContent.toString()).contains("# snacks:", "Chips, 5");

        System.setOut(System.out);
    }

    @Test
    void should_delete_json_file() throws IOException {
        Path jsonFile = tempDir.resolve("test.json");
        Files.writeString(jsonFile, "{}");

        StorageInterface storage = new JsonStorage(jsonFile.toString());
        CommandInterface deleteCommand = new DeleteCommand(storage);

        assertThat(Files.exists(jsonFile)).isTrue();

        int result = deleteCommand.execute(List.of());

        assertThat(result).isEqualTo(0);
        assertThat(Files.exists(jsonFile)).isFalse();
    }

    @Test
    void should_delete_csv_file() throws IOException {
        Path csvFile = tempDir.resolve("test.csv");
        Files.writeString(csvFile, "article,nombre,categorie");

        StorageInterface storage = new CsvStorage(csvFile.toString());
        CommandInterface deleteCommand = new DeleteCommand(storage);

        assertThat(Files.exists(csvFile)).isTrue();

        int result = deleteCommand.execute(List.of());

        assertThat(result).isEqualTo(0);
        assertThat(Files.exists(csvFile)).isFalse();
    }

    @Test
    void should_display_info() throws IOException {
        CommandInterface infoCommand = new InfoCommand();

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        int result = infoCommand.execute(List.of());

        assertThat(result).isEqualTo(0);
        assertThat(outContent.toString()).contains("Date:", "Operating System:", "Java Version:");

        System.setOut(System.out);
    }

    @Test
    void should_create_correct_command_via_factory() {
        CommandInterface command = CommandFactory.createCommand("add", "test.json", "json", "default");
        assertThat(command).isInstanceOf(AddCommand.class);

        command = CommandFactory.createCommand("list", "test.json", "json", "default");
        assertThat(command).isInstanceOf(ListCommand.class);

        command = CommandFactory.createCommand("remove", "test.json", "json", "default");
        assertThat(command).isInstanceOf(RemoveCommand.class);

        command = CommandFactory.createCommand("delete", "test.json", "json", "default");
        assertThat(command).isInstanceOf(DeleteCommand.class);

        command = CommandFactory.createCommand("info", "test.json", "json", "default");
        assertThat(command).isInstanceOf(InfoCommand.class);

        command = CommandFactory.createCommand("unknown", "test.json", "json", "default");
        assertThat(command).isNull();
    }

    @Test
    void should_fail_add_without_enough_arguments() throws IOException {
        StorageInterface storage = new JsonStorage("test.json");
        CommandInterface addCommand = new AddCommand(storage, "default");

        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        int result = addCommand.execute(List.of("Milk")); // Missing quantity

        assertThat(result).isEqualTo(1);
        assertThat(errContent.toString()).contains("Missing arguments");

        System.setErr(System.err);
    }

    @Test
    void should_fail_add_with_invalid_quantity() throws IOException {
        StorageInterface storage = new JsonStorage("test.json");
        CommandInterface addCommand = new AddCommand(storage, "default");

        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        int result = addCommand.execute(List.of("Milk", "abc")); // Invalid quantity

        assertThat(result).isEqualTo(1);
        assertThat(errContent.toString()).contains("Quantity must be a number");

        System.setErr(System.err);
    }

    @Test
    void should_fail_remove_without_arguments() throws IOException {
        StorageInterface storage = new JsonStorage("test.json");
        CommandInterface removeCommand = new RemoveCommand(storage);

        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        int result = removeCommand.execute(List.of()); // Missing item name

        assertThat(result).isEqualTo(1);
        assertThat(errContent.toString()).contains("Missing arguments");

        System.setErr(System.err);
    }

    @Test
    void main_should_work_with_valid_arguments() throws IOException {
        Path jsonFile = tempDir.resolve("test.json");
        
        String[] args = {
            "-s", jsonFile.toString(),
            "-f", "json",
            "-c", "default",
            "add", "Milk", "10"
        };
        
        int result = Main.exec(args);
        
        assertThat(result).isEqualTo(0);
        assertThat(Files.exists(jsonFile)).isTrue();
        
        String content = Files.readString(jsonFile);
        assertThat(content).contains("Milk, 10");
    }
}