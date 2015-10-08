package fini.main.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
//import java.util.ArrayList;
//import java.util.logging.Level;

import com.google.gson.Gson;

import javafx.collections.ObservableList;
import fini.main.view.StorageController;
import fini.main.model.Task;

public class StorageController {
	private static StorageController storageController;
	private String saveFileName;
	private File saveFile;
//	private BufferedReader reader;
	private PrintWriter writer;
	private Gson gson;
	
	private StorageController(String fileName) {
		gson = new Gson();
		saveFileName = fileName;
		saveFile = new File(saveFileName);
        createIfMissingFile(saveFile);
	}
	
	public static StorageController getInstance(String fileName) {
		if (storageController == null) {
			storageController = new StorageController(fileName);
		}
		return storageController;
	}
	
	public Boolean save(ObservableList<Task> tasks) {
		try {
            writer = new PrintWriter("save.txt", "UTF-8");
            for (Task task : tasks) {
                writer.println(taskToJson(task));
            }
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
        writer.close();
        System.out.println("Saved");
        return true;		
	}
	
	private Object taskToJson(Task task) {
        return gson.toJson(task);
    }
	
	private void createIfMissingFile(File fileName) {
		try {
            if (!fileName.exists()) {
                fileName.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}
