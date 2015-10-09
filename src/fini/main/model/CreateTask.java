package fini.main.model;

public class CreateTask {
  private static CreateTask createTask;
  
  // default constructor
  private CreateTask() {
    
  }
  
  public CreateTask getInstance() {
    if(createTask == null) {
      createTask = new CreateTask();
    }
    return createTask;
  }
  
  public ArrayList<Task> createNewTask(SimpleStringProperty taskTitle, SimpleObjectProperty<LocalDate> date, 
      SimpleObjectProperty<LocalTime> startTime, SimpleObjectProperty<LocalTime> endTime) {
    Task newTask = new Task(taskTitle,  )
  }
}

