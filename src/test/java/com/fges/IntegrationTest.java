package com.fges;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class IntegrationTest {

    @TempDir
    Path tempDir;

    @Test
    void should_use_default_json_format_when_not_specified() throws IOException {
        // Given
        Path jsonFile = tempDir.resolve("groceries.json");
        String jsonPath = jsonFile.toString();

        // When: Add item without specifying format
        int addResult = Main.exec(new String[]{
                "-s", jsonPath,
                "add", "Milk", "10"
        });

        // Then
        assertThat(addResult).isEqualTo(0);

        // When: List items
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        int listResult = Main.exec(new String[]{
                "-s", jsonPath,
                "list"
        });

        // Then
        assertThat(listResult).isEqualTo(0);
        assertThat(outContent.toString()).contains("Milk: 10");

        // Restore stdout
        System.setOut(System.out);
    }

    @Test
    void should_work_with_csv_format() throws IOException {
        // Given
        Path csvFile = tempDir.resolve("groceries.csv");
        String csvPath = csvFile.toString();

        // When: Add item
        int addResult = Main.exec(new String[]{
                "-s", csvPath,
                "-f", "csv",
                "add", "Milk", "10"
        });

        // Then
        assertThat(addResult).isEqualTo(0);

        // When: List items
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        int listResult = Main.exec(new String[]{
                "-s", csvPath,
                "-f", "csv",
                "list"
        });

        // Then
        assertThat(listResult).isEqualTo(0);
        assertThat(outContent.toString()).contains("Milk: 10");

        // Restore stdout
        System.setOut(System.out);
    }
}