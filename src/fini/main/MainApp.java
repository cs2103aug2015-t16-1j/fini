package fini.main;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import fini.main.view.DisplayController;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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
    }

    private void intialiseLogger() {
        FileHandler fileHandler;  

        try {  
            fileHandler = new FileHandler("Logfile.txt");  
            finiLogger.addHandler(fileHandler);
            SimpleFormatter formatter = new SimpleFormatter();  
            fileHandler.setFormatter(formatter);  

            // Intial Log Message
            finiLogger.info("Fini Logger Intialised. All is well.");  

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadStylesheet() {
        scene.getStylesheets().add(getClass().getResource("view/style.css").toExternalForm());
    }

    private void setUpPrimaryStage() {
        welcomeButton = new Button();
        try {
            parent = FXMLLoader.load(getClass().getResource("view/Welcome.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        scene = new Scene(parent);
        loadStylesheet();
        setListenerForWelcomeScene(parent);

        primaryStage.setTitle("Fini");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("resources/images/icon.png")));
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void setListenerForWelcomeScene(AnchorPane parent) {
        final TextField welcomeSceneListener = new TextField();
        positionWelcomeListener(welcomeSceneListener);
        welcomeSceneListener.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent userPressesAKey) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("view/FiniLayout.fxml"));
                    main = (AnchorPane) loader.load();

                    Scene scene = new Scene(main);
                    scene.getStylesheets().add(getClass().getResource("view/style.css").toExternalForm());

                    primaryStage.setScene(scene); 

                    displayController = loader.getController();

                    primaryStage.show();
                    primaryStage.setResizable(false);

                    fadeOut(parent);   
                    fadeIn(main);

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

    private void positionWelcomeListener(final TextField welcomeSceneListener) {
        welcomeSceneListener.setLayoutX(-200);
        welcomeSceneListener.setLayoutY(-200);
    }

    private void fadeOut(Node element) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), element);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.play();
    }

    private void fadeIn(Node element) {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), element);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    private void initializeBrain() {
        brain = Brain.getInstance();
        brain.setRootController(this.displayController);
        brain.initDisplay();
    }
}
