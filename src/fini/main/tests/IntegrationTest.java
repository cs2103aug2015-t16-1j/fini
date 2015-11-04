package fini.main.tests;

import fini.main.Brain;
import fini.main.model.*;
import fini.main.model.Command.CommandType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

public class IntegrationTest {
	
	private static final String PASS = new String("PASS");
	private static final String FAIL = new String("FAIL");
	
	int result;
	Brain testBrain;
	
	@Before
	public void initialize() {
		testBrain = Brain.getInstance();
	}
	
	static public String test(int value) {
		if (value == 1)
			return PASS;
		else
			return FAIL;
	}
	//TODO
	@Test
	public void testIntegrationDeleteTask() {
		testBrain.executeCommand("delete 5");
		assertEquals("Task not found", testBrain.getRootController().getDisplayToUser().toString() );
	}

}
