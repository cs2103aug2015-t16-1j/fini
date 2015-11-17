package fini.main.tests;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;

import org.junit.Test;

import fini.main.model.FiniParser;
import fini.main.model.Task.Priority;

/*
 * Testing Parser with various types of commands. The objective of this test class is
 * to ensure that the correct output is obtained from FiniParser.
 */
// @@author A0121828H
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
    public void testAddFloatingTaskWithPriorityAndProject() {
        String userInput = "eat project gai curry priority high chicken";
        
        assertEquals("Parse return message", "FiniParser.parse SUCCESS", parser.parse(userInput));
        assertEquals("Not parsed words", "eat curry chicken", parser.getNotParsed());
        assertEquals("Clean parameters with date info", "eat curry chicken", parser.getCleanParameters());
        
        assertEquals(true, parser.getDatetimes().isEmpty());
        assertEquals(false, parser.getIsRecurring());
        assertEquals(null, parser.getRecursUntil());
        assertEquals("gai", parser.getProjectName());
        assertEquals(Priority.HIGH, parser.getPriority());
        
        userInput = "eat lunch priority wtf";
        parser.parse(userInput);
        assertEquals(Priority.NORMAL, parser.getPriority());
        assertEquals("eat lunch priority wtf", parser.getNotParsed());
        
        userInput = "eat lunch priority";
        parser.parse(userInput);
        assertEquals(Priority.NORMAL, parser.getPriority());
        assertEquals("eat lunch priority", parser.getNotParsed());
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

	// @@author A0130047W
	@Test
	public void testRecur() {
		String userInput = "math tuition tomorrow 2pm repeat";
		parser.parse(userInput);
		assertEquals(LocalDate.now().plusDays(1), parser.getDatetimes().get(0).toLocalDate());
		assertEquals(false, parser.getIsRecurring());
		assertEquals(null, parser.getInterval());
		assertEquals(null, parser.getRecursUntil());
		
		userInput = "math tuition tomorrow 2pm repeat every week";
		parser.parse(userInput);
		assertEquals(LocalDate.now().plusDays(1), parser.getDatetimes().get(0).toLocalDate());
		assertEquals(true, parser.getIsRecurring());
		assertEquals(createDateTime(2015, 11, 13, 14, 00), parser.getDatetimes().get(0));
		assertEquals(Period.ofWeeks(1), parser.getInterval());
		assertEquals(null, parser.getRecursUntil());
		
		userInput = "math tuition tomorrow 2pm repeat everyday";
		parser.parse(userInput);
		assertEquals(LocalDate.now().plusDays(1), parser.getDatetimes().get(0).toLocalDate());
		assertEquals(true, parser.getIsRecurring());
		assertEquals(Period.ofDays(1), parser.getInterval());
		assertEquals(null, parser.getRecursUntil());
		
		userInput = "math tuition tomorrow 2pm repeat every day";
		parser.parse(userInput);
		assertEquals(LocalDate.now().plusDays(1), parser.getDatetimes().get(0).toLocalDate());
		assertEquals(true, parser.getIsRecurring());
		assertEquals(Period.ofDays(1), parser.getInterval());
		assertEquals(null, parser.getRecursUntil());
		
		userInput = "math tuition tomorrow 2pm repeat every two weeks";
		parser.parse(userInput);
		assertEquals(LocalDate.now().plusDays(1), parser.getDatetimes().get(0).toLocalDate());
		assertEquals(true, parser.getIsRecurring());
		assertEquals(Period.ofWeeks(2), parser.getInterval());
		assertEquals(null, parser.getRecursUntil());
		
		userInput = "math tuition tomorrow 2pm repeat every two week";
		parser.parse(userInput);
		assertEquals(LocalDate.now().plusDays(1), parser.getDatetimes().get(0).toLocalDate());
		assertEquals(true, parser.getIsRecurring());
		assertEquals(Period.ofDays(1), parser.getInterval());
		assertEquals(null, parser.getRecursUntil());
		
		userInput = "math tuition repeat every week";
		parser.parse(userInput);
		assertEquals(0, parser.getDatetimes().size());
		assertEquals(false, parser.getIsRecurring());
		assertEquals(null, parser.getInterval());
		assertEquals(null, parser.getRecursUntil());
		
		userInput = "math tuition repeat every week until dec";
		parser.parse(userInput);
		assertEquals(false, parser.getIsRecurring());
		assertEquals(null, parser.getInterval());
		assertEquals(null, parser.getRecursUntil());
		
		userInput = "math tuition tomorrow 2pm repeat every week until dec";
		parser.parse(userInput);
		assertEquals(true, parser.getIsRecurring());
		assertEquals(Period.ofWeeks(1), parser.getInterval());
		assertEquals(createDate(2015, 12, 1), parser.getRecursUntil().toLocalDate());
		
		userInput = "math tuition tomorrow 2pm repeat every two weeks until dec";
		parser.parse(userInput);
		assertEquals(true, parser.getIsRecurring());
		assertEquals(Period.ofWeeks(2), parser.getInterval());
		assertEquals(createDate(2015, 12, 1), parser.getRecursUntil().toLocalDate());
		
		userInput = "math tuition tomorrow 2pm repeat everyday until dec";
		parser.parse(userInput);
		assertEquals(true, parser.getIsRecurring());
		assertEquals(Period.ofDays(1), parser.getInterval());
		assertEquals(createDate(2015, 12, 1), parser.getRecursUntil().toLocalDate());
		
		userInput = "math tuition tomorrow 2pm repeat every sun until dec";
		parser.parse(userInput);
		assertEquals(true, parser.getIsRecurring());
		assertEquals(Period.ofDays(1), parser.getInterval());
		assertEquals(createDate(2015, 12, 1), parser.getRecursUntil().toLocalDate());
		
		userInput = "math tuition tomorrow 2pm repeat until dec";
		parser.parse(userInput);
		assertEquals(true, parser.getIsRecurring());
		assertEquals(Period.ofDays(1), parser.getInterval());
		assertEquals(null, parser.getRecursUntil());
		
		userInput = "math tuition every two weeks until";
		parser.parse(userInput);
		assertEquals(false, parser.getIsRecurring());
		assertEquals(null, parser.getInterval());
		assertEquals(null, parser.getRecursUntil());
		
		userInput = "math tuition every week";
		parser.parse(userInput);
		assertEquals(false, parser.getIsRecurring());
		assertEquals(null, parser.getInterval());
		assertEquals(null, parser.getRecursUntil());

		userInput = "math tuition every mon 2pm";
		parser.parse(userInput);
		assertEquals(false, parser.getIsRecurring());
		assertEquals(null, parser.getInterval());
		assertEquals(null, parser.getRecursUntil());
		
		userInput = "math tuition every week until dec";
		parser.parse(userInput);
		assertEquals(false, parser.getIsRecurring());
		assertEquals(null, parser.getInterval());
		assertEquals(null, parser.getRecursUntil());
		
		userInput = "math tuition every two weeks until dec";
		parser.parse(userInput);
		assertEquals(false, parser.getIsRecurring());
		assertEquals(null, parser.getInterval());
		assertEquals(null, parser.getRecursUntil());
		
		userInput = "math tuition every mon to fri 2pm";
		parser.parse(userInput);
		assertEquals(false, parser.getIsRecurring());
		assertEquals(null, parser.getInterval());
		assertEquals(null, parser.getRecursUntil());
		
		userInput = "math tuition repeat every 12th";
		assertEquals(false, parser.getIsRecurring());
		assertEquals(null, parser.getInterval());
		assertEquals(null, parser.getRecursUntil());
	}
	
	private LocalDate createDate(int year, int month, int day) {
		return LocalDate.of(year, month, day);
	}
	
    private LocalDateTime createDateTime(int year, int month, int day, int hour, int minute) {
        return LocalDateTime.of(year, month, day, hour, minute);
    }
}
