package fini.main.model;

/**
 * This class handles the saving of past status of the application
 * Mainly for UNDO and REDO functions
 * @author gaieepo
 *
 */
public class StatusSaver {
	private static StatusSaver statusSaver;
	
	private StatusSaver() {
		
	}
	
	public static StatusSaver getInstance() {
		if (statusSaver == null) {
			statusSaver = new StatusSaver();
		}
		return statusSaver;
	}
}
