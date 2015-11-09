package fini.main;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import fini.main.view.DisplayController;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

/*
 * This is the main class for FINI (the application). The two scenes, welcome scene and the main
 * scene are loaded here. The welcome scene is launched and slowly fades away to reveal the main
 * display
 * 
 * @@author A0121828H
 */

public class MainApp extends Application {

    private static final String STAGE_TITLE = "Fini";

    // Opacities for Fade Animation
    private static final double OPACITY_FULL = 1.0;
    private static final double OPACITY_ZERO = 0.0;

    // Duration for Fade Animations
    private static final int FADE_DURATION = 1500;

    // Path to relevant files
    private static final String PATH_FINI_LAYOUT = "view/FiniLayout.fxml";
    private static final String PATH_FINI_ICON = "resources/images/icon.png";
    private static final String PATH_WELCOME_FXML = "view/Welcome.fxml";
    private static final String PATH_STYLESHEET = "view/style.css";
    private static final String PATH_LOGFILE = "Logfile.txt";

    /* ***********************************
     * DEFINE VARIABLES
     * ***********************************/ 

    // Global Logger
    public final static Logger finiLogger = Logger.getLogger(MainApp.class.getName());

    private DisplayController displayController;
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
        setUpPrimaryStage();		
        intialiseLogger();

        fadeOut(parent);

        SequentialTransition seqTransition = new SequentialTransition (new PauseTransition(Duration.millis(1500)));
        seqTransition.setOnFinished(event ->  transitToMainScene());
        seqTransition.play();
    }

    private void intialiseLogger() {
        FileHandler fileHandler;  

        try {  
            fileHandler = new FileHandler(PATH_LOGFILE);  
            finiLogger.addHandler(fileHandler);
            SimpleFormatter formatter = new SimpleFormatter();  
            fileHandler.setFormatter(formatter);  

            // Initial Log Message
            finiLogger.info("Fini Logger Intialised. All is well.");  

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadStylesheet() {
        scene.getStylesheets().add(getClass().getResource(PATH_STYLESHEET).toExternalForm());
    }

    private void setUpPrimaryStage() {
        try {
            parent = FXMLLoader.load(getClass().getResource(PATH_WELCOME_FXML));
        } catch (IOException e) {
            e.printStackTrace();
        }
        scene = new Scene(parent);
        loadStylesheet();

        primaryStage.setTitle(STAGE_TITLE);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream(PATH_FINI_ICON)));
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    
    /*
     * This method transits Fini from the Welcome Scene to the main display
     * of tasks. It loads the respective FXML and employs a fade transition.
     */
    private void transitToMainScene() {
        try {  
            FXMLLoader loader = new FXMLLoader(getClass().getResource(PATH_FINI_LAYOUT));
            main = (AnchorPane) loader.load();

            Scene scene = new Scene(main);
            scene.getStylesheets().add(getClass().getResource(PATH_STYLESHEET).toExternalForm());

            fadeIn(main);
            primaryStage.setScene(scene);

            displayController = loader.getController();

            primaryStage.show();
            primaryStage.setResizable(false);

            initializeBrain();
        } catch (IOException exception) {
            finiLogger.severe("Unable to find or load FXML file");
            exception.printStackTrace();
        }
    }

    private void fadeOut(Node element) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(FADE_DURATION), element);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.play();
    }

    private void fadeIn(Node element) {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(FADE_DURATION), element);
        fadeIn.setFromValue(OPACITY_ZERO);
        fadeIn.setToValue(OPACITY_FULL);
        fadeIn.play();
    }

    private void initializeBrain() {
        brain = Brain.getInstance();
        brain.setDisplayController(this.displayController);
        brain.initDisplay();
    }
}