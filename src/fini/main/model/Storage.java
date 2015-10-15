package fini.main.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.media.VideoTrack;

public class Storage {
  private static Storage taskOrganiser;
  private ObservableList<Task> taskMasterList;
  
  private File saveFile;
  private BufferedReader reader;
  private PrintWriter writer;
  private Gson gson;
  
  public Storage() {
    taskMasterList = FXCollections.observableArrayList();
    gson = new Gson();
    saveFile = new File("save.txt");
    createIfNotExists(saveFile);
  }
  
  public static Storage getInstance() {
    if(taskOrganiser == null) {
      taskOrganiser = new Storage();
    }
    return taskOrganiser;
  }
  
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

  public ObservableList<Task> getTasks() {
    return taskMasterList;
  }
  
  public void readFile() {
	  ArrayList<Task> storedTask = new ArrayList<Task>();
	  String text = "";
	  
	  try {
		reader = new BufferedReader(new FileReader(saveFile));
		while ((text = reader.readLine()) != null) {
			Task task = gson.fromJson(text, Task.class);
			storedTask.add(task);
		}
	} catch (JsonSyntaxException | IOException e) {
		e.printStackTrace();
	}
	  
	  if (storedTask == null || storedTask.isEmpty()) {
		storedTask = new ArrayList<Task>();
	}
	  
	  
	  
	  try {
		reader.close();
	} catch (IOException e) {
		e.printStackTrace();
	}
  }
}
