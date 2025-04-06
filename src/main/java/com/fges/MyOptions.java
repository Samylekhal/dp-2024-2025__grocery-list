package com.fges;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.util.List;

public class MyOptions {
    private final Options options;
    private final CommandLineParser parser;
    private String sourceFile;
    private String format = "json"; // Default format is JSON
    private String category = "default"; // Default category is "default"
    private String command;
    private List<String> commandArgs;

    public MyOptions() {
        this.options = new Options();
        this.parser = new DefaultParser();
        setupOptions();
    }

    private void setupOptions() {
        options.addRequiredOption("s", "source", true, "File containing the grocery list");
        options.addOption("f", "format", true, "Format of the file containing the list (json or csv)");
        options.addOption("c", "category", true, "Category of the item to add");
    }

    public boolean parse(String[] args) {
        try {
            CommandLine cmd = parser.parse(options, args);
            sourceFile = cmd.getOptionValue("s");
    
            // Format est optionnel
            if (cmd.hasOption("f")) {
                format = cmd.getOptionValue("f").toLowerCase();
            }

            if (cmd.hasOption("c")) {
                category = cmd.getOptionValue("c").toLowerCase();
            }
    
            // Ajoute l'extension si elle manque
            if (!(sourceFile.endsWith(".json") || sourceFile.endsWith(".csv"))) {
                sourceFile += "." + format;
            }


    
            List<String> positionalArgs = cmd.getArgList();
            if (positionalArgs.isEmpty()) {
                System.err.println("Missing Command");
                return false;
            }
    
            command = positionalArgs.get(0);
            commandArgs = positionalArgs.subList(1, positionalArgs.size());
    
            return true;
        } catch (ParseException ex) {
            System.err.println("Fail to parse arguments: " + ex.getMessage());
            return false;
        }
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public String getFormat() {
        return format;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCommand() {
        return command;
    }

    public List<String> getCommandArgs() {
        return commandArgs;
    }
}