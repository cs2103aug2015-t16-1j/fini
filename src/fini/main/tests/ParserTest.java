package fini.main.tests;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.Before;
import org.junit.Test;

import fini.main.MainApp;
import fini.main.model.FiniParser;
import fini.main.model.Task.Priority;

public class ParserTest {
	private FiniParser parser = FiniParser.getInstance();

	@Test
	public void testNotParsed() {
	    String userInput = "eat          curry  chicken";
	    parser.parse(userInput);
	    assertEquals("eat curry chicken", parser.getNotParsed());
	}
	
	@Test
	public void testClean() {
		String userInput = "eat curry chicken tomorrow morning with gaieepo priority high project funny";
		parser.parse(userInput);
		assertEquals("Clean parameters with date info", "eat curry chicken tomorrow morning with gaieepo", parser.getCleanParameters());
	}

	@Test
    public void testAddFloatWithPriorityAndProject() {
        String userInput = "eat project gai curry priority high chicken";
        
        assertEquals("Parse return message", "FiniParser.parse SUCCESS", parser.parse(userInput));
        assertEquals("Not parsed words", "eat curry chicken", parser.getNotParsed());
        assertEquals("Clean parameters with date info", "eat curry chicken", parser.getCleanParameters());
        
        assertEquals(true, parser.getDatetimes().isEmpty());
        assertEquals(false, parser.getIsRecurring());
        assertEquals(null, parser.getRecursUntil());
        assertEquals("gai", parser.getProjectName());
        assertEquals(Priority.HIGH, parser.getPriority());
	}
	
	@Test
	public void testAddDeadline() {
	    String userInput = "study 3/15/2016";
        assertEquals("FiniParser.parse SUCCESS", parser.parse(userInput));
        assertEquals(1, parser.getDatetimes().size());
        assertEquals(LocalDateTime.of(LocalDate.of(2016, 3, 15), LocalTime.MAX), parser.getDatetimes().get(0));
        assertEquals("study", parser.getNotParsed());
        assertEquals("study 3/15/2016", parser.getCleanParameters());
        assertEquals(Priority.NORMAL, parser.getPriority());
        assertEquals("Inbox", parser.getProjectName());
        assertEquals(false, parser.getIsRecurring());
        assertEquals(null, parser.getRecursUntil());
	}

	@Test
    public void testAddEvent() {
	    String userInput = "meeting from the day after the second thur of dec to christmas";

        assertEquals("FiniParser.parse SUCCESS", parser.parse(userInput));
        assertEquals("meeting", parser.getNotParsed());
        assertEquals("meeting from the day after the second thur of dec to christmas", parser.getCleanParameters());
        assertEquals(Priority.NORMAL, parser.getPriority());
        assertEquals("Inbox", parser.getProjectName());
        assertEquals(2, parser.getDatetimes().size());
        assertEquals(createDate(2015, 12, 11), parser.getDatetimes().get(0).toLocalDate());
        assertEquals(createDate(2015, 12, 25), parser.getDatetimes().get(1).toLocalDate());
        assertEquals(false, parser.getIsRecurring());
        assertEquals(null, parser.getRecursUntil());
    }

	@Test
	public void testRecur() {
//		String userInput = "math tuition tomorrow 2pm";
	}
	
	private LocalDate createDate(int year, int month, int day) {
		return LocalDate.of(year, month, day);
	}
	
    private LocalDateTime createDateTime(int year, int month, int day, int hour, int minute) {
        return LocalDateTime.of(year, month, day, hour, minute);
    }
}
