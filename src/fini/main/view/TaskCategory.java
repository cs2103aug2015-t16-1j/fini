/*
 * @author A0121828H
 */
package fini.main.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class TaskCategory extends HBox {
    @FXML
    private Label category;

    @FXML
    private Label date;

    public TaskCategory(String taskCategory) throws Exception {
        initialiseFxmlFiles();
        category = new Label();
        this.category.setText("HARISH");
    }

    public TaskCategory(String taskCategory, String date) throws Exception {
        initialiseFxmlFiles();
        this.category = new Label();
        this.date = new Label();
        this.category.setText("HARISH");
        this.date.setText(date);
    }

    private void initialiseFxmlFiles() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TaskCategory.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
    }
}
