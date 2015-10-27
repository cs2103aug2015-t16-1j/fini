package fini.main.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;

import fini.main.model.Task;
import fini.main.model.Task.Priority;
import fini.main.model.Task.TaskBuilder;
import fini.main.model.Task.Type;

import org.junit.Test;

public class TaskTest {
    @Test
    public void testFloatingTask() {
        String taskTitle = "happy";    // 0 to max words/numbers/symbols
        boolean isRecurring = false;        // true/false
        
        String projectName = "Inbox";
        Priority priority = Priority.NORMAL;    // 4 diff priority
        ArrayList<LocalDateTime> dateTimes = new ArrayList<LocalDateTime>();
        LocalDateTime recursUntil = null;       // have / dont have
        Period interval = null;                 // have / dont have
        
        Task testTask = new Task.TaskBuilder(taskTitle, isRecurring).build();
//                .setDatetimes(finiParser.getDatetimes())
//                .setPriority(finiParser.getPriority())
//                .setInterval(finiParser.getInterval())
//                .setRecursUntil(finiParser.getRecursUntil()).build();
        assertEquals("happy", testTask.getTitle());
        assertFalse(testTask.getIsRecurring());
    }
    
    @Test
    public void testFloatingTaskWithprojectAndPriority() {
        String taskTitle = "eat a b c d e i u s ss asd";    // 0 to max words/numbers/symbols
        boolean isRecurring = true;        // true/false
        
        String projectName = "Inbox";
        Priority priority = Priority.NORMAL; 
        
        Task testTask = new Task.TaskBuilder(taskTitle, isRecurring)
                .setPriority(priority)
                .setProjectName(projectName).build();
        assertEquals("", testTask.getTitle());
        assertTrue(isRecurring);
        assertEquals("Inbox", testTask.getProject());
        assertEquals(Priority.NORMAL, testTask.getPriority());
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
