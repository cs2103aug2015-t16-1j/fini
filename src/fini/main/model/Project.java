package fini.main.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Project Model
 * @author gaieepo
 *
 */
public class Project implements ProjectInterface {
	private ArrayList<Task> tasks;
	private String projectName;
	private LocalDate projectEndDate = null;
	private LocalTime projectEndTime = null;
	private boolean projectIsCompleted = false;
	
	public Project(String projectName) {
		this.tasks = new ArrayList<Task>();
		this.projectName = projectName;
	}
	
	@Override
	public void addTaskToProject(Task newTask) {
		tasks.add(newTask);
	}
	
	public void addAllTasksToProject(ArrayList<Task> newTasks) {
		tasks.addAll(newTasks);
	}

	@Override
	public void removeTaskFromProject(Task deleteTask) {
		int taskIndex = tasks.indexOf(deleteTask);
		tasks.remove(taskIndex);
	}

	@Override
	public void updateTasks() {
		for (Task task : tasks) {
			task.update(projectName, projectEndDate, projectEndTime, projectIsCompleted);
		}
	}
	
	// Public Getters
	public String getProjectName() {
		return projectName;
	}
	
	public LocalDate getProjectEndDate() {
		return projectEndDate;
	}
	
	public LocalTime getProjectEndTime() {
		return projectEndTime;
	}
	
	public ArrayList<Task> getTasks() {
		return tasks;
	}
	
	public int getTaskSize() {
		return tasks.size();
	}
	
	public boolean isOverdue() {
		LocalDate nowDate = LocalDate.now();
		LocalTime nowTime = LocalTime.now();
		return checkOverdue(nowDate, nowTime);
	}
	
	// Public Setters
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	public void setProjectEndDate(LocalDate projectEndDate) {
		this.projectEndDate = projectEndDate;
	}
	
	public void setProjectEndTime(LocalTime projectEndTime) {
		this.projectEndTime = projectEndTime;
	}
	
	public void setCompleted() {
		this.projectIsCompleted = true;
	}
	
	public void setIncomplete() {
		this.projectIsCompleted = false;
	}
	
	// Private Getters
	
	// Utility
	@Override
	public String toString() {
		return getProjectName().trim();
	}
	
	public String getFormattedTimeAndDate(boolean includeDate) {
		String result = "";
		if (getProjectEndTime() != null) {
			result += addFormattedTime() + " ";
		}
		if (includeDate) {
			result += addFormattedDate();
		}
		return result.trim();
	}
	
	private String addFormattedTime() {
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h.mma");
		LocalTime endTime = getProjectEndTime();
		if (endTime != null) {
			return endTime.format(timeFormatter).toLowerCase();
		}
		return "";
	}
	
	private String addFormattedDate() {
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, d MMMM y");
		if (getProjectEndDate() != null) {
			return getProjectEndDate().format(dateFormatter);
		}
		return "";
	}
	
	private boolean checkOverdue(LocalDate dateNow, LocalTime timeNow) {
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
		if (getProjectEndDate() == null) {
			return false;
		} else if (getProjectEndDate().isBefore(dateNow)) {
			return true;
		} else if (getProjectEndDate().isAfter(dateNow)) {
			return false;
		} else {
			if (getProjectEndTime() == null) {
				return false;
			} else if (getProjectEndTime().isBefore(timeNow)) {
				return true;
			} else if (getProjectEndTime().isAfter(timeNow)) {
				return false;
			} else {
				return true;
			}
		}
	}
}
