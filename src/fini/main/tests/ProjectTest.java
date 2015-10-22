package fini.main.tests;

import fini.main.model.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;


public class ProjectTest {

	Project proj;
	Task testTask;
	
	@Before
	public void initialize() {
		proj = new Project("testProject");
		testTask = new Task("unit test");
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

}
