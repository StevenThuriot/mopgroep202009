package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import gui.Describable;


import exception.EmptyStringException;
import exception.IllegalStateCallException;

public class Project implements Describable{

	
	/**
	 * A string providing a description of this project.
	 * @invar	description must not be empty
	 * 			| description != ""
	 */
	private String description;
	
	/**
	 * The user responsible for this project.
	 *	@TODO: consistency with user
	 */
	private User user;
	
	/**
	 * An ArrayList containing all the task that belong to this project.
	 */
	private ArrayList<Task> projectTasks;
	
	
	/**
	 * Creates a new project belonging to a certain user and with a given description.
	 * @param 	newDescription
	 * 			The description of the project.
	 * @param	newUser
	 * 			The user responsible for this project.
	 * @throws 	EmptyStringException
	 * 			The description given is empty
	 * 			| newDescription == ""
	 */
	public Project(String newDescription) throws EmptyStringException, NullPointerException{
		projectTasks = new ArrayList<Task>();
		setDescription(newDescription);
	}
	
	/**
	 * Removes this project and all of its tasks.
	 * Warning: tasks are deleted recursively - if other tasks depend on a deleted
	 * task, they will be deleted as well.
	 * @throws IllegalStateCallException 
	 */
	public void remove() throws IllegalStateCallException{
		List<Task> taskList = this.getTasks();
		
		if (taskList.size() > 0)
		{
			for(Task task: this.getTasks()){
				task.remove();
			}
		}
	}
	
	/**
	 * Binds a task to this project.
	 * @param 	task
	 * 			The task to bind to this project.
	 */
	public void bindTask(Task task){
		projectTasks.add(task);
	}
	
	/**
	 * Returns the user responsible for this project.
	 */
	public User getUser(){
		return user;
	}
	

	/**
	 * Returns the description of the project.
	 */
	public String getDescription(){
		return this.description;
	}
	
	/**
	 * Returns a String representation of this Project. At the moment,
	 * returns the description.
	 */
	public String toString(){
		return getDescription();
	}
	
	/**
	 * Returns an ArrayList of the tasks that are in this project.
	 */
	public List<Task> getTasks(){
		return Collections.unmodifiableList(projectTasks);
	}
	
	
	/**
	 * Sets <newDescription> to be the description of this project.
	 * @param 	newDescription
	 * @post	|new.getDescription() == newDescription
	 * @throws 	EmptyStringException 
	 * 			newDescription is the empty String.
	 * 			| newDescription()==""
	 */
	public void setDescription(String newDescription) throws EmptyStringException, NullPointerException{
		if (newDescription == null)
			throw new NullPointerException("Null was passed");
		
		if(newDescription.equals(""))
			throw new EmptyStringException(
					"A project must have a non-empty description");
		description = newDescription;
	}


}
