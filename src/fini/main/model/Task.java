package fini.main.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.UUID;

/**
 * 
 * Task class is the fundamental element of FINI. Users can create, manipulate and store the tasks.
 * Since creating a task requires quite a lot of parameters for constructor which makes a lot of constructing work redundant,
 * we choose to make use of builder design pattern to merge a TaskBuilder inside Task class.
 * 
 * @@author Wang Jie (gaieepo) A0127483B 
 */
public class Task implements TaskInterface {
    /* ***********************************
     * Constants
     * ***********************************/
    public static enum Type {
        DEFAULT, EVENT, DEADLINE
    }

    public static enum Priority {
        HIGH, MEDIUM, LOW, NORMAL
    }

    /* ***********************************
     * Required fields
     * ***********************************/
    private String taskTitle;
    private boolean isRecurring;

    /* ***********************************
     * Optional fields
     * ***********************************/
    private String projectName;
    private Priority priority;
    private LocalDateTime taskStartDateTime = null;
    private LocalDateTime taskEndDateTime = null;
    private LocalDateTime recursUntil;
    private Period interval;

    private boolean isCompleted = false;
    private Type taskType;

    // ObjectID assign each Task instance a distinct ID
    // RecurUniqueID assign same ID for different Tasks under same recurring task
    private String objectID = null;
    private String recurUniqueID = null;

    private static final String defaultProject = "Inbox";

    /* ***********************************
     * TaskBuilder
     * ***********************************/
    public static class TaskBuilder {
        // Required
        private final String taskTitle;
        private final boolean isRecurring;

        // Optional
        private String projectName = defaultProject;
        private Priority priority = Priority.NORMAL;
        private ArrayList<LocalDateTime> dateTimes = new ArrayList<LocalDateTime>();
        private LocalDateTime recursUntil = null;
        private Period interval = null;

        public TaskBuilder(String taskTitle, boolean isRecurring) {
            this.taskTitle = taskTitle;
            this.isRecurring = isRecurring;
        }

        public TaskBuilder setProjectName(String projectName) {
            this.projectName = projectName;
            return this;
        }

        public TaskBuilder setPriority(Priority priority) {
            this.priority = priority;
            return this;
        }

        public TaskBuilder setDatetimes(ArrayList<LocalDateTime> dateTimes) {
            this.dateTimes = dateTimes;
            return this;
        }

        public TaskBuilder setRecursUntil(LocalDateTime recursUntil) {
            this.recursUntil = recursUntil;
            return this;
        }

        public TaskBuilder setInterval(Period interval) {
            this.interval = interval;
            return this;
        }

        // Return a Task through build()
        public Task build() {
            return new Task(this);
        }
    }

    /* ***********************************
     * Private constructor
     * ***********************************/
    private Task(TaskBuilder taskBuilder) {
        // Required
        taskTitle = taskBuilder.taskTitle;
        isRecurring = taskBuilder.isRecurring;

        if (isRecurring) {
            recurUniqueID = UUID.randomUUID().toString();
        }

        objectID = UUID.randomUUID().toString();

        // Optional
        projectName = taskBuilder.projectName;
        priority = taskBuilder.priority;
        recursUntil = taskBuilder.recursUntil;
        interval = taskBuilder.interval;

        switch (taskBuilder.dateTimes.size()) {
            case 2:
                taskType = Type.EVENT;
                break;
            case 1:
                taskType = Type.DEADLINE;
                break;
            default:
                taskType = Type.DEFAULT;
                break;
        }

        switch (taskType) {
            case EVENT:
                taskStartDateTime = taskBuilder.dateTimes.get(0);
                taskEndDateTime = taskBuilder.dateTimes.get(1);
                break;
            case DEADLINE:
                taskStartDateTime = taskBuilder.dateTimes.get(0);
                break;
            default:
                break;
        }
    }

    /* ***********************************
     * Public getters
     * ***********************************/
    public String getTitle() {
        assert taskTitle != null;
        return taskTitle;
    }

    public String getProjectName() {
        return projectName;
    }

    public Priority getPriority() {
        return priority;
    }

    public LocalDateTime getStartDateTime() {
        return taskStartDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return taskEndDateTime;
    }

    public LocalDateTime getRecursUntil() {
        return recursUntil;
    }

    public Type getTaskType() {
        return taskType;
    }

    public String getRecurUniqueID() {
        return recurUniqueID;
    }

    public boolean hasRecurUniqueID() {
        return recurUniqueID != null;
    }

    public String getObjectID() {
        return objectID;
    }

    public Period getInterval() {
        return interval;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public boolean isOverdue() {
        LocalDateTime nowDateTime = LocalDateTime.now();

        /**
         * Possible combination:
         * no startDateTime -> Not overdue
         * startDateTime before now -> Overdue
         * startDateTime after now -> Not Overdue
         * 
         */
        if (taskStartDateTime == null) {
            return false;
        } else if (taskStartDateTime.isAfter(nowDateTime)) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isRecurring() {
        return isRecurring;
    }

    /* ***********************************
     * Public setters
     * ***********************************/
    public void setIsRecurring(boolean isRecurring) {
        this.isRecurring = isRecurring;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public void setProjectName(String projectName) {
        if (projectName != null) {
            this.projectName = projectName;
        } else {
            this.projectName = defaultProject;
        }
    }

    public void setPriority(Priority priority) {
        if (priority != null) {
            assert(priority != null);
            this.priority = priority;
        } else {
            this.priority = Priority.NORMAL;
        }
    }

    public void setTaskStartDateTime(LocalDateTime taskStartDateTime) {
        this.taskStartDateTime = taskStartDateTime;
    }

    public void setTaskEndDateTime(LocalDateTime taskEndDateTime) {
        this.taskEndDateTime = taskEndDateTime;
    }

    public void setIsComplete() {
        isCompleted = true;
    }

    public void setIncomplete() {
        isCompleted = false;
    }

    public void updateObjectID() {
        objectID = UUID.randomUUID().toString();
    }

    /* ***********************************
     * Utility methods
     * ***********************************/
    public boolean hasNext() {
        if (isRecurring) {
            if (recursUntil != null) {
                return taskStartDateTime.plus(interval).isBefore(recursUntil);
            }
            return true;
        }
        return false;
    }

    public void toNext() {
        if (hasNext()) {
            if (taskStartDateTime != null) {
                taskStartDateTime = taskStartDateTime.plus(interval);
            }
            if (taskEndDateTime != null) {
                taskEndDateTime = taskEndDateTime.plus(interval);
            }
        }
    }

    /* ***********************************
     * Copy related methods
     * ***********************************/
    @Override
    public Task makeCopy() {
        Task taskObject = null;
        try {
            taskObject = (Task) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        taskObject.updateObjectID();
        return taskObject;
    }

    /* ***********************************
     * Display related methods
     * ***********************************/
    public boolean isTaskDueToday() {
        return taskStartDateTime == null ? false : taskStartDateTime.toLocalDate().isEqual(LocalDate.now());
    }

    public boolean isTaskDueTomorrow() {
        return taskStartDateTime == null ? false : taskStartDateTime.toLocalDate().isEqual(LocalDate.now().plusDays(1));
    }

    public boolean isTaskDueWithinSevenDays() {
        return taskStartDateTime == null ? false : taskStartDateTime.toLocalDate().isBefore(LocalDate.now().plusDays(7));
    }

    public String getLabelForTaskOverviewPane() {
        return "Inbox";
    }
}
