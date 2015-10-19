package fini.main.model;

/**
 * Project Model Interface
 * @author gaieepo
 *
 */
public interface ProjectInterface {
	public void addTaskToProject(Task t);
	public void removeTaskFromProject(Task t);
	public void updateTasks();
}
