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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
/**
 * Storage component
 * @author gaieepo
 *
 */
public class Storage {
    private static Storage taskOrganiser;

    private ObservableList<Task> taskMasterList = FXCollections.observableArrayList();

    private File saveFile;
    private BufferedReader reader;
    private PrintWriter writer;

    private Gson gson;

    public static Storage getInstance() {
        if (taskOrganiser == null) {
            taskOrganiser = new Storage();
        }
        return taskOrganiser;
    }

    public Storage() {
        gson = new Gson();

        saveFile = new File("save.txt");
        createIfNotExists(saveFile);
    }

    public ArrayList<Task> readFile() {
    	taskMasterList = FXCollections.observableArrayList();
        ArrayList<Task> tempTaskMasterList;
        tempTaskMasterList = readTasks(saveFile);

        if (tempTaskMasterList == null || tempTaskMasterList.isEmpty()) {
            taskMasterList = FXCollections.observableArrayList();
        } else {
        	for (Task task : tempTaskMasterList) {
        		taskMasterList.add(task);
        	}
        }
        return tempTaskMasterList;
    }
    
    private ArrayList<Task> readTasks(File saveFile) {
        String text = "";
        ArrayList<Task> tempTaskMasterList = new ArrayList<Task>();
    	try {
            if (!initReader(saveFile)) {
            	return tempTaskMasterList;
            }
            while ((text = reader.readLine()) != null) {
                Task task = gson.fromJson(text, Task.class);
                tempTaskMasterList.add(task);
            }
        } catch (IOException | JsonSyntaxException e) {
            e.printStackTrace();
        }
        closeReader();
        return tempTaskMasterList;
    }

    public void updateFile() {
        try {
            writer = new PrintWriter(saveFile, "UTF-8");
            for (Task task : taskMasterList) {
                writer.println(gson.toJson(task));
            }
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        writer.close();
    }

    // Initialization Methods
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

    // Utility Methods
    private void createIfNotExists(File saveFile) {
        try {
            if (!saveFile.exists()) {
                saveFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addNewTask(Task newTask) {
        taskMasterList.add(newTask);
    }
    
    public void deleteTask(int taskId) {
    	taskMasterList.remove(taskId-1);
    }

    public ObservableList<Task> getTasks() {
        return taskMasterList;
    }
    
    public int getSize() {
        return taskMasterList.size();
    }
}
