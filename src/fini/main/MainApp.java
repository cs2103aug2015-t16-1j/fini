package fini.main;

import java.io.IOException;

import fini.main.view.RootController;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class MainApp extends Application {
	@FXML
	private Button welcomeButton;
	
	private RootController rootController;
	private Brain brain;
	private Stage primaryStage = new Stage();
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) {
		Parent parent = null;
		primaryStage = stage;
		welcomeButton = new Button();
		try {
			parent = FXMLLoader.load(getClass().getResource("view/Welcome.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Scene scene = new Scene(parent);
		primaryStage.setTitle("Fini");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void intialiseRootController() {
		brain = Brain.getInstance();
		rootController = new RootController();
		brain.setRootController(this.rootController);
	}
	
	@FXML
	public void handleKeyPress(KeyEvent event) throws IOException {
		if(event.getCode() == KeyCode.ENTER) {
			Parent main = null;
			main = FXMLLoader.load(getClass().getResource("view/FiniLayout.fxml"));
			Scene scene = new Scene(main);
			primaryStage.setScene(scene);
			primaryStage.show();
			intialiseRootController();
		}
	}
}
