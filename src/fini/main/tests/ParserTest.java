package fini.main.tests;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
	private LocalDate createDate(int year, int month, int day) {
        return LocalDate.of(year, month, day);
    }

    private LocalDateTime createDateTime(int year, int month, int day, int hour, int minute) {
        return LocalDateTime.of(year, month, day, hour, minute);
    }
	
	
	@Test
	public void testCleaning() {
	    String userInput = "      add eat          curry  chicken";
	    assertEquals("add eat curry chicken", parser.getSimpleCleanString(userInput));
	}

	@Test
    public void testAddFloat() {
        String userInput = "add eat curry chicken";
        
        assertEquals("SUCCESS", parser.parse(userInput));
        assertEquals("add eat curry chicken", parser.getStoredUserInput());
        assertEquals(CommandType.ADD, parser.getCommandType());
        assertEquals("eat curry chicken", parser.getCommandParameters());
        assertEquals("eat curry chicken", parser.getCleanParameters());
        assertEquals(Priority.NORMAL, parser.getPriority());
        //assertEquals(null, parser.getProjectName());
        //assertEquals(null, parser.getDatetimes());
        //assertEquals(false, parser.getIsRecurring());
        assertEquals("eat curry chicken", parser.getNotParsed());
        //assertEquals(null, parser.getRecursUntil());
	}
	@Test
	public void testAddFloatWithProject() {}
	@Test
    public void testAddFloatWithPriority() {
	    String userInput = "add new task with priority high";
        
        assertEquals("SUCCESS", parser.parse(userInput));
        assertEquals("add new task with priority high", parser.getStoredUserInput());
        assertEquals(CommandType.ADD, parser.getCommandType());
        assertEquals("new task with priority high", parser.getCommandParameters());
        assertEquals("new task with priority high", parser.getCleanParameters());
        assertEquals(Priority.HIGH, parser.getPriority());
        assertEquals("TestCase", parser.getProjectName());
        //assertEquals(null, parser.getDatetimes());
        //assertEquals(false, parser.getIsRecurring());
        assertEquals("new task", parser.getNotParsed());
        //assertEquals(null, parser.getRecursUntil());
    }
	
	@Test
	public void testAddDeadline() {
	    String userInput = "add study cs2103t 15/3/2016";

        assertEquals("Parse Successful", parser.parse(userInput));
        assertEquals("add study cs2103t 15/3/2016", parser.getStoredUserInput());
        assertEquals(CommandType.ADD, parser.getCommandType());
        assertEquals("study cs2103t 15/3/2016", parser.getCommandParameters());
        assertEquals("study cs2103t 15/3/2016", parser.getCleanParameters());
        assertEquals(Priority.NORMAL, parser.getPriority());
        //assertEquals(null, parser.getProjectName());
        assertEquals(1, parser.getDatetimes().size());
        assertEquals(createDate(2016, 3, 15),
                     parser.getDatetimes().get(0).toLocalDate());
        //assertEquals(false, parser.getIsRecurring());
        assertEquals("study cs2103t", parser.getNotParsed());
        //assertEquals(null, parser.getRecursUntil());
	}
	
	@Test
    public void testAddEvent() {
	    String userInput = "add meeting from the day after the second thur of dec to christmas";

        assertEquals("Parse Successful", parser.parse(userInput));
        assertEquals("add meeting from the day after the second thur of dec to christmas", parser.getStoredUserInput());
        assertEquals(CommandType.ADD, parser.getCommandType());
        assertEquals("meeting from the day after the second thur of dec to christmas", parser.getCommandParameters());
        assertEquals("meeting from the day after the second thur of dec to christmas", parser.getCleanParameters());
        assertEquals(Priority.NORMAL, parser.getPriority());
        //assertEquals(null, parser.getProjectName());
        assertEquals(2, parser.getDatetimes().size());
        assertEquals(createDate(2015, 12, 11), parser.getDatetimes().get(0));
        assertEquals(createDate(2015, 12, 25), parser.getDatetimes().get(1));
        //assertEquals(false, parser.getIsRecurring());
        assertEquals("meeting", parser.getNotParsed());
        //assertEquals(null, parser.getRecursUntil());
    }
	
	@Test
    public void testAddRecurDeadline() {
        
    }
	
	@Test
    public void testAddRecurEvent() {
        
    }
}
