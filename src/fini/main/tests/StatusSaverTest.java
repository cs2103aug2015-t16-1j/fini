package fini.main.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import fini.main.model.StatusSaver;
import fini.main.model.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class StatusSaverTest {
    
        @Test
        public void TestWithThreeStatus() {
            ArrayList<Task> listArr = new ArrayList<Task>();
            ObservableList<Task> listObsv = FXCollections.observableArrayList();
            
            Task taskA = new Task("First");
            Task taskB = new Task("Second");
            Task taskC = new Task("Third");
            
            listArr.add(taskA);
            listArr.add(taskB);
            listArr.add(taskC);
            
            listObsv.add(taskA);
            listObsv.add(taskB);
            listObsv.add(taskC);
            
            StatusSaver testingSaver = StatusSaver.getInstance();
            // save status1 with all three tasks 
            testingSaver.saveStatus(listArr, listObsv);
            assertFalse(testingSaver.isMasterStackEmpty());
            listArr.remove(2);
            listObsv.remove(2);
            
            // save status2 with taskC removed, only taskA and taskB left
            testingSaver.saveStatus(listArr, listObsv);
            assertFalse(testingSaver.isMasterStackEmpty());
            // retrieve status 2 
            testingSaver.retrieveLastStatus();
            assertFalse(testingSaver.isMasterStackEmpty());
            // test status 2
            assertEquals(listArr.size(), testingSaver.getLastTaskMasterList().size());
            assertEquals(listObsv.size(), testingSaver.getLastTaskObservableList().size());
            assertEquals(listArr.get(0).getTitle(), testingSaver.getLastTaskMasterList().get(0).getTitle());
            assertEquals(listObsv.get(0).getTitle(), testingSaver.getLastTaskObservableList().get(0).getTitle());
            assertEquals(listArr.get(1).getTitle(), testingSaver.getLastTaskMasterList().get(1).getTitle());
            assertEquals(listObsv.get(1).getTitle(), testingSaver.getLastTaskObservableList().get(1).getTitle());
            
            // retrieve status 1
            testingSaver.retrieveLastStatus();
            assertTrue(testingSaver.isMasterStackEmpty());
            // change the testing lists back to status 1 also
            listArr.add(taskC);
            listObsv.add(taskC);
            // test status 1 against testing lists
            assertEquals(listArr.size(), testingSaver.getLastTaskMasterList().size());
            assertEquals(listObsv.size(), testingSaver.getLastTaskObservableList().size());
            assertEquals(listArr.get(0).getTitle(), testingSaver.getLastTaskMasterList().get(0).getTitle());
            assertEquals(listObsv.get(0).getTitle(), testingSaver.getLastTaskObservableList().get(0).getTitle());
            assertEquals(listArr.get(1).getTitle(), testingSaver.getLastTaskMasterList().get(1).getTitle());
            assertEquals(listObsv.get(1).getTitle(), testingSaver.getLastTaskObservableList().get(1).getTitle());
            assertEquals(listArr.get(2).getTitle(), testingSaver.getLastTaskMasterList().get(2).getTitle());
            assertEquals(listObsv.get(2).getTitle(), testingSaver.getLastTaskObservableList().get(2).getTitle());
            // TODO change all getTitle() to toString(). create toString() in task to override
        }
}
