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
	  ArrayList<Task> storedTask;
	  
	  String text = "";
	  
	  // Init BufferedReader
	  try {
		  reader = new BufferedReader(new FileReader(saveFile));
		  
		  Task task = gson.fromJson(text, Task.class);
		  storedTask.add(task);
	  } catch (FileNotFoundException e) {
		  storedTask = new ArrayList<Task>();
	  }
	  
	  
	  
	  try {
		  reader.close();
	  } catch (IOException e) {
		  e.printStackTrace();
	  }
  }
}
