package fini.main.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import fini.main.Brain;
import fini.main.model.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class BrainTest {
    Brain brain = Brain.getInstance();
    DisplayControllerStub displayController = new DisplayControllerStub();
    
    @Before
    public void initialize() {
        brain.setDisplayController(displayController);
    }
    
    @Test
    public void testHelp() {
        brain.executeCommand("help");
        assertEquals("displayHelpPanel", displayController.getTestMessage());
    }
    
    @Test
    public void testClear() {
        brain.executeCommand("clear");
        ObservableList<Task> testList = FXCollections.observableArrayList();
        assertEquals(testList, brain.getTaskObservableList());
    }
}
