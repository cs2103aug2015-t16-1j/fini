package fini.main.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;

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
        boolean isRecurring = false;

        String projectName = "School CCA";
        Priority priority = Priority.HIGH;

        Task testTask = new Task.TaskBuilder(taskTitle, isRecurring)
                .setPriority(priority)
                .setProjectName(projectName).build();

        assertEquals("eat a b c d e i u s ss asd", testTask.getTitle());
        assertFalse(isRecurring);
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
        assertEquals(null, testTask.getRecurUniqueID());
    }
    
    @Test
    public void testDeadlineTask() {
        String taskTitle = "123E Dr";
        boolean isRecurring = false;

        Priority priority = Priority.LOW;
        ArrayList<LocalDateTime> dateTimes = new ArrayList<LocalDateTime>();
        LocalDateTime date = LocalDateTime.of(2015, 12, 10, 9, 56);
        dateTimes.add(date);
        
        Task testTask = new Task.TaskBuilder(taskTitle, isRecurring)
                .setPriority(priority)
                .setDatetimes(dateTimes).build();

        assertEquals("123E Dr", testTask.getTitle());
        assertFalse(isRecurring);
        assertEquals("Inbox", testTask.getProject());
        assertEquals(Priority.LOW, testTask.getPriority());

        assertEquals(LocalDateTime.of(2015, 12, 10, 9, 56), testTask.getStartDateTime());
        assertEquals(null, testTask.getEndDateTime());
        assertEquals(null, testTask.getRecursUntil());
        assertEquals(null, testTask.getInterval());

        assertFalse(testTask.getIsCompleted());
        assertEquals(Type.DEADLINE, testTask.getTaskType());

        // TODO how to test objectID???
        // assertEquals(null, testTask.getObjectID());
        assertEquals(null, testTask.getRecurUniqueID());
    }
    
    @Test
    public void testDeadlineTaskRecurr() {
        String taskTitle = "1 23";
        boolean isRecurring = true;

        Priority priority = Priority.MEDIUM;
        ArrayList<LocalDateTime> dateTimes = new ArrayList<LocalDateTime>();
        LocalDateTime date = LocalDateTime.of(2016, 12, 10, 21, 56);
        dateTimes.add(date);
        Period interval = Period.ofDays(1);
        LocalDateTime recursUntil = LocalDateTime.of(2016, 12, 20, 21, 56);
        
        // TODO what will happen if I set recursUntil same as/before deadline date
        Task testTask = new Task.TaskBuilder(taskTitle, isRecurring)
                .setPriority(priority)
                .setDatetimes(dateTimes)
                .setInterval(interval)
                .setRecursUntil(recursUntil).build();

        assertEquals("1 23", testTask.getTitle());
        assertTrue(isRecurring);
        assertEquals("Inbox", testTask.getProject());
        assertEquals(Priority.MEDIUM, testTask.getPriority());

        assertEquals(LocalDateTime.of(2015, 12, 10, 21, 56), testTask.getStartDateTime());
        assertEquals(null, testTask.getEndDateTime());
        assertEquals(LocalDateTime.of(2016, 12, 20, 21, 56), testTask.getRecursUntil());
        assertEquals(Period.ofDays(1), testTask.getInterval());

        assertFalse(testTask.getIsCompleted());
        assertEquals(Type.DEADLINE, testTask.getTaskType());

        // TODO how to test objectID???
        // assertEquals(null, testTask.getObjectID());
        // TODO how to test recurID if isRecurring = true
        // assertEquals(null, testTask.getRecurUniqueID());
    }
    
    
    // @Test
    // public void testFloatTask() {
    // String notParsed = "attend meeting";
    // ArrayList<LocalDateTime> datetimes = null;
    // Priority priority = Task.Priority.NORMAL;
    // String projectName = "school";
    // boolean isRecurring = false;
    // LocalDateTime recursUntil = null;
    //
    // Task testingTask = new Task(notParsed, datetimes, priority,
    // projectName, isRecurring, recursUntil);

    // Testing type and the info
    // assertEquals(Task.Type.DEFAULT, testingTask.getTaskType());
    // assertEquals("attend meeting", testingTask.getTitle());
    // assertEquals("school", testingTask.getProject());
    // assertEquals(Task.Priority.NORMAL, testingTask.getPriority());

    // Testing LocalDateTime
    // assertEquals(null, testingTask.getStartDate());
    // assertEquals(null, testingTask.getEndDate());
    // assertEquals(null, testingTask.getStartTime());
    // assertEquals(null, testingTask.getEndTime());
    // assertEquals(null, testingTask.getRecursUntil());

    // Testing other attributes
    // assertFalse(testingTask.isCompleted());
    // assertFalse(testingTask.getIsOverdue());
    // assertFalse(testingTask.getIsRecurring());
    // }
}
