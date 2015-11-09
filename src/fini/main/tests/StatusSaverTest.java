package fini.main.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import fini.main.Brain;
import fini.main.model.FiniParser;
import fini.main.model.StatusSaver;
import fini.main.model.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/*
 * This class tests if the StatusSaver class is functioning properly using a variety
 * of methods.
 * 
 * @@author A0130047W
 */

public class StatusSaverTest {
    
        @Test
        public void testEmptyStack() {
        	Brain brain = Brain.getInstance(); // save initial state
        	StatusSaver statusSaver = StatusSaver.getInstance();
        	assertEquals(true, statusSaver.isUndoMasterStackEmpty());
        	assertEquals(1, statusSaver.getUndoMasterStackSize());
        	assertEquals(true, statusSaver.isRedoMasterStackEmpty());
        	assertEquals(0, statusSaver.getRedoMasterStackSize());
        }
        
        @Test
        public void testStatusSaver() {
        	ArrayList<Task> testMaster = new ArrayList<Task>();
        	ObservableList<Task> testObservable = FXCollections.observableArrayList();
        	
        	StatusSaver statusSaver = StatusSaver.getInstance();
        	
        	Task task1 = createTask("A");
        	Task task2 = createTask("B");
        	Task task3 = createTask("C");
        	
        	testMaster.add(task1);
        	testObservable.add(task1);
        	statusSaver.saveStatus(testMaster, testObservable);
        	assertEquals(false, statusSaver.isUndoMasterStackEmpty());
        	assertEquals(2, statusSaver.getUndoMasterStackSize());
        	
        	testMaster.add(task2);
        	testObservable.add(task2);
        	statusSaver.saveStatus(testMaster, testObservable);
        	assertEquals(3, statusSaver.getUndoMasterStackSize());
        	
        	testMaster.add(task3);
        	testObservable.add(task3);
        	statusSaver.saveStatus(testMaster, testObservable);
        	assertEquals(4, statusSaver.getUndoMasterStackSize());
        	
        	statusSaver.retrieveLastStatus();
        	assertEquals(1, statusSaver.getRedoMasterStackSize());
        	assertEquals(3, statusSaver.getUndoMasterStackSize());
        	assertEquals(2, statusSaver.getLastTaskMasterList().size());
        	assertEquals(task1.getTitle(), statusSaver.getLastTaskMasterList().get(0).getTitle());
        	assertEquals(task2.getTitle(), statusSaver.getLastTaskMasterList().get(1).getTitle());
        	
        	statusSaver.retrieveLastStatus();
        	assertEquals(2, statusSaver.getRedoMasterStackSize());
        	assertEquals(2, statusSaver.getUndoMasterStackSize());
        	assertEquals(1, statusSaver.getLastTaskMasterList().size());
        	assertEquals(task1.getTitle(), statusSaver.getLastTaskMasterList().get(0).getTitle());
        	
        	Task task4 = createTask("D");
        	testMaster.add(task4);
        	testObservable.add(task4);
        	statusSaver.saveStatus(testMaster, testObservable);
        	assertEquals(0, statusSaver.getRedoMasterStackSize());
        	assertEquals(3, statusSaver.getUndoMasterStackSize());
        }
        
        private Task createTask(String input) {
        	FiniParser finiParser = FiniParser.getInstance();
        	finiParser.parse(input);
        	return new Task.TaskBuilder(finiParser.getNotParsed(), finiParser.getIsRecurring()).build();
        }
}
