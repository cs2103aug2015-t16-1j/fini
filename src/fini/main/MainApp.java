package fini.main;

import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

import fini.main.model.Task;
import fini.main.view.TaskOverviewController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {
 private Stage primaryStage;
 private BorderPane rootLayout;
 
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
 public void start(Stage primaryStage) {
  this.primaryStage = primaryStage;
  this.primaryStage.setTitle("Fini");
  
  initRootLayout();
  
  showTaskOverview();
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
   TaskOverviewController controller = loader.getController();
   controller.setMainApp(this);
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

