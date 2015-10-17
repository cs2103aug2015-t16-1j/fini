package fini.main;

import java.io.IOException;
import java.util.logging.Logger;

import fini.main.view.RootController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/*
 * This is the main class for FINI (the application). The two scenes,
 * welcome scene and the main scene are handled here.
 * @author A0121828H
 */

public class MainApp extends Application {
	// Global Logger
	public final static Logger finiLogger = Logger.getLogger(MainApp.class.getName());

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
		AnchorPane parent = null;
		primaryStage = stage;
		welcomeButton = new Button();
		try {
			parent = FXMLLoader.load(getClass().getResource("view/Welcome.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Scene scene = new Scene(parent);

		setListenerForWelcomeScene(parent);
		primaryStage.setTitle("Fini");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void setListenerForWelcomeScene(AnchorPane parent) {
		final TextField welcomeSceneListener = new TextField();
		welcomeSceneListener.setLayoutX(-200);
		welcomeSceneListener.setLayoutY(-200);
		welcomeSceneListener.setOnKeyPressed(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent userPressesEnter) {
				if(userPressesEnter.getCode().equals(KeyCode.ENTER)) {
					Parent main = null;
					try {
						main = FXMLLoader.load(getClass().getResource("view/FiniLayout.fxml"));
					} catch (IOException e) {
						System.out.println("Unable to find or load FXML file");
						e.printStackTrace();
					}
					Scene scene = new Scene(main);
					primaryStage.setScene(scene);
					primaryStage.show();
					intialiseRootController();
				}
			}
		});
		parent.getChildren().add(welcomeSceneListener);
		welcomeSceneListener.requestFocus();
	}

	private void intialiseRootController() {
		rootController = new RootController();
		brain = Brain.getInstance();
		brain.setRootController(this.rootController);
	}
}
