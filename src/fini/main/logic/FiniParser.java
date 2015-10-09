package fini.main.logic;

import java.io.File;
import java.util.ArrayList;

import fini.main.MainApp;
import fini.main.model.Task;
import fini.main.view.MainController;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import fini.main.view.StorageController;

public class FiniParser {
	private MainController mainController;
	private StorageController storageController;
	
	public void setMainController(MainController mainController) {
		System.out.println("main controller set");
		this.mainController = mainController;
	}
	
	private static enum CommandType {
		ADD, DELETE, UPDATE, CLEAR, SEARCH, SORT, EXIT, INVALID
	}
	
	public FiniParser() {
		System.out.println("Fini Parser Constructed");
		storageController = StorageController.getInstance("save.txt");
	}
	
	public String executeCommand(String userCommand) {
		String firstWord = getFirstWord(userCommand);
		CommandType commandType = getCommandType(firstWord);
		String return_string = "";
		switch (commandType) {
		case ADD:
			return_string = addTask(userCommand);
//			storageController.save(getMainApp().getTaskData());
			break;
		case DELETE:
			return_string = deleteTask(userCommand);
//			storageController.save(getMainApp().getTaskData());
			break;
//		case SEARCH:
//			return searchTask(userCommand);
		case UPDATE:
			return_string = updateTask(userCommand);
//			storageController.save(getMainApp().getTaskData());
			break;
//		case SORT:
//			return sortTask(userCommand);
		case EXIT:
			System.exit(0);
		case INVALID:
			return_string = "Why don't you try giving an actual command?";
			break;
		default:
			return_string = "ERROR executeCommand";
			break;
		}
		return return_string;
	}
	
	private CommandType getCommandType(String command) {
		String commandLowerCase = command.toLowerCase();
		if (commandLowerCase.equals("add")) {
			return CommandType.ADD;
		} else if (commandLowerCase.equals("clear")) {
			return CommandType.CLEAR;
		} else if (commandLowerCase.equals("delete")) {
			return CommandType.DELETE;
		} else if (commandLowerCase.equals("update")) {
			return CommandType.UPDATE;
		} else if (commandLowerCase.equals("sort")) {
			return CommandType.SORT;
		} else if (commandLowerCase.equals("exit")) {
			return CommandType.EXIT;
//		} else if () {
		} else {
			return CommandType.INVALID;
		}
	}
	
	private MainApp getMainApp() {
		return mainController.getMainApp();
	}
	
	private String getFirstWord(String userCommand) {
		String oneOrMoreSpaces = "\\s+";
		String[] splitUserCommand = userCommand.split(oneOrMoreSpaces);
		String firstWord = splitUserCommand[0];
		return firstWord;
	}
	
	private String removeFirstWord(String userCommand) {
		String blank = "";
		String firstWord = getFirstWord(userCommand);
		String removeFirstWord = userCommand.replaceFirst(firstWord, blank);
		String removeFirstWordTrimmed = removeFirstWord.trim();
		return removeFirstWordTrimmed;
	}
	
	private String addTask(String userCommand) {
		String taskToAdd = removeFirstWord(userCommand);
		Task tempTask;
		
		// check if there is a given date
		int indexOfTaskDetails = taskToAdd.indexOf("//");
		if (indexOfTaskDetails > 0) {
		  String taskDetails = taskToAdd.substring(indexOfTaskDetails + 3);
		  String taskName = taskToAdd.substring(0, indexOfTaskDetails);
		  tempTask = new Task(taskName, taskDetails);
		}
		else {
		  tempTask = new Task(taskToAdd);
		}
		getMainApp().getTaskData().add(tempTask);
		boolean isAddSuccess = true;
		if (isAddSuccess) {
			return "Added " + tempTask.getTitle();
		} else {
			return "Add Error";
		}
	}
	
	private int parseInt(String intString) {
		try {
			int value = Integer.parseInt(intString);
			return value;
		} catch (Exception e) {
			return -1; // Error Index
		}
	}
	
	private String deleteTask(String userCommand) {
		String taskNumberString = removeFirstWord(userCommand);
		int taskNumber = parseInt(taskNumberString);
		int actualTaskNumber = taskNumber - 1;
		Task deletedTask;
		try {
			deletedTask = getMainApp().getTaskData().remove(actualTaskNumber);
		} catch (Exception e) {
			deletedTask = null;
		}
		
		boolean isDeleteSuccess = deletedTask != null;
		if (isDeleteSuccess) {
			return "Deleted " + deletedTask.getTitle();
		} else {
			return "Delete Error";
		}
	}
	
	private String updateTask(String userCommand) {
		String[] userCommandArray = userCommand.split("\\s+");
		String taskNumberString = userCommandArray[1];
		String updateTitle = userCommandArray[2];
		int taskNumber = parseInt(taskNumberString);
		int actualTaskNumber = taskNumber - 1;
		
		Task tempTask = new Task(updateTitle);
		try {
			getMainApp().getTaskData().set(actualTaskNumber, tempTask);
			return "Updated";
		} catch (Exception e) {
			e.printStackTrace();
			return "Fatal error";
		}
	}
	
//	private String searchTask(String userCommand) {
//		String keyword = removeFirstWord(userCommand);
//		ArrayList<Task> searchList = logic.searchTask(keyword);
//		boolean searchSuccess = !searchList.isEmpty();
//		if (searchSuccess) {
//			return "Searched";
//		} else {
//			return "Search Error";
//		}
//	}
//	
//	
//	private String sortTask(String userCommand) {
//		boolean sortSuccess = logic.sortTask();
//		if (sortSuccess) {
//			return "Sorted";
//		} else {
//			return "Sort Error";
//		}
//	}
	
	
	/*private String saveFile() {
		File taskFile = getMainApp().getTaskFilePath();
		if (taskFile != null) {
			return getMainApp().saveTaskDataToFile(taskFile);
		} else {
			return saveFileAs();
		}
	}
	
    private String handleSaveAs() {
        FileChooser fileChooser = new FileChooser();
        
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "Text files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());

        if (file != null) {
            // Make sure it has the correct extension
            if (!file.getPath().endsWith(".xml")) {
                file = new File(file.getPath() + ".xml");
            }
            mainApp.savePersonDataToFile(file);
        }
    }()*/
}