package fini.main.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;

public class Task implements TaskInterface {
	public static enum Type {
		DEFAULT, EVENT, DEADLINE
	}

	public static enum Priority {
		HIGH, MEDIUM, LOW, NORMAL
	}

	private String taskTitle;
	private String projectName;
	private Priority priority;
	private LocalDate taskStartDate;
	private LocalTime taskStartTime;
	private LocalDate taskEndDate;
	private LocalTime taskEndTime;
	private boolean isRecurring;
	private LocalDateTime recursUntil;
	private boolean isCompleted;
	private Type taskType;

	private static final String defaultProject = "Inbox";

	private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHMM");

	public Task(String notParsed, ArrayList<LocalDateTime> datetimes,
			Priority priority, String projectName, boolean isRecurring, LocalDateTime recursUntil) {
		isCompleted = false;

		setTitle(notParsed);
		setRecurring(isRecurring);
		setPriority(priority);
		setProject(projectName);
		setRecursUntil(recursUntil);
		this.recursUntil = recursUntil;

		switch (datetimes.size()) {
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
			LocalDateTime firstDatetime = datetimes.get(0);
			LocalDateTime secondDatetime = datetimes.get(1);
			taskStartDate = firstDatetime.toLocalDate();
			taskStartTime = firstDatetime.toLocalTime();
			taskEndDate = secondDatetime.toLocalDate();
			taskEndTime = secondDatetime.toLocalTime();
			break;
		case DEADLINE:
			firstDatetime = datetimes.get(0);
			taskStartDate = firstDatetime.toLocalDate();
			taskStartTime = firstDatetime.toLocalTime();
			break;
		default:
			break;
		}

		System.out.println("Task type: " + taskType);
	}

	private void setRecursUntil(LocalDateTime recursUntil) {
		this.recursUntil = recursUntil;
	}

	// constructor with only taskTitle. dont delete. for testing purpose
	public Task(String taskTitle){
		setTitle(taskTitle);
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
		if(projectName != null) {
			return projectName;
		}
		return defaultProject;
	}

	public Priority getPriority() {
		return priority;
	}

	public LocalDate getStartDate() {
		return taskStartDate;
	}

	public LocalTime getStartTime() {
		return taskStartTime;
	}

	public LocalDate getEndDate() {
		return taskEndDate;
	}

	public LocalTime getEndTime() {
		return taskEndTime;
	}

	public LocalDateTime getRecursUntil() {
		return recursUntil;
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
		if (getStartDate() == null) {
			return false;
		} else if (getStartDate().isBefore(nowDate)) {
			return true;
		} else if (getStartDate().isAfter(nowDate)) {
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
			this.projectName = project;
		} else {
			this.projectName = defaultProject;
		}
	}

	public void setPriority(Priority priority) {
		if (priority != null) {
			this.priority = priority;
		} else {
			this.priority = Priority.NORMAL;
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

	public void setTaskStartDate(String date) {
		if (date != null) {
			this.taskStartDate = LocalDate.parse(date, dateFormatter);
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

		// Init values?

		return taskObject;
	}

	// TODO: Implement recurring day function
	public String getRecurringDay() {
		return null;
	}

	// only applicable for deadlines/events etc.
	public boolean checkIfOverdue() {
		if(taskType != Type.DEFAULT) {
			LocalDate today = LocalDate.now();
			return taskStartDate.isBefore(today);
		}
		return false;
	}

	public boolean isTaskDueToday() {
		if(taskType != Type.DEFAULT) {
			LocalDate today = LocalDate.now();
			return taskStartDate.equals(today);
		} 
		return false;
	}

	public boolean isTaskDueTomorrow() {
		if(taskType != Type.DEFAULT) {
			LocalDate tomorrow = LocalDate.now().plusDays(1);
			return taskStartDate.equals(tomorrow);
		} 
		return false;
	}

	public boolean isTaskDueWithinSevenDays() {
		if(taskType != Type.DEFAULT) {
			LocalDate nextSevenDays = LocalDate.now().plusDays(7);
			return taskStartDate.isBefore(nextSevenDays);
		}
		return false;
	}
}
