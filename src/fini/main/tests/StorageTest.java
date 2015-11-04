package fini.main.tests;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import fini.main.model.FiniParser;
import fini.main.model.Storage;
import fini.main.model.Task;

public class StorageTest {
	File saveFile = new File("save.txt");
	File configFile = new File("config.txt");
	File userPrefFile = new File("Fini_untitled.txt");
	
	PrintWriter writer;
	BufferedReader reader;
	
	@Test
	public void testSettings() {
		saveFile.delete();
		configFile.delete();
		userPrefFile.delete();
		
		Storage testStorage = Storage.getInstance();
		
		assertEquals(true, saveFile.exists());
		assertEquals(true, configFile.exists());
		assertEquals(true, userPrefFile.exists());
	}
	
	@Test
	public void testReadAndUpdateFile() {
		Storage testStorage = Storage.getInstance();
		String[] data = {"curry chicken tomorrow morning project gai", 
						 "gaieepo the day before the third sat of dec", 
						 "harish tomorrow 5am repeat every two days priority high"};
		ArrayList<Task> inputData = createTasks(data);
		assertEquals(true, testStorage.updateFile(inputData));
		ArrayList<Task> outputData = testStorage.readFile();
		for (int i = 0; i < outputData.size(); ++i) {
			assertEquals(inputData.get(i).getTitle(), inputData.get(i).getTitle());
			assertEquals(inputData.get(i).getTaskType(), inputData.get(i).getTaskType());
			assertEquals(inputData.get(i).isCompleted(), inputData.get(i).isCompleted());
			assertEquals(inputData.get(i).getStartDateTime(), inputData.get(i).getStartDateTime());
			assertEquals(inputData.get(i).getEndDateTime(), inputData.get(i).getEndDateTime());
			assertEquals(inputData.get(i).getPriority(), inputData.get(i).getPriority());
			assertEquals(inputData.get(i).getProjectName(), inputData.get(i).getProjectName());
		}
	}
	
//	assertEquals("No such file", testStorage.setUserPrefDirectory("/home/gaieepo/Videos/test.txt"));
//	assertEquals("Invalid path", testStorage.setUserPrefDirectory("/home/g"));
//	assertEquals("Invalid path", testStorage.setUserPrefDirectory("/home/gaieepo/Videos/tes?t.txt"));
//	assertEquals("Invalid path", testStorage.setUserPrefDirectory("/home/gaieepo/Videos/"));
	
	private ArrayList<Task> createTasks(String[] data) {
		FiniParser finiParser = FiniParser.getInstance();
		ArrayList<Task> tasks = new ArrayList<Task>();
		for (String taskString : data) {
			finiParser.parse(taskString);
			tasks.add(new Task.TaskBuilder(finiParser.getNotParsed(), finiParser.getIsRecurring())
						.setDatetimes(finiParser.getDatetimes())
						.setPriority(finiParser.getPriority())
						.setProjectName(finiParser.getProjectName())
						.setInterval(finiParser.getInterval())
						.setRecursUntil(finiParser.getRecursUntil()).build());
		}
		return tasks;
	}
	
	@After
	public void cleanUp() {
		saveFile.delete();
		configFile.delete();
		userPrefFile.delete();
	}
}
