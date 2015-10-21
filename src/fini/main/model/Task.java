package fini.main.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Task implements TaskInterface {
	public static enum Type {
		DEFAULT, EVENT, DEADLINE
	}

	private String taskTitle;
	private String project;
	private String priority;
	private LocalDate taskDate;
	private LocalTime taskStartTime;
	private LocalTime taskEndTime;
	private String recurringDay;
	private boolean isRecurring;
	private boolean isCompleted;

	private Type taskType;

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
		this.taskType = Type.DEFAULT;
		this.taskDate = null;
		this.taskStartTime = null;
		this.taskEndTime = null;
	}

	// constructor for floating task
	public Task(String taskTitle) {
		this.taskTitle = taskTitle;
		this.taskType = Type.DEFAULT;
		this.project = defaultProject;
	}

	// constructor for event
	public Task(boolean isRecurring, String title, String date, String startTime, String endTime,
			String priority, String project) {
		this.isRecurring = isRecurring;
		this.taskTitle = title;
		this.priority = priority;
		setProject(project);
		setStartTime(startTime);
		setEndTime(endTime);
		
		if (startTime != null && endTime != null) {
			this.taskType = Type.EVENT;
		} else if (startTime != null) {
			this.taskType = Type.DEADLINE;
		} else {
			this.taskType = Type.DEFAULT;
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

	// Public Getters
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

	public LocalDate getDate() {
		return taskDate;
	}

	public LocalTime getStartTime() {
		return taskStartTime;
	}

	public LocalTime getEndTime() {
		return taskEndTime;
	}

	public String getRecurringDay() {
		return recurringDay;
	}

	public Type getTaskType() {
		return taskType;
	}

	// TODO only a stub
	public String getLabelForTaskOverviewPane() {
		return "Inbox";
	}
	
	public boolean isCompleted() {
		return isCompleted;
	}
	
	public boolean isOverdue() {
		LocalDate nowDate = LocalDate.now();
		LocalTime nowTime = LocalTime.now();
		
		/**
		 * Possible combination:
		 * No end date -> F
		 * Got end date + end date before now -> T
		 * Got end date + end date after now -> F
		 * Got end date + end date today + No end time -> F
		 * Got end date + end date today + Got end time + end time before now -> T
		 * Got end date + end date today + Got end time + end time after now -> F
		 * Got end date + end date today + Got end time + end time now -> T (This else branch rarely touch! So sad :( )
		 */
		if (getDate() == null) {
			return false;
		} else if (getDate().isBefore(nowDate)) {
			return true;
		} else if (getDate().isAfter(nowDate)) {
			return false;
		} else {
			if (getStartTime() == null) {
				return false;
			} else if (getStartTime().isBefore(nowTime)) {
				return true;
			} else if (getStartTime().isAfter(nowTime)) {
				return false;
			} else {
				return true;
			}
		}
	}

	// Checking Methods
	public boolean checkIfRecurring() {
		return isRecurring;
	}

	public boolean checkIfDeadline() {
		return taskType == Type.DEADLINE;
	}

	public boolean checkIfEvent() {
		return taskType == Type.EVENT;
	}

	public boolean checkIfFloating() {
		return taskType == Type.DEFAULT;
	}

	// Public Setters
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
	
	public void setComplete() {
		isCompleted = true;
	}
	
	public void setIncomplete() {
		isCompleted = false;
	}

	// Project Related Methods
	// @author gaieepo
	public void update(String projectName, LocalDate projectEndDate, LocalTime projectEndTime, Boolean projectIsCompleted) {
		// TODO
	}

	@Override
	public Task makeCopy() {
		Task taskObject = null;
		try {
			taskObject = (Task) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return taskObject;
	}
}
