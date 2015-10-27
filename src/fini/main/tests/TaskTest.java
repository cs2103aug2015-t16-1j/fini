package fini.main.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import fini.main.model.Task;
import fini.main.model.Task.Priority;
import fini.main.model.Task.Type;

import org.junit.Test;

public class TaskTest {
    // title: 0 to max words/numbers/symbols
    // isRecurring: true/false
    // projectName: same as title
    // priority: 4 types
    // dateTimes:
    // recursUntil: have/dont have
    // interval: have/dont have
    
    @Test
    public void testFloatingTask() {
        String taskTitle = "happy";    
        boolean isRecurring = false;        
        
        Task testTask = new Task.TaskBuilder(taskTitle, isRecurring).build();

        assertEquals("happy", testTask.getTitle());
        assertFalse(testTask.getIsRecurring());
       
        assertEquals("Inbox", testTask.getProject());
        assertEquals(Priority.NORMAL, testTask.getPriority());
        assertEquals(null, testTask.getStartDateTime());
        assertEquals(null, testTask.getEndDateTime());
        assertEquals(null, testTask.getRecursUntil());
        assertEquals(null, testTask.getInterval());
        
        assertFalse(testTask.getIsCompleted());
        assertEquals(Type.DEFAULT, testTask.getTaskType());
        
        // TODO how to test objectID???
        // assertEquals(null, testTask.getObjectID());
        assertEquals(null, testTask.getRecurUniqueID());
    }
    
    @Test
    public void testFloatingTaskWithprojectAndPriority() {
        String taskTitle = "eat a b c d e i u s ss asd";   
        boolean isRecurring = true;        
        
        String projectName = "School CCA";
        Priority priority = Priority.HIGH; 
        
        Task testTask = new Task.TaskBuilder(taskTitle, isRecurring)
                .setPriority(priority)
                .setProjectName(projectName).build();
        
        assertEquals("eat a b c d e i u s ss asd", testTask.getTitle());
        assertTrue(isRecurring);
        assertEquals("School CCA", testTask.getProject());
        assertEquals(Priority.HIGH, testTask.getPriority());
        
        assertEquals(null, testTask.getStartDateTime());
        assertEquals(null, testTask.getEndDateTime());
        assertEquals(null, testTask.getRecursUntil());
        assertEquals(null, testTask.getInterval());
        
        assertFalse(testTask.getIsCompleted());
        assertEquals(Type.DEFAULT, testTask.getTaskType());
        
        // TODO how to test objectID???
        // assertEquals(null, testTask.getObjectID());
        // TODO howo to test recurID when isRecurring = true
        // assertEquals(null, testTask.getRecurUniqueID());
    }
//    @Test
//    public void testFloatTask() {
//        String notParsed = "attend meeting";
//        ArrayList<LocalDateTime> datetimes  = null;
//        Priority priority = Task.Priority.NORMAL;
//        String projectName = "school";
//        boolean isRecurring = false;
//        LocalDateTime recursUntil = null;
//        
//        Task testingTask = new Task(notParsed, datetimes, priority, 
//                                    projectName, isRecurring, recursUntil);
        
        // Testing type and the info
//        assertEquals(Task.Type.DEFAULT, testingTask.getTaskType());
//        assertEquals("attend meeting", testingTask.getTitle());
//        assertEquals("school", testingTask.getProject());
//        assertEquals(Task.Priority.NORMAL, testingTask.getPriority());
        
        // Testing LocalDateTime 
//        assertEquals(null, testingTask.getStartDate());
//        assertEquals(null, testingTask.getEndDate());
//        assertEquals(null, testingTask.getStartTime());
//        assertEquals(null, testingTask.getEndTime());
//        assertEquals(null, testingTask.getRecursUntil());
        
        // Testing other attributes
//        assertFalse(testingTask.isCompleted());
        //assertFalse(testingTask.getIsOverdue());
        //assertFalse(testingTask.getIsRecurring());
//    }
}
