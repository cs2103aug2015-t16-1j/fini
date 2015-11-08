package fini.main.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import fini.main.model.Command;
import fini.main.model.Command.CommandType;

public class CommandTest {
	
	private static final String PASS = new String("PASS");
	private static final String FAIL = new String("FAIL");

	Command addCommand;
	Command deleteCommand;
	Command deleteInvalidCommand;
	Command updateCommand;
	Command clearCommand;
	Command exitCommand;
	Command undoCommand;
	Command redoCommand;
	Command displayCommand;
	
	int result;
	
	@Before
	public void initialize() {
		addCommand = new Command("add task");
		deleteInvalidCommand = new Command("delete task");
		deleteCommand = new Command("delete 1");
		updateCommand = new Command("update 1");
		clearCommand = new Command("clear");
		exitCommand = new Command("exit");
		undoCommand = new Command("undo");
		redoCommand = new Command("redo");
		displayCommand = new Command("display");
	}
	
	static public String test(int value) {
		if (value == 1)
			return PASS;
		else
			return FAIL;
	}
	
	@Test
	public void testAddCommand() {
		assertEquals("ADD", addCommand.getCommandType().toString());
	}
	
	@Test
	public void testDeleteInvalidCommand() {
		assertEquals("INVALID", deleteInvalidCommand.getCommandType().toString());
	}
	
	@Test
	public void testDeleteCommand() {
		assertEquals("DELETE", deleteCommand.getCommandType().toString());
	}
	
	@Test
	public void testUpdateCommand() {
		assertEquals("UPDATE", updateCommand.getCommandType().toString());
	}
	
	@Test
	public void testClearCommand() {
		assertEquals("CLEAR", clearCommand.getCommandType().toString());
	}
	
	@Test
	public void testExitCommand() {
		assertEquals("EXIT", exitCommand.getCommandType().toString());
	}
	
	@Test
	public void testUndoCommand() {
		assertEquals("UNDO", undoCommand.getCommandType().toString());
	}

	@Test
	public void testRedoCommand() {
		assertEquals("REDO", redoCommand.getCommandType().toString());
	}
	
	@Test
	public void testDisplayCommand() {
		assertEquals("DISPLAY", displayCommand.getCommandType().toString());
	}
	
	@Test
	public void testObjectIndex() {
		Command newCommand = new Command("delete 3");
		assertEquals(CommandType.DELETE, newCommand.getCommandType());
		assertEquals(3, newCommand.getObjectIndex());
		assertEquals("", newCommand.getCommandParameters());
		
		newCommand = new Command("update gai");
		assertEquals(CommandType.INVALID, newCommand.getCommandType());
		assertEquals(-1, newCommand.getObjectIndex());
		assertEquals("", newCommand.getCommandParameters());
		
		newCommand = new Command("update 3 gai");
		assertEquals(CommandType.UPDATE, newCommand.getCommandType());
		assertEquals(3, newCommand.getObjectIndex());
		assertEquals("gai", newCommand.getCommandParameters());
	}
}
