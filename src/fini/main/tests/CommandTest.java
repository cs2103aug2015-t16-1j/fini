package fini.main.tests;

import fini.main.model.*;
import fini.main.model.Command.CommandType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

public class CommandTest {
	
	private static final String PASS = new String("PASS");
	private static final String FAIL = new String("FAIL");

	Command addCommand;
	Command deleteCommand;
	Command deleteInvalidCommand;
	Command updateCommand;
	Command clearCommand;
	int result;
	
	
	@Before
	public void initialize() {
		addCommand = new Command("add task");
		deleteInvalidCommand = new Command("delete task");
		deleteCommand = new Command("delete 1");
		updateCommand = new Command("update 1");
		clearCommand = new Command("clear");
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
	

}
