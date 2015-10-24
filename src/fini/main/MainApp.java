package fini.main;

import java.io.File;
import java.io.IOException;
import java.rmi.server.LoaderHandler;
import java.util.logging.Logger;

import fini.main.view.RootController;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

/*
 * This is the main class for FINI (the application). The two scenes, welcome scene and the main
 * scene are handled here.
 * 
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
	private static Scene scene;
	private static boolean isNormalMode;


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
		scene = new Scene(parent);

		setListenerForWelcomeScene(parent);
		primaryStage.setTitle("Fini");
		// TODO: Icon not working
		//		primaryStage.getIcons().add(new Image("file:icon.png"));
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("resources/images/icon.png")));
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void setListenerForWelcomeScene(AnchorPane parent) {
		final TextField welcomeSceneListener = new TextField();
		welcomeSceneListener.setLayoutX(-200);
		welcomeSceneListener.setLayoutY(-200);
		welcomeSceneListener.setOnKeyPressed(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent userPressesEnter) {
				if (userPressesEnter.getCode().equals(KeyCode.ENTER)) {
					AnchorPane main = null;
					try {
						FXMLLoader loader = new FXMLLoader(getClass().getResource("view/FiniLayout2.fxml"));
						main = (AnchorPane) loader.load();
						
						FadeTransition ft = new FadeTransition(Duration.millis(3000), main);
						ft.setFromValue(0.0);
						ft.setToValue(1.0);
						ft.play();
						
						Scene scene = new Scene(main);
						primaryStage.setScene(scene); 

						rootController = loader.getController();

						primaryStage.show();
						
						initializeBrain();
						isNormalMode = true;
					} catch (IOException e) {
						System.out.println("Unable to find or load FXML file");
						e.printStackTrace();
					}
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

	// TODO: NightMode Switching
//	public static void switchMode() {
//		String loadCss;
//		if(isNormalMode) {
//			loadCss = this.getClass().getResource("view/nightMode.css").toExternalForm();
//		} else {
//			loadCss = this.getClass().getResource("view/style.css").toExternalForm();
//		}
//		scene.getStylesheets().clear();
//		scene.getStylesheets().add(css);
//	}
}
