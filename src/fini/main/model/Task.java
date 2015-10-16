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
	private boolean isDeadline = false;
	private boolean isEvent = false;
	private boolean isFloating = false;

	private static final String priorityHigh = "High";
	private static final String priorityMedium = "Medium";
	private static final String priorityNormal = "Normal";
	private static final String priorityLow = "Low";

	private static final String defaultPriority = "Normal";
	private static final String defaultProject = "Inbox";

	private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHMM");

	public Task() {
		this.taskTitle = "Untitled Task";
		this.project = defaultProject;
		this.priority = priorityMedium;
		this.taskDate = null;
		this.taskStartTime = null;
		this.taskEndTime = null;
	}

	public Task(String taskTitle) {
		this.taskTitle = taskTitle;
		this.project = defaultProject;
	}

	public Task(boolean isRecurring, String title, String date, String startTime, String endTime, String priority, String project) {
		this.isRecurring = isRecurring;
		this.taskTitle = title;

		if(startTime != null) {
			String formattedStartTime = formatTime(startTime);
			this.taskStartTime = LocalTime.parse(formattedStartTime, timeFormatter);
		}

		if(endTime != null) {
			String formattedEndTime = formatTime(endTime);
			this.taskEndTime = LocalTime.parse(formattedEndTime, timeFormatter);
		}

		if(startTime !=null  && endTime != null) {
			isEvent = true;
		} else if(startTime != null) {
			isDeadline = true;
		} else {
			isFloating = true;
		}

		if(priority != null) {
			assert(priority != null);
			this.priority = priority;
		} else {
			this.priority = defaultPriority;
		}

		if(project != null) {
			this.project = project;
		} else {
			this.project = defaultProject;
		}

		// if recurring take in the day as it is, else parse
		if (isRecurring) {
			recurringDay = date;
			System.out.println("Task Class: Recurring Day: " + recurringDay);
		} else {
			if(date != null) {
				this.taskDate = LocalDate.parse(date, dateFormatter);
			}
		}
	}

	private String formatTime(String userGivenTime) {
		userGivenTime = userGivenTime.toLowerCase();
		System.out.println("Task Class: User Given Time " + userGivenTime);

		boolean isTimeAm = checkIfTimeIsAm(userGivenTime);
		boolean isTimePm = checkIfTimeIsPm(userGivenTime);
		String timeOfDay = "";
		int time = 0;

		if(isTimeAm) {
			timeOfDay = "am";
			String extractTime = userGivenTime.replace("am", "");
			time = Integer.parseInt(extractTime);
		} else if(isTimePm) {
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

	public String getDate() {
		if(taskDate == null) {
			return "No date";
		}
		return taskDate.toString();
	}

	public String getStartTime() {
		if(taskStartTime == null) {
			return "No Time";
		}
		return taskStartTime.toString();
	}

	public String getEndTime() {
		if(taskEndTime == null) {
			return "No Time";
		}
		return taskEndTime.toString();
	}

	public boolean checkIfRecurring() {
		return isRecurring;
	}

	public String getRecurringDay() {
		return recurringDay;
	}

	public boolean checkIfDeadline() {
		return isDeadline;
	}

	public boolean checkIfEvent() {
		return isEvent;
	}

	public boolean checkIfFloating() {
		return isFloating;
	}

	public String getPriority() {
		return priority;
	}
	
	// TODO need to add task type
	public String getTaskType() {
        return "Inbox";
    }
}
