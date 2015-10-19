package fini.main.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Task {
    private String taskTitle;
    private String project;
    private String priority;
    private LocalDate taskDate;
    private LocalTime taskStartTime;
    private LocalTime taskEndTime;
    private String recurringDay;
    private boolean isRecurring;
    private String taskType;
    
    private static final String DEFAULT_TYPE = "Inbox";
    private static final String EVENT_TYPE = "Event";
    private static final String DEADLINE_TYPE = "Deadline";
    
    private static final String priorityHigh = "High";
    private static final String priorityMedium = "Medium";
    private static final String priorityNormal = "Normal";
    private static final String priorityLow = "Low";

    private static final String defaultPriority = "Normal";
    private static final String defaultProject = "Inbox";

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHMM");
    
    // default constructor
    public Task() {
        this.taskTitle = "Untitled Task";
        this.project = defaultProject;
        this.priority = priorityMedium;
        this.taskType = DEFAULT_TYPE;
        this.taskDate = null;
        this.taskStartTime = null;
        this.taskEndTime = null;
    }
    
    // constructor for floating task
    public Task(String taskTitle) {
        this.taskTitle = taskTitle;
        this.taskType = DEFAULT_TYPE;
        this.project = defaultProject;
    }
    
    // constructor for event
    public Task(boolean isRecurring, String title, String date, String startTime, String endTime,
            String priority, String project) {
        
        setRecurring(isRecurring);
        setTitle(title);
        setPriority(priority);
        setProject(project);
        setStartTime(startTime);
        setEndTime(endTime);
       
        if (startTime != null && endTime != null) {
            this.taskType = EVENT_TYPE;
        } else if (startTime != null) {
            this.taskType = DEADLINE_TYPE;
        } else {
            this.taskType = DEFAULT_TYPE;
        } 
        
        // if recurring take in the day as it is, else parse
        if (isRecurring) {
            recurringDay = date;
            System.out.println("Task Class: Recurring Day: " + recurringDay);
        } else {
            setTaskDate(date);
        }
    }

    private String formatTime(String userGivenTime) {
        userGivenTime = userGivenTime.toLowerCase();
        System.out.println("Task Class: User Given Time " + userGivenTime);

        boolean isTimeAm = checkIfTimeIsAm(userGivenTime);
        boolean isTimePm = checkIfTimeIsPm(userGivenTime);
        String timeOfDay = "";
        int time = 0;

        if (isTimeAm) {
            timeOfDay = "am";
            String extractTime = userGivenTime.replace("am", "");
            time = Integer.parseInt(extractTime);
        } else if (isTimePm) {
            timeOfDay = "pm";
            String extractTime = userGivenTime.replace("pm", "");
            time = Integer.parseInt(extractTime);
        }

        String formattedStartTime = "";
        switch (time) {
            case 1:
                formattedStartTime = (timeOfDay.equals("am")) ? "0100" : "1300";
                break;
            case 2:
                formattedStartTime = (timeOfDay.equals("am")) ? "0200" : "1400";
                break;
            case 3:
                formattedStartTime = (timeOfDay.equals("am")) ? "0300" : "1500";
                break;
            case 4:
                formattedStartTime = (timeOfDay.equals("am")) ? "0400" : "1600";
                break;
            case 5:
                formattedStartTime = (timeOfDay.equals("am")) ? "0500" : "1700";
                break;
            case 6:
                formattedStartTime = (timeOfDay.equals("am")) ? "0600" : "1800";
                break;
            case 7:
                formattedStartTime = (timeOfDay.equals("am")) ? "0700" : "1900";
                break;
            case 8:
                formattedStartTime = (timeOfDay.equals("am")) ? "0800" : "2000";
                break;
            case 9:
                formattedStartTime = (timeOfDay.equals("am")) ? "0900" : "2100";
                break;
            case 10:
                formattedStartTime = (timeOfDay.equals("am")) ? "1000" : "2200";
                break;
            case 11:
                formattedStartTime = (timeOfDay.equals("am")) ? "1100" : "2300";
                break;
            case 12:
                formattedStartTime = (timeOfDay.equals("am")) ? "0000" : "1200";
                break;
            default:
                break;
        }
        return formattedStartTime;
    }

    private boolean checkIfTimeIsPm(String userGivenTime) {
        return userGivenTime.contains("pm");
    }

    private boolean checkIfTimeIsAm(String userGivenTime) {
        return userGivenTime.contains("am");
    }

    public String getTitle() {
        assert taskTitle != null;
        return taskTitle;
    }

    public String getProject() {
        return this.project;
    }
    
    public String getPriority() {
        return priority;
    }
    
    public String getDate() {
        if (taskDate == null) {
            return "No date";
        }
        return taskDate.toString();
    }

    public String getStartTime() {
        if (taskStartTime == null) {
            return "No Time";
        }
        return taskStartTime.toString();
    }

    public String getEndTime() {
        if (taskEndTime == null) {
            return "No Time";
        }
        return taskEndTime.toString();
    }
    
    public String getRecurringDay() {
        return recurringDay;
    }
    
    public String getTaskType() {
        return taskType;
    }
    
    // TODO only a stub
    public String getLabelForTaskOverviewPane() {
        return "Inbox";
    }
    
    public boolean checkIfRecurring() {
        return isRecurring;
    }

    public boolean checkIfDeadline() {
        return taskType == DEADLINE_TYPE;
    }

    public boolean checkIfEvent() {
        return taskType == EVENT_TYPE;
    }

    public boolean checkIfFloating() {
        return taskType == DEFAULT_TYPE;
    }

    public void setRecurring(boolean isRecurring) {
        this.isRecurring = isRecurring;
    }
    
    public void setTitle(String title) {
        this.taskTitle = title;
    }
    
    public void setProject(String project) {
        if (project != null) {
            this.project = project;
        } else {
            this.project = defaultProject;
        }
    }
    
    public void setPriority(String priority) {
        if (priority != null) {
            assert(priority != null);
            this.priority = priority;
        } else {
            this.priority = defaultPriority;
        }
    }
    
    public void setStartTime(String startTime) {
        if (startTime != null) {
            String formattedStartTime = formatTime(startTime);
            this.taskStartTime = LocalTime.parse(formattedStartTime, timeFormatter);
        }
    }
    
    public void setEndTime(String endTime) {
        if (endTime != null) {
            String formattedEndTime = formatTime(endTime);
            this.taskEndTime = LocalTime.parse(formattedEndTime, timeFormatter);
        }
    }
    
    public void setTaskDate(String date) {
        if (date != null) {
            this.taskDate = LocalDate.parse(date, dateFormatter);
        }
    }
}
