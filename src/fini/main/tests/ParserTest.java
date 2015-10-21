package fini.main.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import fini.main.MainApp;
import fini.main.model.FiniParser;
import fini.main.model.FiniParser.CommandType;
import fini.main.model.Task.Priority;

public class ParserTest {
	private FiniParser parser = new FiniParser();
	//private String[] dummyArray = null;
	
//	public ParserTest() {
//		parser = FiniParser.getInstance();
//	}
	
	@Test
	public void testCleaning() {
	    String userInput = "      add eat          curry  chicken";
	    assertEquals("add eat curry chicken", parser.getSimpleCleanString(userInput));
	}

	@Test
    public void testAddFloat() {
        String userInput = "add eat curry chicken";
        parser.parse(userInput);

        assertEquals("add eat curry chicken", parser.getStoredUserInput());
        assertEquals(CommandType.ADD, parser.getCommandType());
        assertEquals("eat curry chicken", parser.getCommandParameters());
        assertEquals("eat curry chicken", parser.getCleanParameters());
        assertEquals(Priority.NORMAL, parser.getPriority());
        //assertEquals("eat curry chicken", parser.getProjectName());
        //assertEquals(null, parser.getDates());
        assertEquals(false, parser.getIsRecurring());
        assertEquals("", parser.getNotParsed());
        assertEquals(null, parser.getRecursUntil());
	}
	
	@Test
    public void testAddDeadline() {
        
    }
	
	@Test
	public void testAddEvent() {
	    
	}
	
	@Test
    public void testAddRecurDeadline() {
        
    }
	
	@Test
    public void testAddRecurEvent() {
        
    }
}
