package model;

import exception.*;

public abstract class TaskState {
	private Task context;
	
	/**
	 * Constructor
	 * @param context
	 */
	protected TaskState(Task context) {
		this.context = context;
	}

	/**
	 * Adds a dependency to the current task.
	 * @param 	dependency
	 * 			The dependency to be added.
	 * @post	The task is now dependent on <dependency>
	 * 			| (new.getDependentTasks()).contains(dependent)
	 * @throws 	BusinessRule1Exception
	 * 			Adding the dependency would violate business rule 1.
	 * 			| !this.depencySatisfiesBusinessRule1(dependent)
	 * @throws 	DependencyCycleException
	 * 			Adding the dependency would create a dependency cycle.
	 * 			| !this.dependencyHasNoCycle()
	 * @throws IllegalStateCall If the call is not allowed in the current state
	 */
	protected void addDependency(Task dependency) throws BusinessRule1Exception, DependencyCycleException, IllegalStateCall{
		throw new IllegalStateCall();
	}	
	
	/**
	 * Adds a resource to the resources required for this task.
	 * @throws IllegalStateCall 
	 */
	protected void addRequiredResource(Resource resource) throws IllegalStateCall{
		throw new IllegalStateCall();
	}
	
	/**
	 * Returns whether a task can be executed right now.
	 * This is true when all its dependencies are (successfully) finished and
	 * all of its required resources are available.
	 */
	protected Boolean canBeExecuted() 
	{
		return false;
	}
	
	/**
	 * Returns a boolean indicating whether the current task can be finished.
	 * A task can not be finished when it is failed or any of its dependencies is failed.
	 */
	protected boolean canBeFinished()
	{
		return false;
	}

	/**
	 * Get the context (The task which this status belongs to)
	 * @return
	 */
	protected Task getContext() {
		return context;
	}

	/**
	 * Returns whether a task is failed or not.
	 * @return
	 */
	protected Boolean isFailed()
	{
		return false;
	}

	/**
	 * Returns whether a task is performed or not.
	 * @return
	 */
	protected Boolean isPerformed()
	{
		return false;
	}

	/**
	 * Returns whether a task is succesful or not.
	 * @return
	 */
	protected Boolean isSuccesful()
	{
		return false;
	}
	
	/**
	 * Returns whether a task is unfinished or not.
	 * @return
	 */
	protected Boolean isUnfinished()
	{
		return false;
	}
	
	/**
	 * Removes a dependency from this task.
	 * @param 	dependency
	 * 			The dependency to be removed.
	 * @throws DependencyException 
	 * @throws IllegalStateCall 
	 * @throws DependencyException 
	 * @post 	The task is no longer dependent on <dependency>
	 * 			|! (new.getDependentTasks()).contains(dependent)
	 */
	public void removeDependency(Task dependency) throws IllegalStateCall, DependencyException{
		throw new IllegalStateCall();
	}
	
	/**
	 * Removes a resource from the resources required for this task.
	 * @throws IllegalStateCall 
	 */
	public void removeRequiredResource(Resource resource) throws IllegalStateCall{
		throw new IllegalStateCall();
	}
	
	/**
	 * Returns whether the current task satisfies the business rule 2.
	 * @return Boolean
	 */
	protected abstract Boolean satisfiesBusinessRule2();
	
	/**
	 * Returns whether the current task satisfies the business rule 3.
	 * @return Boolean
	 */
	protected abstract Boolean satisfiesBusinessRule3();
	
	/**
	 * Sets <newDescription> to be the new description of this task.
	 * @param	newDescription
	 * 			The new description
	 * @throws EmptyStringException 
	 * @throws IllegalStateCall 
	 * @post	| new.getDescription()== newDescription
	 */
	protected void setDescription(String newDescription) throws EmptyStringException, NullPointerException, IllegalStateCall {
		throw new IllegalStateCall();
	}
	
	/**
	 * Set the current state to failed
	 * @throws IllegalStateChangeException
	 */
	protected void setFailed() throws IllegalStateChangeException
	{
		throw new IllegalStateChangeException();
	}
	
	/**
	 * Set the current state to successful
	 * @throws IllegalStateChangeException
	 */
	protected void setSuccessful() throws IllegalStateChangeException 
	{
		throw new IllegalStateChangeException();
	}
	
}
