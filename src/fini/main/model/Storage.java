package fini.main.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import fini.main.MainApp;

/**
 * Storage component (which is instantiated as taskOrganizer in Brain) takes charge in saving and retrieving of tasks
 * It makes use of google json library - Gson
 * to solidate tasks into readable text format.
 * 
 * A Java serialization library that can convert Java Objects into JSON and back
 * https://github.com/google/gson
 * 
 * There are total three files that Storage will hand on.
 * save.txt -> Main save file, when the user preferred file is not found, it acts like a back up file
 * config.txt -> Store the directory of user preferred file
 * user preferred file (Fini_untitled.txt) -> user preferred saving file, Storage will access this file first
 * Notice that user preferred file must be created before the path is set.
 * 
 * @@author A0127483B 
 */
public class Storage {
    /* ***********************************
     * Constants
     * ***********************************/
    private static final String SAVE_FILE_NAME = "save.txt";
    private static final String CONFIG_FILE_NAME = "config.txt";
    private static final String DEFAULT_USER_PREF_NAME = "Fini_untitled.txt";
    
    private static final String SAME_DIRECTORY_MESSAGE = "Same file directory";
    private static final String SET_SUCCESS_MESSAGE = "The directory is set";
    private static final String SET_FAIL_MESSAGE = "No such file";
    private static final String EMPTY_STRING = "";
    
    /* ***********************************
     * Fields
     * ***********************************/
    private static Storage taskOrganiser;

    private File saveFile;
    private File configFile;
    private File userPrefFile;

    private String userPrefFileName;

    private BufferedReader reader;
    private PrintWriter writer;

    private Gson gson;

    /* ***********************************
     * Private constructor
     * ***********************************/
    private Storage() {
        gson = new Gson();

        saveFile = new File(SAVE_FILE_NAME);
        createIfNotExists(saveFile);

        configFile = new File(CONFIG_FILE_NAME);
        createIfNotExists(configFile);

        userPrefFileName = getUserPrefFileName(configFile);
        updateConfigFile(userPrefFileName);
        userPrefFile = new File(userPrefFileName);
        createIfNotExists(userPrefFile);
        
        MainApp.finiLogger.info("All file instantiated");
    }
    
    public static Storage getInstance() {
        if (taskOrganiser == null) {
            taskOrganiser = new Storage();
        }
        return taskOrganiser;
    }
    
    /* ***********************************
     * Public methods
     * ***********************************/
    public ArrayList<Task> readFile() {
        ArrayList<Task> tasks = new ArrayList<Task>();
        tasks = readTasks(userPrefFile);
        if (tasks == null || tasks.isEmpty()) {
            tasks = readTasks(saveFile);
            if (tasks == null || tasks.isEmpty()) {
                MainApp.finiLogger.info("Back up cannot save you!");
                tasks = new ArrayList<Task>();
            } else if (!tasks.isEmpty()) {
                MainApp.finiLogger.info("Main save file is damaged. Immediate back up plan is on!");
            }
        } else {
            MainApp.finiLogger.info("Successfully read file");
        }
        return tasks;
    }
    
    public String setUserPrefDirectory(String filePath) {
        userPrefFileName = filePath;
        File userFile = new File(userPrefFileName);

        if (userFile.equals(userPrefFile)) {
            return SAME_DIRECTORY_MESSAGE;
        } else if (userFile.exists()) {
            updateConfigFile(userPrefFileName);
            userPrefFile = userFile;
            return SET_SUCCESS_MESSAGE;
        } else {
            return SET_FAIL_MESSAGE;
        }
    }

    public boolean updateFile(ArrayList<Task> tasks) {
        return updateTasks(saveFile, tasks) && updateTasks(userPrefFile, tasks);
    }
    
    /* ***********************************
     * Private methods
     * ***********************************/
    private ArrayList<Task> readTasks(File file) {
        String text = EMPTY_STRING;
        ArrayList<Task> tasks = new ArrayList<Task>();
        try {
            if (!initReader(file)) {
                return tasks;
            }
            while ((text = reader.readLine()) != null) {
                Task task = gson.fromJson(text, Task.class);
                tasks.add(task);
            }
        } catch (IOException | JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
        closeReader();
        return tasks;
    }

    private boolean updateTasks(File file, ArrayList<Task> tasks) {
        try {
            writer = new PrintWriter(file, "UTF-8");
            for (Task task : tasks) {
                writer.println(gson.toJson(task));
            }
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
        writer.close();
        return true;
    }

    private void updateConfigFile(String fileName) {
        try {
            writer = new PrintWriter(configFile, "UTF-8");
            writer.println(fileName);
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /* ***********************************
     * Initialization methods
     * ***********************************/
    private boolean initReader(File saveFile) {
        try {
            reader = new BufferedReader(new FileReader(saveFile));
        } catch (FileNotFoundException e) {
            return false;
        }
        return true;
    }

    private void closeReader() {
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* ***********************************
     * Utility methods
     * ***********************************/
    private void createIfNotExists(File file) {
        try {
            if (!file.exists()) {
                file.createNewFile();
                MainApp.finiLogger.info(file.toString() + " is created");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getUserPrefFileName(File configFile) {
        initReader(configFile);
        String fileName = EMPTY_STRING;
        try {
            if ((fileName = reader.readLine()) == null) {
                fileName = DEFAULT_USER_PREF_NAME;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        closeReader();
        return fileName;
    }
}
