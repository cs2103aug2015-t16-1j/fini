package fini.main;

import java.io.IOException;

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
  public void start(Stage stage) {
    Parent parent = null;
	try {
		parent = FXMLLoader.load(getClass().getResource("view/FiniLayout.fxml"));
	} catch (IOException e) {
		e.printStackTrace();
	}
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
