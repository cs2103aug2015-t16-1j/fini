package fini.main.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class WelcomeController {
    @FXML
    private Button welcomeButton;

    @FXML
    public void handleKeyPress(KeyEvent event) {
	if(event.getCode() == KeyCode.ENTER) {

	}
    }
}
