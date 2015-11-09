# A0127483Bunused
###### src/fini/main/tests/BrainTest.java
``` java
// *
// */
//public class BrainTest {
//    Brain brain = Brain.getInstance();
//    DisplayControllerStub displayController = new DisplayControllerStub();
//    FiniParser finiParser = FiniParser.getInstance();
//    
//    @Before
//    public void initialize() {
//        brain.setDisplayController(displayController);
//    }
//    
//    @Test
//    public void testHelp() {
//        brain.executeCommand("help");
//        assertEquals(2, displayController.getTestMessages().size());
//        assertEquals("displayHelpPanel", displayController.getTestMessages().get(0));
//        assertEquals("updateMainDisplay", displayController.getTestMessages().get(1));
//    }
//    
//    @Test
//    public void testClear() {
//        brain.executeCommand("clear");
//        ObservableList<Task> testList = FXCollections.observableArrayList();
//        assertEquals(testList, brain.getTaskObservableList());
//    }
//    
//    @Test
//    public void testAdd() {
//        brain.executeCommand("add gaieepo tomorrow 2pm");
//        ObservableList<Task> testList = FXCollections.observableArrayList();
//        assertEquals(1, displayController.getTestMessages().size());
//        testList = displayController.getTestList();
//        assertEquals(1, testList.size());
//        assertEquals("gaieepo", testList.get(0).getTitle());
//    }
//}
```
