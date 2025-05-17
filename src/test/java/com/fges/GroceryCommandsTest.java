package com.fges;

import com.fges.commands.AddItemCommand;
import com.fges.repository.GroceryRepositoryFactory;
import com.fges.commands.DeleteFileCommand;
import com.fges.commands.RemoveItemCommand;
import com.fges.core.CommandBus;
import com.fges.core.QueryBus;
import com.fges.handlers.AddItemCommandHandler;
import com.fges.handlers.DeleteFileCommandHandler;
import com.fges.handlers.InfoQueryHandler;
import com.fges.handlers.ListItemsQueryHandler;
import com.fges.handlers.RemoveItemCommandHandler;
import com.fges.queries.InfoQuery;
import com.fges.queries.ListItemsQuery;
import com.fges.repository.GroceryRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class GroceryCommandsTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private Path tempFilePath;
    private GroceryRepository repository;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @BeforeEach
    public void initializeRepository(@TempDir Path tempDir) throws IOException {
        tempFilePath = tempDir.resolve("groceries.json");
        Files.createFile(tempFilePath);

        // Initialiser le repository avec un fichier temporaire
        repository = repository = GroceryRepositoryFactory.createRepository(tempFilePath.toString(), "json");


        // Enregistrer les handlers pour les commandes et requêtes
        CommandBus.register(AddItemCommand.class, new AddItemCommandHandler(repository));
        CommandBus.register(RemoveItemCommand.class, new RemoveItemCommandHandler(repository));
        CommandBus.register(DeleteFileCommand.class, new DeleteFileCommandHandler(repository));
        QueryBus.register(ListItemsQuery.class, new ListItemsQueryHandler(repository));
        QueryBus.register(InfoQuery.class, new InfoQueryHandler());
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @AfterEach
    public void cleanUp() throws IOException {
        Files.deleteIfExists(tempFilePath);
    }

    @Test
    public void testAddItems() throws Exception {
        // Ajouter un article
        AddItemCommand addCommand = new AddItemCommand("Milk", 2, "dairy");
        int result = CommandBus.dispatch(addCommand);
        assertEquals(0, result);

        // Vérifier que l'article a été ajouté
        ListItemsQuery listQuery = new ListItemsQuery();
        result = QueryBus.dispatch(listQuery);
        assertEquals(0, result);
        assertTrue(outContent.toString().contains("Milk"));
        assertTrue(outContent.toString().contains("dairy"));
    }

    @Test
    public void testListEmptyList() throws Exception {
        // Tester la liste quand elle est vide
        ListItemsQuery listQuery = new ListItemsQuery();
        int result = QueryBus.dispatch(listQuery);
        assertEquals(0, result);
        assertTrue(outContent.toString().contains("No items found"));
    }

    @Test
    public void testRemoveItem() throws Exception {
        // Ajouter un article
        AddItemCommand addCommand = new AddItemCommand("Bread", 1, "bakery");
        int result = CommandBus.dispatch(addCommand);
        assertEquals(0, result);

        // Vider le contenu de sortie pour le prochain test
        outContent.reset();

        // Supprimer l'article
        RemoveItemCommand removeCommand = new RemoveItemCommand("Bread");
        result = CommandBus.dispatch(removeCommand);
        assertEquals(0, result);

        // Vérifier que l'article a été supprimé
        outContent.reset();
        ListItemsQuery listQuery = new ListItemsQuery();
        result = QueryBus.dispatch(listQuery);
        assertEquals(0, result);
        assertTrue(outContent.toString().contains("No items found"));
    }

    @Test
    public void testDeleteFile() throws Exception {
        // Ajouter un article
        AddItemCommand addCommand = new AddItemCommand("Cheese", 1, "dairy");
        int result = CommandBus.dispatch(addCommand);
        assertEquals(0, result);

        // Vider le contenu de sortie pour le prochain test
        outContent.reset();

        // Supprimer le fichier
        DeleteFileCommand deleteCommand = new DeleteFileCommand();
        result = CommandBus.dispatch(deleteCommand);
        assertEquals(0, result);
        assertTrue(outContent.toString().contains("File deleted successfully"));

        // Vérifier que le fichier a été supprimé
        assertFalse(Files.exists(tempFilePath));
    }

    @Test
    public void testInfo() throws Exception {
        // Tester la commande info
        InfoQuery infoQuery = new InfoQuery();
        int result = QueryBus.dispatch(infoQuery);
        assertEquals(0, result);

        // Vérifier que les informations du système sont affichées
        String output = outContent.toString();
        assertTrue(output.contains("Date:"));
        assertTrue(output.contains("Operating System:"));
        assertTrue(output.contains("Java Version:"));
    }

    @Test
    public void testCommandCreation() throws Exception {
        // Test des instances de commandes/requêtes correctes
        AddItemCommand addCommand = new AddItemCommand("Eggs", 12, "dairy");
        assertEquals("Eggs", addCommand.getItemName());
        assertEquals(12, addCommand.getQuantity());
        assertEquals("dairy", addCommand.getCategory());

        RemoveItemCommand removeCommand = new RemoveItemCommand("Eggs");
        assertEquals("Eggs", removeCommand.getItemName());

        DeleteFileCommand deleteCommand = new DeleteFileCommand();
        assertNotNull(deleteCommand.getPayload());

        ListItemsQuery listQuery = new ListItemsQuery();
        assertNotNull(listQuery.getParameters());

        InfoQuery infoQuery = new InfoQuery();
        assertNotNull(infoQuery.getParameters());
    }
}