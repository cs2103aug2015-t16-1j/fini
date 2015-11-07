/*
 * TaskCategory models each category of tasks in the main display.
 * @@author A0121828H
 */
package fini.main.view;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class TaskCategory extends HBox {
    @FXML
    private Label category;

    public TaskCategory(String taskCategory) {
        initialiseFxmlFiles();
        this.category.setText(taskCategory);
        if(taskCategory.equals("Overdue")) {
        	category.setTextFill(Color.web("#cc0000"));
        }
    }

    private void initialiseFxmlFiles() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TaskCategory.fxml"));
            loader.setRoot(this);
            loader.setController(this);
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
