package fini.main.tests;

import org.junit.Before;
import org.junit.Test;

import fini.main.MainApp;
import fini.main.model.FiniParser;

public class ParserTest {
	private FiniParser parser;
	private String[] dummyArray = null;
	
	public ParserTest() {
		parser = FiniParser.getInstance();
	}
	
	@Before
	public void initialiseFini() {
		MainApp.launch(dummyArray);
	}
	
	@Test
	public void testParser() {
		parser.parse("add task");
	}
}
