package fini.main.tests;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;

import org.junit.After;
import org.junit.Test;

import com.google.gson.Gson;

import fini.main.model.Storage;
import fini.main.model.Task;

public class StorageTest {
	File saveFile = new File("save.txt");
	File configFile = new File("config.txt");
	File userPrefFile = new File("Fini_untitled.txt");
	PrintWriter writer;
	BufferedReader reader;
	
	@Test
	public void testConstructor() {
		saveFile.delete();
		configFile.delete();
		userPrefFile.delete();
		
		Storage testStorage = Storage.getInstance();
		
		assertEquals(true, saveFile.exists());
		assertEquals(true, configFile.exists());
		assertEquals(true, userPrefFile.exists());
		
//		assertEquals("The directory is set", testStorage.setUserPrefDirectory("/home/gaieepo/Videos/test.txt"));
//		assertEquals("Invalid path", testStorage.setUserPrefDirectory("/home/Videos/test.txt"));
//		assertEquals("Invalid path", testStorage.setUserPrefDirectory("/home/gaieepo/Videos/tes?t.txt"));
//		assertEquals("Invalid path", testStorage.setUserPrefDirectory("/home/gaieepo/Videos/"));
	}
	
	
	@After
	public void cleanUp() {
		saveFile.delete();
		configFile.delete();
		userPrefFile.delete();
	}
}
