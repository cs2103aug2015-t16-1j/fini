package fini.main;

import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

import fini.main.model.Task;
import fini.main.view.MainController;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainApp extends Application {
 protected static final double OPACITY_ZERO = 0;
protected static final double OPACITY_FULL = 100;
protected static final double DURATION_OF_FADE_TRANSITION = 3000;
private Stage primaryStage;
 private Scene welcomeScreen;
 private BorderPane rootLayout;
 private AnchorPane welcomeLayout;
 
 private ObservableList<Task> taskData = FXCollections.observableArrayList();
 
 public MainApp() {
  // Add some sample data
  taskData.add(new Task("Project Meeting"));
  taskData.add(new Task("Send iPhone for servicing"));
  taskData.add(new Task("Watch Superman"));
  taskData.add(new Task("Buy Groceries"));
  taskData.add(new Task("Do CS2103T Homework :/"));
 }
 
 public ObservableList<Task> getTaskData() {
  return taskData;
 }
 
 @Override
 public void start(Stage primaryStage) throws IOException {
  this.primaryStage = primaryStage;
  this.primaryStage.setTitle("Fini");
  displayWelcomeScene();
  fadeOutWelcomeScene();
  //fadeInMainScene();
  initRootLayout();
  
  showTaskOverview();
 }

 private void displayWelcomeScene() throws IOException {
     FXMLLoader loader = new FXMLLoader();
     loader.setLocation(MainApp.class.getResource("view/WelcomeScene.fxml"));
     welcomeLayout = (AnchorPane) loader.load();
     welcomeScreen = new Scene(welcomeLayout);
     primaryStage.setScene(welcomeScreen);
     primaryStage.show();
     System.out.println("SHowing WELCOME");
     SequentialTransition seqTransition = new SequentialTransition (
         new PauseTransition(Duration.millis(30000)) // wait a second
     );
     seqTransition.play();
 }

 private void fadeOutWelcomeScene() {
   FadeTransition welcomeSceneFadeOut = new FadeTransition(Duration.millis(DURATION_OF_FADE_TRANSITION), rootLayout);
   welcomeSceneFadeOut.setFromValue(OPACITY_FULL);
   welcomeSceneFadeOut.setToValue(OPACITY_ZERO);
   welcomeSceneFadeOut.play();
}
 
 private void fadeInMainScene() {
   Timeline timeline = new Timeline();
   timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(0.5),
       new EventHandler<ActionEvent>() {

           @Override
           public void handle(ActionEvent event) {
               try {
                   rootLayout = FXMLLoader.load(getClass().getResource("view/RootLayout.fxml"));
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

 
 public void initRootLayout() {
  try {
   // Load root layout from fxml file.
   FXMLLoader loader = new FXMLLoader();
   loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
   rootLayout = (BorderPane) loader.load();
   
   // Show the scene containing the root layout.
   Scene scene = new Scene(rootLayout);
   primaryStage.setScene(scene);
   scene.getStylesheets().add(getClass().getResource("view/rootLayout.css").toExternalForm());
   primaryStage.show();
  } catch (IOException e) {
   e.printStackTrace();
  }
 }
 
 public void showTaskOverview() {
  try {
   // Load task overview.
   FXMLLoader loader = new FXMLLoader();
   loader.setLocation(MainApp.class.getResource("view/TaskOverview.fxml"));
   AnchorPane taskOverview = (AnchorPane) loader.load();
   
   // Show task overview into the center of root layout.
   rootLayout.setCenter(taskOverview);
   
   // Give the controller access to the main app.
   MainController controller = loader.getController();
   controller.setMainApp(this);
   System.out.println("Controller Set!");
  } catch (IOException e) {
   e.printStackTrace();
  }
 }
 
 public Stage getPrimaryStage() {
  return primaryStage;
 }

 public static void main(String[] args) {
  launch(args);
 }
 
 public File getTaskFilePath() {
  Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
  String filePath = prefs.get("filePath", null);
  if (filePath != null) {
   return new File(filePath);
  } else {
   return null;
  }
 }
 
 public void setTaskFilePath(File file) {
  Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
  if (file != null) {
   prefs.put("filePath", file.getPath());
   
   primaryStage.setTitle("Fini - " + file.getName());
  } else {
   prefs.remove("filePath");
   
   primaryStage.setTitle("Fini");
  }
 }
}

