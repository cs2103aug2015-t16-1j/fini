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
	private File configFile;
	private File userPrefFile;
	
	private String userPrefFileName;
	
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
		
		configFile = new File("config.txt");
		createIfNotExists(configFile);
		
		userPrefFileName = getUserPrefFileName(configFile);
		updateConfigFile(userPrefFileName);
		userPrefFile = new File(userPrefFileName);
		createIfNotExists(userPrefFile);
	}

	public ArrayList<Task> readFile() {
		ArrayList<Task> tasks = new ArrayList<Task>();
		tasks = readTasks(userPrefFile);
		if (tasks == null || tasks.isEmpty()) {
			tasks = readTasks(saveFile);
			if (tasks == null || tasks.isEmpty()) {
				tasks = new ArrayList<Task>();
			}
		}
		return tasks;
	}
	
	private ArrayList<Task> readTasks(File file) {
		String text = "";
		ArrayList<Task> tasks = new ArrayList<Task>();
		try {
			if (!initReader(file)) {
				return tasks;
			}
			while ((text = reader.readLine()) != null) {
				Task task = gson.fromJson(text, Task.class);
				tasks.add(task);
			}
		} catch (IOException | JsonSyntaxException e) {
			e.printStackTrace();
			return null;
		}
		closeReader();
		return tasks;
	}

	public void updateFile(ArrayList<Task> tasks) {
		updateTasks(saveFile, tasks);
		updateTasks(userPrefFile, tasks);
	}
	
	private boolean updateTasks(File file, ArrayList<Task> tasks) {
		try {
			writer = new PrintWriter(file, "UTF-8");
			for (Task task : tasks) {
				writer.println(gson.toJson(task));
			}
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		}
		writer.close();
		return true;
	}
	
	public String setUserPrefDirectory(String filePath) {
		userPrefFileName = filePath;
		File userFile = new File(userPrefFileName);
		if (userFile.equals(userPrefFile)) {
			return "Same file directory";
		}
		createIfNotExists(userFile);
		updateConfigFile(userPrefFileName);
		userPrefFile = userFile;
		return "The directory is set";
	}
	
	private void updateConfigFile(String fileName) {
		try {
			writer = new PrintWriter(configFile, "UTF-8");
			writer.println(fileName);
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
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
	private void createIfNotExists(File file) {
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String getUserPrefFileName(File configFile) {
		initReader(configFile);
		String fileName = "";
		try {
			if ((fileName = reader.readLine()) == null) {
				fileName = "Fini_untitled.txt";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		closeReader();
		return fileName;
	}
}
