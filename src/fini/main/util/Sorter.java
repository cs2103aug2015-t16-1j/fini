package fini.main.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
/**
 * Sorting utility
 * @author gaieepo
 *
 */

import fini.main.model.Task;
import fini.main.model.Task.Type;
public class Sorter {
	private ArrayList<Task> listToSort;
	private ArrayList<Comparator<Task>> comparators;
	
	private static final int LOWER_THAN = -1;
	private static final int EQUAL = 0;
	private static final int HIGHER_THAN = 1;
	
	class SortByType implements Comparator<Task> {
		/**
		 * Possible combination:
		 * 
		 * Default - Default -> 0
		 * Event - Event -> 0
		 * Deadline - Deadline -> 0
		 * 
		 * Default - Deadline -> -1
		 * Deadline - Default -> 1
		 * 
		 * Default - Event -> -1
		 * Event - Default -> 1
		 * 
		 * Event - Deadline -> 0
		 * Deadline - Event -> 0
		 */
		@Override
		public int compare(Task lhs, Task rhs) {
			Type lt = lhs.getTaskType();
			Type rt = rhs.getTaskType();
			
			if (lt == Task.Type.DEFAULT && rt == Task.Type.DEADLINE) {
				return LOWER_THAN;
			} else if (lt == Task.Type.DEADLINE && rt == Task.Type.DEFAULT) {
				return HIGHER_THAN;
			} else if (lt == Task.Type.DEFAULT && rt == Task.Type.EVENT) {
				return LOWER_THAN;
			} else if (lt == Task.Type.EVENT && rt == Task.Type.DEFAULT) {
				return HIGHER_THAN;
			} else {
				return EQUAL;
			}
		}
	}
	
	class SortByTime implements Comparator<Task> {		
		/**
		 * Possible combination:
		 * NULL EXIST -> -1
		 * EXIST NULL -> 1
		 * NULL NULL -> 0
		 * EARLY LATER -> -1
		 * LATER EARLY -> 1
		 * SAME SAME -> 0
		 */
		@Override
		public int compare(Task lhs, Task rhs) {
			LocalTime t1 = lhs.getStartTime();
			LocalTime t2 = rhs.getStartTime();
			
			if (t1 == null && t2 != null) {
				return LOWER_THAN;
			} else if (t1 != null && t2 == null) {
				return HIGHER_THAN;
			} else if (t1 == null && t2 == null) {
				return EQUAL;
			} else if (t1.isBefore(t2)) {
				return LOWER_THAN;
			} else if (t1.isAfter(t2)) {
				return HIGHER_THAN;
			} else {
				return EQUAL;
			}
		}
	}
	
	class SortByDate implements Comparator<Task> {
		/**
		 * Possible combination:
		 * Same as SortByTime
		 */
		@Override
		public int compare(Task lhs, Task rhs) {
			LocalDate d1 = lhs.getStartDate();
			LocalDate d2 = rhs.getStartDate();
			
			if (d1 == null && d2 != null) {
				return LOWER_THAN;
			} else if (d1 != null && d2 == null) {
				return HIGHER_THAN;
			} else if (d1 == null && d2 == null) {
				return EQUAL;
			} else if (d1.isBefore(d2)) {
				return LOWER_THAN;
			} else if (d1.isAfter(d2)) {
				return HIGHER_THAN;
			} else {
				return EQUAL;
			}
		}
	}
	
	class SortByOverdue implements Comparator<Task> {
		/**
		 * Possible combination:
		 * Overdue Pending -> -1
		 * Pending Overdue -> 1
		 * Same -> 0
		 */
		@Override
		public int compare(Task lhs, Task rhs) {
			if (lhs.isOverdue() && !rhs.isOverdue()) {
				return LOWER_THAN;
			} else if (!lhs.isOverdue() && rhs.isOverdue()) {
				return HIGHER_THAN;
			} else {
				return EQUAL;
			}
		}
	}
	
	public Sorter(ArrayList<Task> listToSort) {
		this.listToSort = listToSort;
		comparators = new ArrayList<Comparator<Task>>();
		comparators.add(new SortByType());
		comparators.add(new SortByTime());
		comparators.add(new SortByDate());
//		comparators.add(new SortByOverdue());
	}
	
	public ArrayList<Task> getSortedList() {
		return listToSort;
	}
	
	public void sort() {
		assert listToSort != null;
		for (Comparator<Task> comparator : comparators) {
			Collections.sort(listToSort, comparator);
		}
	}
}
