package fini.main.view;

//import java.io.IOException;
import java.time.LocalDate;

import fini.main.MainApp;
import fini.main.logic.FiniParser;
import fini.main.model.Task;
import javafx.fxml.FXML;
//import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
//import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class TaskOverviewController {
	private String userCommand;
	private static FiniParser finiParser;
	
	@FXML
	private TableView<Task> taskTable;
	@FXML
	private TableColumn<Task, LocalDate> taskDateColumn; 
	@FXML
	private TableColumn<Task, String> taskTitleColumn;
	@FXML
	private TableColumn<Task, String> taskPriorityColumn;
	@FXML
	private TableColumn<Task, String> taskGroupColumn;
	@FXML
	private TextArea outputField;
	@FXML
	private TextField inputField;
	
	private MainApp mainApp;
	
	public TaskOverviewController() {
	}
		
	@FXML
	private void initialize() {
		System.out.println("Fini initialized");
		
		taskTitleColumn.setCellValueFactory(cellData -> cellData.getValue().taskTitleProperty());
		taskPriorityColumn.setCellValueFactory(cellData -> cellData.getValue().taskPriorityProperty());
		taskGroupColumn.setCellValueFactory(cellData -> cellData.getValue().taskGroupProperty());
		taskDateColumn.setCellValueFactory(cellData -> cellData.getValue().taskDateProperty());
		
		outputField.setEditable(false);
		
		initFiniParser();
		
		outputField.appendText("Welcome!" + System.lineSeparator());
	}
	
	public MainApp getMainApp() {
		return mainApp;
	}
	
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		
		taskTable.setItems(mainApp.getTaskData());
	}
	
	public void initFiniParser() {
		finiParser = new FiniParser();
		finiParser.setMainController(this);
	}
	
	@FXML
	private void handleCommand(KeyEvent keyEvent) throws Exception { // EVERY KEY!!! HELPFUL FOR SEARCH
		if (keyEvent.getCode() == KeyCode.ENTER) {
			executeCommandUserEntered();
		}
		
		// Alternative behavior:
		// UP / DOWN: previous command
		// ...
	}
	
	private void executeCommandUserEntered() throws Exception {
		if (isValidInput()) {
			userCommand = inputField.getText();
			inputField.setText("");
			
			String response = "";
			try {
//				response = executeCommand(userCommand);
				response = finiParser.executeCommand(userCommand);
				// Maybe save handler
			} catch (Exception e) {
				e.printStackTrace();
			}
			outputField.appendText(response + System.lineSeparator());
//			Update GUI
		}
	}
	
	private boolean isValidInput() {
		String errorMessage = "";
		if (inputField.getText() == null || inputField.getText().length() == 0) {
			errorMessage += "No valid input";
		}
		
		if (errorMessage.length() == 0) {
			return true;
		} else {
			// throw new EmptyUserInputException();
			// Alert
			return false;
		}
	}
}