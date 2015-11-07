package fini.main;

import java.io.IOException;
import java.util.logging.Logger;

import fini.main.view.RootController;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

/*
 * This is the main class for FINI (the application). The two scenes, welcome scene and the main
 * scene are loaded here.
 * 
 * @@author A0121828H
 */

public class MainApp extends Application {
	
	/* ***********************************
	 * DEFINE VARIABLES
	 * ***********************************/ 
	
	// Global Logger
	public final static Logger finiLogger = Logger.getLogger(MainApp.class.getName());

	@FXML
	private Button welcomeButton;
	
	private RootController rootController;
	private Brain brain;
	private Stage primaryStage;
	private static Scene scene;
	private AnchorPane parent = null;
	private AnchorPane main = null;

	/* ***********************************
	 * METHODS
	 * ***********************************/ 
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) {		
		primaryStage = stage;
		welcomeButton = new Button();
		try {
			parent = FXMLLoader.load(getClass().getResource("view/Welcome.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		scene = new Scene(parent);
		loadStylesheet();
		setListenerForWelcomeScene(parent);
		
		setUpPrimaryStage();		
		primaryStage.show();
	}

	private void loadStylesheet() {
		scene.getStylesheets().add(getClass().getResource("view/style.css").toExternalForm());
	}

	private void setUpPrimaryStage() {
		primaryStage.setTitle("Fini");
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("resources/images/icon.png")));
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
	}

	private void setListenerForWelcomeScene(AnchorPane parent) {
		final TextField welcomeSceneListener = new TextField();
		welcomeSceneListener.setLayoutX(-200);
		welcomeSceneListener.setLayoutY(-200);
		welcomeSceneListener.setOnKeyPressed(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent userPressesAKey) {
				try {
					FXMLLoader loader = new FXMLLoader(getClass().getResource("view/FiniLayout.fxml"));
					main = (AnchorPane) loader.load();

					Scene scene = new Scene(main);
					scene.getStylesheets().add(getClass().getResource("view/style.css").toExternalForm());
					
					primaryStage.setScene(scene); 

					rootController = loader.getController();

					primaryStage.show();
					primaryStage.setResizable(false);

					FadeTransition fadeIn = new FadeTransition(Duration.millis(1500), main);
					FadeTransition fadeOut = new FadeTransition(Duration.millis(1500), parent);

					fadeOut.setFromValue(1.0);
					fadeOut.setToValue(0.0);
					fadeOut.play();

					fadeIn.setFromValue(0.0);
					fadeIn.setToValue(1.0);
					fadeIn.play();

					initializeBrain();

				} catch (IOException e) {
					System.out.println("Unable to find or load FXML file");
					e.printStackTrace();
				}
			}
		});

		parent.getChildren().add(welcomeSceneListener);
		welcomeSceneListener.requestFocus();
	}

	private void initializeBrain() {
		brain = Brain.getInstance();
		brain.setRootController(this.rootController);
		brain.initDisplay();
	}
}
