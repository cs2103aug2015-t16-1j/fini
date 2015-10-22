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
	String saveFileName = "save.txt";
	File saveFile = new File(saveFileName);
	PrintWriter writer;
	BufferedReader reader;
	
	@Test
	public void testInit() {
		saveFile.delete();
		
		Storage testStorage = Storage.getInstance();
		
		assertEquals(true, saveFile.exists());		
	}
	
	@Test
	public void testReadFile() {
		
	}
	
	@After
	public void cleanUp() {
		saveFile.delete();
	}
}
