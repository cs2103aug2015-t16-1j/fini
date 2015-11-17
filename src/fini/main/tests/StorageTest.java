package fini.main.tests;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fini.main.model.FiniParser;
import fini.main.model.Storage;
import fini.main.model.Task;

/*
 * This class performs tests on the Storage class to test if the various write and
 * read methods are working correctly.
 * 
 * @@author A0127483B 
 */

public class StorageTest {
    File saveFile = new File("save.txt");
    File configFile = new File("config.txt");
    File userPrefFile = new File("Fini_untitled.txt");
    File testFile = new File("/Users/HarishV/Documents/test.txt");

    PrintWriter writer;
    BufferedReader reader;

    @Before
    public void initStorage() {
        Storage.clearStorage();
    }

    @Test
    public void testStorageInit() {
        saveFile.delete();
        configFile.delete();
        userPrefFile.delete();
        testFile.delete();

        Storage.getInstance();

        assertEquals(true, saveFile.exists());
        assertEquals(true, configFile.exists());
        assertEquals(true, userPrefFile.exists());
    }

    @Test
    public void testReadAndUpdateFile() {
        String[] data = {"curry chicken tomorrow morning project gai", 
                "gaieepo the day before the third sat of dec", 
        "harish tomorrow 5am repeat every two days priority high"};
        ArrayList<Task> inputData = createTasks(data);
        Storage testStorage = Storage.getInstance();
        assertEquals(true, testStorage.updateFile(inputData));
        ArrayList<Task> outputData = testStorage.readFile();
        compareTasks(inputData, outputData);
    }

    @Test
    public void testSetUserPrefDirectory() {
        Storage testStorage = Storage.getInstance();
        assertEquals("No such file", testStorage.setUserPrefDirectory("/Users/HarishV/Documents/test.txt"));

        if (!testFile.exists()) {
            try {
                testFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        // Directory tested is unique to different users
        assertEquals("The directory is set", testStorage.setUserPrefDirectory("/Users/HarishV/Documents/test.txt"));
        assertEquals("Same file directory", testStorage.setUserPrefDirectory("/Users/HarishV/Documents/test.txt"));
        assertEquals("No such file", testStorage.setUserPrefDirectory("/Users/HarishV/Documents/test123.txt"));
        assertEquals("No such file", testStorage.setUserPrefDirectory("/home/harish/test.txt"));
    }

    @Test
    public void testBackUpSaveFile() {
        String[] data = {"curry chicken tomorrow morning project gai", 
                "gaieepo the day before the third sat of dec", 
        "harish tomorrow 5am repeat every two days priority high"};
        ArrayList<Task> inputData = createTasks(data);
        ArrayList<Task> outputData = new ArrayList<Task>();
        Storage testStorage = Storage.getInstance();
        assertEquals(true, testStorage.updateFile(inputData));

        // Spoil the userPrefFile
        try {
            writer = new PrintWriter(userPrefFile, "UTF-8");
            writer.println("Victory Draws Near!");
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        outputData = testStorage.readFile();
        compareTasks(inputData, outputData);

        // userPrefFile not found
        userPrefFile.delete();
        outputData = testStorage.readFile();
        compareTasks(inputData, outputData);

        try {
            writer = new PrintWriter(saveFile, "UTF-8");
            writer.println("Victory Draws Near!");
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        outputData = testStorage.readFile();
        assertEquals(true, outputData.isEmpty());
    }

    private ArrayList<Task> createTasks(String[] data) {
        FiniParser finiParser = FiniParser.getInstance();
        ArrayList<Task> tasks = new ArrayList<Task>();
        for (String taskString : data) {
            finiParser.parse(taskString);
            tasks.add(new Task.TaskBuilder(finiParser.getNotParsed(), finiParser.getIsRecurring())
                    .setDatetimes(finiParser.getDatetimes())
                    .setPriority(finiParser.getPriority())
                    .setProjectName(finiParser.getProjectName())
                    .setInterval(finiParser.getInterval())
                    .setRecursUntil(finiParser.getRecursUntil()).build());
        }
        return tasks;
    }

    private void compareTasks(ArrayList<Task> inputData, ArrayList<Task> outputData) {
        for (int i = 0; i < outputData.size(); ++i) {
            assertEquals(inputData.get(i).getTitle(), inputData.get(i).getTitle());
            assertEquals(inputData.get(i).getTaskType(), inputData.get(i).getTaskType());
            assertEquals(inputData.get(i).isCompleted(), inputData.get(i).isCompleted());
            assertEquals(inputData.get(i).getStartDateTime(), inputData.get(i).getStartDateTime());
            assertEquals(inputData.get(i).getEndDateTime(), inputData.get(i).getEndDateTime());
            assertEquals(inputData.get(i).getPriority(), inputData.get(i).getPriority());
            assertEquals(inputData.get(i).getProjectName(), inputData.get(i).getProjectName());
        }
    }

    @After
    public void cleanUp() {
        saveFile.delete();
        configFile.delete();
        userPrefFile.delete();
        testFile.delete();
    }
}
