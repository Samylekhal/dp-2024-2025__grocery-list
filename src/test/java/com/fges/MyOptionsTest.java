package com.fges;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class MyOptionsTest {

    @Test
    void should_parse_valid_arguments_with_format() {
        // Arrange
        MyOptions options = new MyOptions();
        String[] args = {"-s", "groceries.json", "-f", "json", "list"};

        // Act
        boolean result = options.parse(args);

        // Assert
        assertThat(result).isTrue();
        assertThat(options.getSourceFile()).isEqualTo("groceries.json");
        assertThat(options.getFormat()).isEqualTo("json");
        assertThat(options.getCommand()).isEqualTo("list");
        assertThat(options.getCommandArgs()).isEmpty();
    }

    @Test
    void should_parse_valid_arguments_with_csv_format() {
        // Arrange
        MyOptions options = new MyOptions();
        String[] args = {"-s", "groceries.csv", "-f", "csv", "list"};

        // Act
        boolean result = options.parse(args);

        // Assert
        assertThat(result).isTrue();
        assertThat(options.getSourceFile()).isEqualTo("groceries.csv");
        assertThat(options.getFormat()).isEqualTo("csv");
        assertThat(options.getCommand()).isEqualTo("list");
        assertThat(options.getCommandArgs()).isEmpty();
    }

    @Test
    void should_use_default_format_when_not_specified() {
        // Arrange
        MyOptions options = new MyOptions();
        String[] args = {"-s", "groceries.json", "list"};

        // Act
        boolean result = options.parse(args);

        // Assert
        assertThat(result).isTrue();
        assertThat(options.getSourceFile()).isEqualTo("groceries.json");
        assertThat(options.getFormat()).isEqualTo("json"); // Default format
        assertThat(options.getCommand()).isEqualTo("list");
    }

    @Test
    void should_fail_when_missing_source_option() {
        // Arrange
        MyOptions options = new MyOptions();
        String[] args = {"list"};  // Missing source option

        // Act
        boolean result = options.parse(args);

        // Assert
        assertThat(result).isFalse();
    }
}
