package fini.main.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * Storage component - Nothing should be stored in Storage
 * @author gaieepo
 *
 */
public class Storage {
	private static Storage taskOrganiser;

	private File saveFile;
	private BufferedReader reader;
	private PrintWriter writer;

	private Gson gson;

	public static Storage getInstance() {
		if (taskOrganiser == null) {
			taskOrganiser = new Storage();
		}
		return taskOrganiser;
	}

	public Storage() {
		gson = new Gson();

		saveFile = new File("save.txt");
		createIfNotExists(saveFile);
	}

	public ArrayList<Task> readFile() {
		String text = "";
		ArrayList<Task> tasks = new ArrayList<Task>();
		
		try {
			if (!initReader(saveFile)) {
				return tasks;
			}
			while ((text = reader.readLine()) != null) {
				Task task = gson.fromJson(text, Task.class);
				tasks.add(task);
			}
		} catch (IOException | JsonSyntaxException e) {
			e.printStackTrace();
		}
		closeReader();
		
		if (tasks == null || tasks.isEmpty()) {
			tasks = new ArrayList<Task>();
		}
		return tasks;
	}

	public void updateFile(ArrayList<Task> tasks) {
		try {
			writer = new PrintWriter(saveFile, "UTF-8");
			for (Task task : tasks) {
				writer.println(gson.toJson(task));
			}
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		writer.close();
	}

	// Initialization Methods
	private boolean initReader(File saveFile) {
		try {
			reader = new BufferedReader(new FileReader(saveFile));
		} catch (FileNotFoundException e) {
			return false;
		}
		return true;
	}

	private void closeReader() {
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Utility Methods
	private void createIfNotExists(File saveFile) {
		try {
			if (!saveFile.exists()) {
				saveFile.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
