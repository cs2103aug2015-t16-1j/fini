 // @@author A0127483B 
package fini.main.model;

/**
 * Task interface for Task class
 * It extends Cloneable which allows Task to be copied when executing undo and redo functions
 * 
 */
public interface TaskInterface extends Cloneable {
    public Task makeCopy();
}
