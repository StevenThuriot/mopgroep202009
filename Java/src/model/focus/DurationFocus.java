package model.focus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.Task;
import model.TaskDurationComparator;

public class DurationFocus extends FocusStrategy {

	private int minimum,maximum;
	public DurationFocus(int minimum,int maximum)
	{
		this.minimum = minimum;
		this.maximum = maximum;
	}
	
	/**
	 * Sort the tasks according to Duration
	 * @param tasks The original list of tasks
	 * @return list of tasks, sorted by Duration
	 */
	protected List<Task> sort(List<Task> tasks)
	{
		Collections.sort(tasks,new TaskDurationComparator());
		return tasks;
	}
	/**
	 * Filter tasks according to duration. Only returns tasks where minimumDuration < Task duration < maximum duration
	 * @param taken The original list of tasks
	 * @return List of tasks, filtered by minimum and maximum duration
	 */
	protected List<Task> filter(List<Task> taken)
	{
		ArrayList<Task> filtered = new ArrayList<Task>();
		for(Task current : taken)
			if(current.getDuration() < this.maximum && current.getDuration() > this.minimum)
				filtered.add(current);
		return filtered;
	}

}
