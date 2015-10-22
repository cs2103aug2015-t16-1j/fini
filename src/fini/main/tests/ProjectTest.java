package fini.main.tests;

import fini.main.model.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

// @Author: Jonas

public class ProjectTest {

	Project proj;
	Task testTask;
	private static final String PASS = new String("PASS");
	private static final String FAIL = new String("FAIL");
	
	@Before
	public void initialize() {
		proj = new Project("testProject");
		testTask = new Task("unit test");
	}
	
	
	static public String test(int value) {
		if (value == 1)
			return PASS;
		else
			return FAIL;
	}
	
	@Test
	public void testGetProjectName() {
		assertEquals("testProject", proj.getProjectName());
	}
	
	@Test
	public void testSetProjectName() {
		proj.setProjectName("testProjectTwice");
		assertEquals("testProjectTwice", proj.getProjectName());
	}
	
	@Test
	public void testAddTask() {
		proj.addTaskToProject(testTask);
		assertEquals("unit test", proj.getTasks().get(0).getTitle());
	}
	
	/* Future Test
	@Test
	public void testRemoveTask() {
		int result = 0;
		proj.removeTaskFromProject(testTask);
		if (proj.getTasks().isEmpty())
			result = 1;
		assertEquals("PASS", test(result));
	}
	*/

}
