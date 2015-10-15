package fini.main;

import fini.main.view.RootController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
  
  private RootController rootController;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) throws Exception {
    Parent parent = FXMLLoader.load(getClass().getResource("view/FiniLayout.fxml"));
    intialiseRootController();
    Scene scene = new Scene(parent);
    stage.setTitle("Fini");
    stage.setScene(scene);
    stage.show();
  }

  private void intialiseRootController() {
    rootController = new RootController();
  }
}
