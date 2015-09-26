package main;

import java.io.IOException;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainApp extends Application {
	private static final String PROGRAM_TITLE = "Fini";
	private static final String PROGRAM_ICON = "/resources/images/icon.png";
	
	private static final String FILE_PATH_MAIN_SCENE_LAYOUT = "views/MainLayout.fxml";
	private static final String FILE_PATH_WELCOME_SCENE_LAYOUT = "views/WelcomeScene.fxml";
	
	private static final int MINIMUM_WINDOW_HEIGHT = 600;
	private static final int MINIMUM_WINDOW_WIDTH = 500;
	
	private static final int DURATION_OF_FADE_TRANSITION = 3000;
	
	private static final double OPACITY_ZERO = 0.0;
	private static final double OPACITY_FULL = 1.0;
	
	private Stage primaryStage;
	private Scene welcomeScreen;
	private AnchorPane rootLayout;
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		initializeStage();
	}

	private void initializeStage() throws IOException {
		intializePrimaryStage();
		displayWelcomeScene();
		fadeOutWelcomeScene();
		fadeInMainScene();
	}

	private void intializePrimaryStage() {
		primaryStage = new Stage();
		primaryStage.setTitle(PROGRAM_TITLE);
		primaryStage.getIcons().add(new Image(PROGRAM_ICON));
		primaryStage.setMinHeight(MINIMUM_WINDOW_HEIGHT);
		primaryStage.setMinWidth(MINIMUM_WINDOW_WIDTH);
	}

	private void displayWelcomeScene() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource(FILE_PATH_WELCOME_SCENE_LAYOUT));
		rootLayout = (AnchorPane) loader.load();
		welcomeScreen = new Scene(rootLayout);
		primaryStage.setScene(welcomeScreen);
		primaryStage.show();
	}

	private void fadeInMainScene() {
		Timeline timeline = new Timeline();
		timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(0.5),
		    new EventHandler<ActionEvent>() {

		        @Override
		        public void handle(ActionEvent event) {
		        	try {
						rootLayout = FXMLLoader.load(getClass().getResource(FILE_PATH_MAIN_SCENE_LAYOUT));
					} catch (IOException e) {
						e.printStackTrace();
					}
		    		FadeTransition mainSceneFadeIn = new FadeTransition(Duration.millis(DURATION_OF_FADE_TRANSITION), rootLayout);
		    		mainSceneFadeIn.setFromValue(OPACITY_ZERO);
		    		mainSceneFadeIn.setToValue(OPACITY_FULL);
		    		mainSceneFadeIn.play();
		    		primaryStage.setScene(new Scene(rootLayout));
		    		primaryStage.show();
		        }
		    }));
		timeline.play();
	}

	private void fadeOutWelcomeScene() {
		FadeTransition welcomeSceneFadeOut = new FadeTransition(Duration.millis(DURATION_OF_FADE_TRANSITION), rootLayout);
		welcomeSceneFadeOut.setFromValue(OPACITY_FULL);
		welcomeSceneFadeOut.setToValue(OPACITY_ZERO);
		welcomeSceneFadeOut.play();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
