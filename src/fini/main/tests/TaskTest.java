package fini.main.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
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
    // dateTimes: 0,1,2 (shouldnt have size() > 2)
    // recursUntil: have/dont have
    // interval: have/dont have

    @Test
    public void testFloatingTaskAndToStringMethod() {
        String taskTitle = "happy";
        boolean isRecurring = false;

        Task testTask = new Task.TaskBuilder(taskTitle, isRecurring).build();

        assertEquals("happy", testTask.getTitle());
        assertFalse(testTask.isRecurring());

        assertEquals("Inbox", testTask.getProject());
        assertEquals(Priority.NORMAL, testTask.getPriority());
        assertEquals(null, testTask.getStartDateTime());
        assertEquals(null, testTask.getEndDateTime());
        assertEquals(null, testTask.getRecursUntil());
        assertEquals(null, testTask.getInterval());

        assertFalse(testTask.isCompleted());
        assertEquals(Type.DEFAULT, testTask.getTaskType());

        // TODO how to test objectID???
        // assertEquals(null, testTask.getObjectID());
        assertEquals(null, testTask.getRecurUniqueID());
        assertFalse(testTask.hasRecurUniqueID());
        
        String expectedResult = ">>>>>>>>>\n" + 
                "happy" + "\n" + 
                "false" + "\n" + 
                "NORMAL" + "\n" + 
                "Null" + "\n" +
                "Null" + "\n" +
                "Null" + "\n" +
                "Null" + "\n" +
                "false" + "\n" +
                "DEFAULT" + "\n" +
//                "objectID" + "\n" +  // TODO ID cannot test
//                "Null" + "\n" +
                "<<<<<<<<<";
        assertEquals(expectedResult, testTask.toString());
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
        assertFalse(testTask.isRecurring());
        assertEquals("School CCA", testTask.getProject());
        assertEquals(Priority.HIGH, testTask.getPriority());

        assertEquals(null, testTask.getStartDateTime());
        assertEquals(null, testTask.getEndDateTime());
        assertEquals(null, testTask.getRecursUntil());
        assertEquals(null, testTask.getInterval());

        assertFalse(testTask.isCompleted());
        assertEquals(Type.DEFAULT, testTask.getTaskType());

        // TODO how to test objectID???
        // assertEquals(null, testTask.getObjectID());
        assertEquals(null, testTask.getRecurUniqueID());
        assertFalse(testTask.hasRecurUniqueID());
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
        assertFalse(testTask.isRecurring());
        assertEquals("Inbox", testTask.getProject());
        assertEquals(Priority.LOW, testTask.getPriority());

        assertEquals(LocalDateTime.of(2015, 12, 10, 9, 56), testTask.getStartDateTime());
        assertEquals(null, testTask.getEndDateTime());
        assertEquals(null, testTask.getRecursUntil());
        assertEquals(null, testTask.getInterval());

        assertFalse(testTask.isCompleted());
        assertEquals(Type.DEADLINE, testTask.getTaskType());

        // TODO how to test objectID???
        // assertEquals(null, testTask.getObjectID());
        assertEquals(null, testTask.getRecurUniqueID());
        assertFalse(testTask.hasRecurUniqueID());
    }
    
    @Test
    public void testDeadlineTaskRecurr() {
        String taskTitle = "1 23";
        boolean isRecurring = true;

        Priority priority = Priority.MEDIUM;
        ArrayList<LocalDateTime> dateTimes = new ArrayList<LocalDateTime>();
        LocalDateTime date = LocalDateTime.of(2015, 12, 10, 21, 56);
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
        assertTrue(testTask.isRecurring());
        assertEquals("Inbox", testTask.getProject());
        assertEquals(Priority.MEDIUM, testTask.getPriority());

        assertEquals(LocalDateTime.of(2015, 12, 10, 21, 56), testTask.getStartDateTime());
        assertEquals(null, testTask.getEndDateTime());
        assertEquals(LocalDateTime.of(2016, 12, 20, 21, 56), testTask.getRecursUntil());
        assertEquals(Period.ofDays(1), testTask.getInterval());

        assertFalse(testTask.isCompleted());
        assertEquals(Type.DEADLINE, testTask.getTaskType());

        // TODO how to test objectID???
        // assertEquals(null, testTask.getObjectID());
        // TODO how to test recurID if isRecurring = true
        // assertEquals(null, testTask.getRecurUniqueID());
        assertTrue(testTask.hasRecurUniqueID());
    }
    
    @Test
    public void testEventTask() {
        String taskTitle = "21238013291038102928319032";
        boolean isRecurring = false;
        // TODO what will happen if I set endDate before startDate
        ArrayList<LocalDateTime> dateTimes = new ArrayList<LocalDateTime>();
        LocalDateTime dateStart = LocalDateTime.of(2015, 12, 10, 9, 56);
        LocalDateTime dateEnd = LocalDateTime.of(2016, 2, 10, 0, 00);
        dateTimes.add(dateStart);
        dateTimes.add(dateEnd);
        
        Task testTask = new Task.TaskBuilder(taskTitle, isRecurring)
                .setDatetimes(dateTimes).build();

        assertEquals("21238013291038102928319032", testTask.getTitle());
        assertFalse(testTask.isRecurring());
        assertEquals("Inbox", testTask.getProject());
        assertEquals(Priority.NORMAL, testTask.getPriority());

        assertEquals(LocalDateTime.of(2015, 12, 10, 9, 56), testTask.getStartDateTime());
        assertEquals(LocalDateTime.of(2016, 2, 10, 0, 00), testTask.getEndDateTime());
        assertEquals(null, testTask.getRecursUntil());
        assertEquals(null, testTask.getInterval());

        assertFalse(testTask.isCompleted());
        assertEquals(Type.EVENT, testTask.getTaskType());

        // TODO how to test objectID???
        // assertEquals(null, testTask.getObjectID());
        assertFalse(testTask.hasRecurUniqueID());
        assertEquals(null, testTask.getRecurUniqueID());
    }
  
    @Test
    public void testEventTaskRecurAndToStringMethod() {
        String taskTitle = "i am a long long long long long long long name";
        boolean isRecurring = true;
        // TODO what will happen if I set endDate before startDate
        ArrayList<LocalDateTime> dateTimes = new ArrayList<LocalDateTime>();
        LocalDateTime dateStart = LocalDateTime.of(2015, 12, 10, 9, 56);
        LocalDateTime dateEnd = LocalDateTime.of(2015, 12, 10, 11, 56);
        dateTimes.add(dateStart);
        dateTimes.add(dateEnd);
        
        LocalDateTime recursUntil = LocalDateTime.of(2016, 5, 5, 11, 56);
        Period interval = Period.ofWeeks(2);
                
        Task testTask = new Task.TaskBuilder(taskTitle, isRecurring)
                .setDatetimes(dateTimes)
                .setRecursUntil(recursUntil)
                .setInterval(interval).build();

        assertEquals("i am a long long long long long long long name", testTask.getTitle());
        assertTrue(testTask.isRecurring());
        assertEquals("Inbox", testTask.getProject());
        assertEquals(Priority.NORMAL, testTask.getPriority());

        assertEquals(LocalDateTime.of(2015, 12, 10, 9, 56), testTask.getStartDateTime());
        assertEquals(LocalDateTime.of(2015, 12, 10, 11, 56), testTask.getEndDateTime());
        assertEquals(LocalDateTime.of(2016, 5, 5, 11, 56), testTask.getRecursUntil());
        assertEquals(Period.ofWeeks(2), testTask.getInterval());

        assertFalse(testTask.isCompleted());
        assertEquals(Type.EVENT, testTask.getTaskType());

        // TODO how to test objectID???
        // assertEquals(null, testTask.getObjectID());
        assertTrue(testTask.hasRecurUniqueID());
        // assertEquals(null, testTask.getRecurUniqueID());

        
        String expectedResult = ">>>>>>>>>\n" + 
                "i am a long long long long long long long name" + "\n" + 
                "true" + "\n" + 
                "NORMAL" + "\n" + 
                "2015-12-10T09:56" + "\n" +
                "2015-12-10T11:56" + "\n" +
                "2016-05-05T11:56"+ "\n" +
                interval.toString() + "\n" +
                "false" + "\n" +
                "EVENT" + "\n" +
//                "objectID" + "\n" +
//                "recurUniqueID" + "\n" +  // TODO cannot test IDs for toString
                "<<<<<<<<<";
        assertEquals(expectedResult, testTask.toString());
    }
    
    @Test
    public void testIsOverdueMethod() {
        // TODO
//        String taskTitle = "0";
//        boolean isRecurring = false;
//
//        Task testTask = new Task.TaskBuilder(taskTitle, isRecurring).build();
    }
    
    @Test
    public void testMakeCopyMethod() {
        String taskTitle = "happy";
        boolean isRecurring = false;

        Task testTaskOriginal = new Task.TaskBuilder(taskTitle, isRecurring).build();

        Task testTaskCopied = testTaskOriginal.makeCopy();

        // Check that the copied task has the same properties as the original
        assertEquals(testTaskOriginal.getTitle(), testTaskCopied.getTitle());
        assertEquals(testTaskOriginal.isRecurring(), testTaskCopied.isRecurring());

        assertEquals(testTaskOriginal.getProject(), testTaskCopied.getProject());
        assertEquals(testTaskOriginal.getPriority(), testTaskCopied.getPriority());
        assertEquals(testTaskOriginal.getStartDateTime(), testTaskCopied.getStartDateTime());
        assertEquals(testTaskOriginal.getEndDateTime(), testTaskCopied.getEndDateTime());
        assertEquals(testTaskOriginal.getRecursUntil(), testTaskCopied.getRecursUntil());
        assertEquals(testTaskOriginal.getInterval(), testTaskCopied.getInterval());

        assertEquals(testTaskOriginal.isCompleted(), testTaskCopied.isCompleted());
        assertEquals(testTaskOriginal.getTaskType(), testTaskCopied.getTaskType());

        assertNotEquals(testTaskOriginal.getObjectID(), testTaskCopied.getObjectID());
        assertEquals(testTaskOriginal.getRecursUntil(), testTaskCopied.getRecurUniqueID());
        assertEquals(testTaskOriginal.hasRecurUniqueID(), testTaskCopied.hasRecurUniqueID());

        // Check that when we change copied task, it doesnt affect original one
        testTaskCopied.setTaskTitle("gggggg");
        assertNotEquals(testTaskOriginal.getTitle(), testTaskCopied.getTitle());
        assertEquals("gggggg", testTaskCopied.getTitle());
    }
    
    @Test
    public void testUpdateObjectIDMethod() {
        String taskTitle = "L";
        boolean isRecurring = false;

        Task testTask = new Task.TaskBuilder(taskTitle, isRecurring).build();
        
        String objectIdBefore = testTask.getObjectID();
        testTask.updateObjectID();
        String objectIdAfter = testTask.getObjectID();
        assertNotEquals(objectIdBefore, objectIdAfter);
    }
    
    // TODO methods not tested yet: hasNext, toNext
    // TODO methods not tested yet: DISPLAY related methods
    
}