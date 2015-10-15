package fini.main.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Storage {
  private static Storage taskOrganiser;
  private ObservableList<Task> taskMasterList;
  
  public Storage() {
    taskMasterList = FXCollections.observableArrayList();
  }
  
  public static Storage getInstance() {
    if(taskOrganiser == null) {
      taskOrganiser = new Storage();
    }
    return taskOrganiser;
  }

  public void addNewTask(Task newTask) {
     taskMasterList.add(newTask);
  }

  public ObservableList<Task> getTasks() {
    return taskMasterList;
  }
}
