package fini.main.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.time.LocalDateTime;
import java.util.ArrayList;

import fini.main.model.Task;
import fini.main.model.Task.Priority;
import fini.main.model.Task.Type;

import org.junit.Test;

public class TaskTest {
    
    @Test
    public void testFloatTask() {
        String notParsed = "attend meeting";
        ArrayList<LocalDateTime> datetimes  = null;
        Priority priority = Task.Priority.NORMAL;
        String projectName = "school";
        boolean isRecurring = false;
        LocalDateTime recursUntil = null;
        
        Task testingTask = new Task(notParsed, datetimes, priority, 
                                    projectName, isRecurring, recursUntil);
        
        // Testing type and the info
        assertEquals(Task.Type.DEFAULT, testingTask.getTaskType());
        assertEquals("attend meeting", testingTask.getTitle());
        assertEquals("school", testingTask.getProject());
        assertEquals(Task.Priority.NORMAL, testingTask.getPriority());
        
        // Testing LocalDateTime 
        assertEquals(null, testingTask.getStartDate());
        assertEquals(null, testingTask.getEndDate());
        assertEquals(null, testingTask.getStartTime());
        assertEquals(null, testingTask.getEndTime());
        assertEquals(null, testingTask.getRecursUntil());
        
        // Testing other attributes
        assertFalse(testingTask.isCompleted());
        //assertFalse(testingTask.getIsOverdue());
        //assertFalse(testingTask.getIsRecurring());
    }
}
