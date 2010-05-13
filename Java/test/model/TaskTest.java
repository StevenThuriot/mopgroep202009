package model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import model.Project;
import model.Resource;
import model.ResourceType;
import model.Task;
import model.User;
import model.repositories.RepositoryManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import exception.BusinessRule1Exception;
import exception.BusinessRule2Exception;
import exception.BusinessRule3Exception;
import exception.DependencyCycleException;
import exception.DependencyException;
import exception.EmptyStringException;
import exception.IllegalStateCallException;
import exception.IllegalStateChangeException;
import exception.NotAvailableException;
import exception.TaskFailedException;
import exception.UnknownStateException;

import static org.junit.Assert.*;


public class TaskTest {
	/**
	 * Task to be tested
	 */
	private Task task;
	/**
	 * StartDate to be used in the Task objects
	 */
	private GregorianCalendar startDate;
	
	/**
	 * EndDate to be used in the Task objects
	 */
	private GregorianCalendar endDate;
	/**
	 * RepositoryManager to be used
	 */
	private RepositoryManager manager;
	/**
	 * Resource to be added to the Task
	 */
	private Resource resource;
	private User user;

	/**
	 * Setting up the test.
	 * @throws BusinessRule3Exception 
	 * @throws IllegalStateCallException 
	 * @throws BusinessRule1Exception 
	 * @throws EmptyStringException 
	 * @throws NullPointerException 
	 * @throws EmptyStringException 
	 * @throws DependencyCycleException 
	 * @throws BusinessRule1Exception 
	 * @throws IllegalStateCallException 
	 * @throws NullPointerException 
	 * @throws BusinessRule3Exception 
	 */
	@Before
	public void setUp() throws NullPointerException, EmptyStringException, BusinessRule1Exception, IllegalStateCallException, BusinessRule3Exception
	{
	    manager = new RepositoryManager();
		user = new User("John",new UserType(""));
		startDate = (GregorianCalendar) manager.getClock().getTime().clone();//Now
		endDate = (GregorianCalendar) manager.getClock().getTime().clone();
		endDate.add(Calendar.DAY_OF_YEAR, 4); // 4 days to finish
        resource = new Resource("Projector", new ResourceType(""));
        task = new Task("Descr",user,new TaskTimings((GregorianCalendar)manager.getClock().getTime().clone(), endDate, 120), manager.getClock());
	}
	
	/**
	 * Tearing down variables after each test
	 */
	@After
	public void tearDown()
	{
		user = null;
		task = null;
		startDate = null;
		endDate = null;
	}
	
	/**
	 * Tests the initialization behavior of Task.
	 * @throws EmptyStringException 
	 * @throws IllegalStateCallException 
	 * @throws NullPointerException 
	 * @throws BusinessRule3Exception 
	 * @throws BusinessRule1Exception 
	 */
	@Test(expected=BusinessRule1Exception.class)
	public void initialization() throws EmptyStringException, NullPointerException, IllegalStateCallException, BusinessRule3Exception, BusinessRule1Exception{
		
		// <task> is initialized with no dependencies or depending tasks
		assertTrue(task.getDependencies().isEmpty());
		assertTrue(task.getDependentTasks().isEmpty());
		// <task> is linked to the user John, and is given the name "Descr"
		assertTrue(task.getUser() == user);
		assertTrue(task.getDescription().equals("Descr"));
		// <task> should satisfy the business rules
		assertTrue(task.satisfiesBusinessRule1());
		// TODO: other BR's here?
		
		//Try a different task, that fails business rule 1
		endDate = new GregorianCalendar();
		endDate.add(Calendar.DAY_OF_YEAR,1);
		//One day is available, but the duration is 25 hours. Business Rule exception should be thrown.
		//Task "MOP" can never be completed in time :-(
		@SuppressWarnings("unused")
		Task task2 = new Task("MOP", user, new TaskTimings(new GregorianCalendar(), endDate, 1500), manager.getClock());
	}/**
	 * Testing setting the state to failed
	 * @throws DependencyException 
	 * @throws IllegalStateChangeException 
	 */
	@Test
	public void checkStateZero() throws IllegalStateChangeException
	{
		assertEquals("Available", task.getCurrentStateName());
	}

	/**
	 * Testing setting the state to Successful
	 * @throws DependencyException 
	 * @throws IllegalStateChangeException 
	 * @throws BusinessRule3Exception 
	 * @throws BusinessRule2Exception 
	 */
	@Test
	public void checkStateOne() throws IllegalStateChangeException, BusinessRule2Exception, BusinessRule3Exception
	{
		task.setSuccessful();
		assertEquals("Successful", task.getCurrentStateName());
	}
	
	/**
	 * Testing setting the state to failed
	 * @throws DependencyException 
	 * @throws IllegalStateChangeException 
	 */
	@Test
	public void checkStateTwo() throws IllegalStateChangeException
	{
		task.setFailed();
		assertEquals("Failed", task.getCurrentStateName());
	}

	
	/**
	 * Testing setting the state to successful when failed
	 * @throws IllegalStateChangeException 
	 * @throws DependencyException 
	 * @throws IllegalStateChangeException 
	 * @throws BusinessRule3Exception 
	 * @throws BusinessRule2Exception 
	 */
	@Test(expected=IllegalStateChangeException.class)
	public void checkStateThree() throws IllegalStateChangeException, BusinessRule2Exception, BusinessRule3Exception
	{
		task.setFailed();
		task.setSuccessful();
	}

	
	/**
	 * Testing setting the state to failed when successful
	 * @throws IllegalStateChangeException 
	 * @throws DependencyException 
	 * @throws IllegalStateChangeException 
	 * @throws BusinessRule3Exception 
	 * @throws BusinessRule2Exception 
	 */
	@Test(expected=IllegalStateChangeException.class)
	public void checkStateFour() throws IllegalStateChangeException, BusinessRule2Exception, BusinessRule3Exception
	{
		task.setSuccessful();
		task.setFailed();
	}

	
	/**
	 * Testing if the task is unfinished
	 * @throws IllegalStateChangeException 
	 * @throws DependencyException 
	 * @throws IllegalStateChangeException 
	 */
	@Test
	public void checkStateFive() throws IllegalStateChangeException
	{
		assertEquals(true, task.isUnfinished());
		assertEquals(false, task.isFailed());
		assertEquals(false, task.isSuccesful());
	}

	
	/**
	 * Testing if the task is successful
	 * @throws IllegalStateChangeException 
	 * @throws DependencyException 
	 * @throws IllegalStateChangeException 
	 * @throws BusinessRule3Exception 
	 * @throws BusinessRule2Exception 
	 */
	@Test
	public void checkStateSix() throws IllegalStateChangeException, BusinessRule2Exception, BusinessRule3Exception
	{
		task.setSuccessful();
		
		assertEquals(false, task.isUnfinished());
		assertEquals(false, task.isFailed());
		assertEquals(true, task.isSuccesful());
	}

	
	/**
	 * Testing if the task is failed
	 * @throws IllegalStateChangeException 
	 * @throws DependencyException 
	 * @throws IllegalStateChangeException 
	 */
	@Test
	public void checkStateSeven() throws IllegalStateChangeException
	{
		task.setFailed();
		
		assertEquals(false, task.isUnfinished());
		assertEquals(true, task.isFailed());
		assertEquals(false, task.isSuccesful());
	}

	
	/**
	 * Testing if the task satisfies business rule 2
	 * @throws IllegalStateChangeException 
	 * @throws DependencyException 
	 * @throws IllegalStateChangeException 
	 * @throws BusinessRule3Exception 
	 * @throws IllegalStateCallException 
	 * @throws BusinessRule1Exception 
	 * @throws EmptyStringException 
	 * @throws NullPointerException 
	 * @throws DependencyCycleException 
	 * @throws BusinessRule2Exception 
	 */
	@Test
	public void checkStateEight() throws IllegalStateChangeException, NullPointerException, EmptyStringException, BusinessRule1Exception, IllegalStateCallException, BusinessRule3Exception, DependencyCycleException, BusinessRule2Exception
	{
		Task task2 = new Task("some name", user, new TaskTimings(startDate, endDate, 50), manager.getClock());
		task2.setSuccessful();
		task.addDependency(task2);
		
		assertEquals(true, task.satisfiesBusinessRule2());
	}
	
	/**
	 * Testing setting the state to Successful using the parser
	 * @throws DependencyException 
	 * @throws IllegalStateChangeException 
	 * @throws UnknownStateException 
	 * @throws BusinessRule3Exception 
	 * @throws BusinessRule2Exception 
	 */
	@Test
	public void checkStateNine() throws IllegalStateChangeException, UnknownStateException, BusinessRule2Exception, BusinessRule3Exception
	{
		task.parseStateString("Successful");
		assertEquals("Successful", task.getCurrentStateName());
	}
	
	/**
	 * Testing setting the state to failed using the parser
	 * @throws DependencyException 
	 * @throws IllegalStateChangeException 
	 * @throws UnknownStateException 
	 * @throws BusinessRule3Exception 
	 * @throws BusinessRule2Exception 
	 */
	@Test
	public void checkStateTen() throws IllegalStateChangeException, UnknownStateException, BusinessRule2Exception, BusinessRule3Exception
	{
		task.parseStateString("Failed");
		assertEquals("Failed", task.getCurrentStateName());
	}
	
	/**
	 * Testing setting the state to failed using the parser
	 * @throws DependencyException 
	 * @throws IllegalStateChangeException 
	 * @throws UnknownStateException 
	 * @throws UnknownStateException 
	 * @throws BusinessRule3Exception 
	 * @throws BusinessRule2Exception 
	 */
	@Test(expected=UnknownStateException.class)
	public void checkStateEleven() throws IllegalStateChangeException, UnknownStateException, BusinessRule2Exception, BusinessRule3Exception
	{
		task.parseStateString("Trogdor the Burninator");
	}
	
	/**
	 * Testing default canBeExecuted
	 * @throws DependencyException 
	 * @throws IllegalStateChangeException 
	 * @throws UnknownStateException 
	 */
	@Test
	public void checkStateTwelve() throws IllegalStateChangeException
	{
		task.setFailed();
		assertEquals(false, task.canBeExecuted());
	}
	
	/**
	 * Testing default setDescription
	 * @throws DependencyException 
	 * @throws IllegalStateChangeException 
	 * @throws IllegalStateCallException 
	 * @throws EmptyStringException 
	 * @throws NullPointerException 
	 * @throws UnknownStateException 
	 */
	@Test(expected=IllegalStateCallException.class)
	public void checkStateThirteen() throws IllegalStateChangeException, NullPointerException, EmptyStringException, IllegalStateCallException
	{
		task.setFailed();
		task.setDescription("Trogdor Rules!");
	}
	
	/**
	 * Testing canBeExecuted when succesful
	 * @throws DependencyException 
	 * @throws IllegalStateChangeException 
	 * @throws IllegalStateCallException 
	 * @throws EmptyStringException 
	 * @throws NullPointerException 
	 * @throws BusinessRule3Exception 
	 * @throws BusinessRule2Exception 
	 * @throws UnknownStateException 
	 */
	@Test
	public void checkStateFourteen() throws IllegalStateChangeException, NullPointerException, EmptyStringException, IllegalStateCallException, BusinessRule3Exception, BusinessRule2Exception
	{
		task.setSuccessful();
		assertEquals(true, task.canBeExecuted());
	}
	
	/**
	 * Testing default implementation for add dependancy
	 * @throws IllegalStateChangeException 
	 * @throws DependencyException 
	 * @throws IllegalStateChangeException 
	 * @throws IllegalStateCallException 
	 * @throws IllegalStateCallException 
	 * @throws BusinessRule3Exception 
	 * @throws EmptyStringException 
	 * @throws DependencyCycleException 
	 * @throws BusinessRule1Exception 
	 * @throws NullPointerException 
	 * @throws EmptyStringException 
	 * @throws NullPointerException 
	 * @throws BusinessRule3Exception 
	 * @throws BusinessRule2Exception 
	 * @throws NotAvailableException 
	 * @throws UnknownStateException 
	 */
	@Test(expected=IllegalStateCallException.class)
	public void checkStateSeventeen() throws IllegalStateChangeException, IllegalStateCallException, NullPointerException, BusinessRule1Exception, DependencyCycleException, EmptyStringException, BusinessRule3Exception, BusinessRule2Exception 
	{
		task.setSuccessful();
		task.addDependency(new Task("some name", user, new TaskTimings(startDate, endDate, 50), manager.getClock()));
	}
	
	/**
	 * Testing default implementation for add dependancy
	 * @throws IllegalStateChangeException 
	 * @throws DependencyException 
	 * @throws IllegalStateChangeException 
	 * @throws IllegalStateCallException 
	 * @throws IllegalStateCallException 
	 * @throws BusinessRule3Exception 
	 * @throws EmptyStringException 
	 * @throws DependencyCycleException 
	 * @throws BusinessRule1Exception 
	 * @throws NullPointerException 
	 * @throws EmptyStringException 
	 * @throws NullPointerException 
	 * @throws BusinessRule3Exception 
	 * @throws DependencyException 
	 * @throws BusinessRule2Exception 
	 * @throws NotAvailableException 
	 * @throws UnknownStateException 
	 */
	@Test(expected=IllegalStateCallException.class)
	public void checkStateNineteen() throws IllegalStateChangeException, IllegalStateCallException, NullPointerException, BusinessRule1Exception, DependencyCycleException, EmptyStringException, BusinessRule3Exception, DependencyException, BusinessRule2Exception 
	{
		Task task2 = new Task("some name", user, new TaskTimings(startDate, endDate, 50), manager.getClock());

		task.addDependency(task2);
		task2.setSuccessful();
		task.setSuccessful();
		task.removeDependency(task2);
	}
	
	
	/**
	 * Testing setDescription
	 * @throws NullPointerException
	 * @throws EmptyStringException
	 * @throws IllegalStateCallException
	 */
	@Test(expected=NullPointerException.class)
	public void checkStateTwenty() throws NullPointerException, EmptyStringException, IllegalStateCallException 
	{
		task.setDescription(null);
	}
	
	
	/**
	 * Testing setdescription
	 * @throws NullPointerException
	 * @throws EmptyStringException
	 * @throws IllegalStateCallException
	 */
	@Test(expected=EmptyStringException.class)
	public void checkStateTwentyOne() throws NullPointerException, EmptyStringException, IllegalStateCallException 
	{
		task.setDescription("");
	}
	
	/**
	 * Failed always satisfies rule 2
	 * @throws NullPointerException
	 * @throws EmptyStringException
	 * @throws IllegalStateCallException
	 * @throws IllegalStateChangeException
	 */
	@Test
	public void checkStateTwentyTwo() throws NullPointerException, EmptyStringException, IllegalStateCallException, IllegalStateChangeException 
	{
		task.setFailed();
		assertEquals(true, task.satisfiesBusinessRule2());
	}
	
	/**
	 * Failed always satisfies rule 3
	 * @throws NullPointerException
	 * @throws EmptyStringException
	 * @throws IllegalStateCallException
	 * @throws IllegalStateChangeException
	 */
	@Test
	public void checkStateTwentyThree() throws NullPointerException, EmptyStringException, IllegalStateCallException, IllegalStateChangeException 
	{
		task.setFailed();
		assertEquals(true, task.satisfiesBusinessRule3());
	}

	
	/**
	 * Checking for state possibilities
	 */
	@Test
	public void checkStateTwentyFour() 
	{
		ArrayList<String> list = new ArrayList<String>();

		list.add("Successful");
		list.add("Failed");
		
		assertEquals(list, task.getPossibleStateChanges());
	}
	
	/**
	 * Checking for state possibilities while failed
	 * @throws IllegalStateChangeException 
	 */
	@Test
	public void checkStateTwentyFive() throws IllegalStateChangeException 
	{
		ArrayList<String> list = new ArrayList<String>();
		
		task.setFailed();
		
		assertEquals(list, task.getPossibleStateChanges());
	}
	
	/**
	 * Checking for state possibilities while successful
	 * @throws IllegalStateChangeException 
	 * @throws BusinessRule3Exception 
	 * @throws BusinessRule2Exception 
	 */
	@Test
	public void checkStateTwentySix() throws IllegalStateChangeException, BusinessRule2Exception, BusinessRule3Exception 
	{
		ArrayList<String> list = new ArrayList<String>();
		
		task.setSuccessful();
		
		assertEquals(list, task.getPossibleStateChanges());
	}
	
	/**
	 * Test to see if description is set properly
	 * @throws EmptyStringException
	 * @throws IllegalStateCallException 
	 * @throws NullPointerException 
	 */
	@Test
	public void updateTaskDescription() throws EmptyStringException, NullPointerException, IllegalStateCallException
	{
		task.setDescription("ABC123");
		assertEquals("ABC123", task.getDescription());
	}
	
	/**
	 * Testing a task attaching to a project
	 * @throws EmptyStringException 
	 */
	@Test
	public void assignTaskProject() throws EmptyStringException
	{
		Project p = new Project("Proj");
		p.bindTask(task);
		
		assertTrue(p.getTasks().contains(task));
	}
	
	/**
	 * Tests on dependencies. Tests a proper dependency
	 * @throws IllegalStateCallException 
	 * @throws NullPointerException 
	 * @throws BusinessRule3Exception 
	 */
	@Test
	public void dependencies1() throws EmptyStringException, BusinessRule1Exception, DependencyCycleException, DependencyException, NullPointerException, IllegalStateCallException, BusinessRule3Exception{
		// Try a proper dependency
		Task task2 = new Task("some name", user, new TaskTimings(startDate, endDate, 50), manager.getClock());
		task.addDependency(task2);
		// Assure that it is properly initialized
		// For more details, see TaskDependencyManager and corresponding test class
		assertTrue(task.dependsOn(task2));
		assertTrue(task2.getDependentTasks().contains(task));
		//Remove it again
		task.removeDependency(task2);
		assertFalse(task.dependsOn(task2));
		assertFalse(task2.getDependentTasks().contains(task2));
	}
	
	/**
	 * Another test on dependencies. Tests a dependency that fails to satisfy business rule 1.
	 * @throws DependencyCycleException 
	 * @throws BusinessRule1Exception 
	 * @throws EmptyStringException 
	 * @throws IllegalStateCallException 
	 * @throws NullPointerException 
	 * @throws BusinessRule3Exception 
	 */
	@Test(expected=BusinessRule1Exception.class)
	public void dependencies2() throws DependencyCycleException, EmptyStringException, BusinessRule1Exception, NullPointerException, IllegalStateCallException, BusinessRule3Exception{
		startDate = new GregorianCalendar();
		startDate.add(Calendar.DAY_OF_YEAR, 4);
		endDate = new GregorianCalendar();
		endDate.add(Calendar.DAY_OF_YEAR, 5);
		Task task2 = new Task("some name", user, new TaskTimings(startDate, endDate, 1380), manager.getClock());
		// <task2> starts after 3 days and takes 23 hours. <task> takes another 2 hours
		// This dependency will not satisfy business rule 1
		task.addDependency(task2);		
	}
	
	/**
	 * Tests the behavior of Task.earliestEndTime()
	 * @throws TaskFailedException 
	 * @throws BusinessRule1Exception 
	 * @throws EmptyStringException 
	 * @throws DependencyCycleException 
	 * @throws IllegalStateCallException 
	 * @throws NullPointerException 
	 * @throws BusinessRule3Exception 
	 */
	@Test
	public void earliestEnd() throws TaskFailedException, EmptyStringException, BusinessRule1Exception, DependencyCycleException, NullPointerException, IllegalStateCallException, BusinessRule3Exception{
		//<task> takes two hours to complete, earliest end time is 2 hours after the start date
		GregorianCalendar earliestEnd = (GregorianCalendar) manager.getClock().getTime().clone();
		earliestEnd.add(Calendar.HOUR, 2);
		assertEquals(earliestEnd, task.earliestEndTime());
		
		startDate = (GregorianCalendar) manager.getClock().getTime().clone();
		startDate.add(Calendar.DAY_OF_YEAR, 1);
		endDate = (GregorianCalendar) manager.getClock().getTime().clone();
		endDate.add(Calendar.DAY_OF_YEAR, 5);
		Task task2 = new Task("some name", user, new TaskTimings(startDate, endDate, 1440), manager.getClock());
		task.addDependency(task2);
		//<task2> takes 24 hours to complete, <task> takes another 2.
		// Earliest end time should be 26 hours after the startDate of <task2>
		earliestEnd = startDate;
		earliestEnd.add(Calendar.HOUR, 26);
		assertEquals(earliestEnd, task.earliestEndTime());		
	}
	
	/**
	 * Tests the behavior of the non-recursive remove method.
	 * @throws BusinessRule1Exception 
	 * @throws EmptyStringException 
	 * @throws DependencyCycleException 
	 * @throws IllegalStateCallException 
	 * @throws NullPointerException 
	 * @throws BusinessRule3Exception 
	 */
	@Test
	public void remove() throws EmptyStringException, BusinessRule1Exception, DependencyCycleException, NullPointerException, IllegalStateCallException, BusinessRule3Exception{
		//Sets up a required resource, and a dependency in both directions
		task.addRequiredResource(resource);
		Task task2 = new Task("some dependency",user,new TaskTimings(startDate,endDate,120),manager.getClock());
		Task task3 = new Task("some dependentTask",user,new TaskTimings(startDate,endDate,120),manager.getClock());
		task.addDependency(task2);
		task3.addDependency(task);
		// remove the task
		task.remove();
		//Assures all links are broken
		assertFalse(task3.getDependencies().contains(task));
		assertFalse(task2.getDependentTasks().contains(task));
		assertFalse(user.getTasks().contains(task));
		assertFalse(resource.getTasksUsing().contains(task));
	}
	
	/**
	 * Tests the behavior of the recursive remove method.
	 * @throws EmptyStringException 
	 * @throws BusinessRule1Exception 
	 * @throws DependencyCycleException 
	 * @throws IllegalStateCallException 
	 * @throws NullPointerException 
	 * @throws BusinessRule3Exception 
	 */
	@Test
	public void removeRecursively() throws EmptyStringException, BusinessRule1Exception, DependencyCycleException, NullPointerException, IllegalStateCallException, BusinessRule3Exception{
		//Sets up 2 additional resources
		Resource resource2 = new Resource("some resource",ResourceType.Tool);
		Resource resource3 = new Resource("some other resource",ResourceType.Tool);
		//Sets up 3 additional tasks
		Task task2 = new Task("some dependency",user,new TaskTimings(startDate,endDate,120),manager.getClock());
		task2.addRequiredResource(resource2);
		Task task3 = new Task("some dependentTask",user,new TaskTimings(startDate,endDate,120),manager.getClock());
		task3.addRequiredResource(resource3);
		Task task4 = new Task("some other task",user, new TaskTimings(startDate, endDate,120),manager.getClock());
		task3.addDependency(task4);
		//Sets up a hierarchy of 3 levels
		task3.addDependency(task2);
		task2.addDependency(task);
		//Removes <task> recursively
		task.removeRecursively();
		//Checks that the user has no link to any of the first 3 tasks anymore
		assertFalse(user.getTasks().contains(task));
		assertFalse(user.getTasks().contains(task2));
		assertFalse(user.getTasks().contains(task3));
		//Check that <task4> is not deleted
		assertTrue(user.getTasks().contains(task4));
		//Check that the resource are no longer required, and that task4 has no reference to task3 anymore
		assertFalse(resource.getTasksUsing().contains(task));
		assertFalse(resource2.getTasksUsing().contains(task2));
		assertFalse(resource3.getTasksUsing().contains(task3));
		assertFalse(task4.getDependentTasks().contains(task3));
	}
	
	/**
	 * Testing if EarliestEndTime throws the error if failed
	 * @throws IllegalStateChangeException 
	 * @throws TaskFailedException 
	 */
	@Test(expected=TaskFailedException.class)
	public void testEarliestEndTime() throws IllegalStateChangeException, TaskFailedException
	{
		task.setFailed();
		task.earliestEndTime();
	}
	
	/**
	 * Testing if setDueDate throws the error if failed
	 * @throws NullPointerException
	 * @throws BusinessRule3Exception 
	 */
	@Test(expected=NullPointerException.class)
	public void testSetDueDate() throws NullPointerException, BusinessRule3Exception
	{
		task.setDueDate(null);
	}
	
	/**
	 * Testing if setStartDate throws the error if failed
	 * @throws NullPointerException
	 * @throws BusinessRule3Exception 
	 */
	@Test(expected=NullPointerException.class)
	public void testSetStartDate() throws NullPointerException, BusinessRule3Exception
	{
		task.setStartDate(null);
	}
	
	/**
	 * Testing if setUser throws the error if failed
	 * @throws NullPointerException
	 */
	@Test(expected=NullPointerException.class)
	public void testSetUser() throws NullPointerException
	{
		task.setUser(null);
	}

	/**
	 * Testing toString
	 */
	@Test
	public void testToString()
	{
		assertEquals("Descr", task.toString());
	}

	/**
	 * Testing DurationComparator
	 * @throws BusinessRule3Exception 
	 * @throws IllegalStateCallException 
	 * @throws BusinessRule1Exception 
	 * @throws EmptyStringException 
	 * @throws NullPointerException 
	 */
	@Test
	public void testDurationComparator() throws NullPointerException, EmptyStringException, BusinessRule1Exception, IllegalStateCallException, BusinessRule3Exception
	{
		Task t1 = new Task("d", user, new TaskTimings(startDate, endDate, 1), manager.getClock());
		Task t2 = new Task("d", user, new TaskTimings(startDate, endDate, 2), manager.getClock());
		
		TaskDurationComparator c = new TaskDurationComparator();
		assertEquals(-1, c.compare(t1, t2));
	}

	/**
	 * Testing DurationComparator
	 * @throws BusinessRule3Exception 
	 * @throws IllegalStateCallException 
	 * @throws BusinessRule1Exception 
	 * @throws EmptyStringException 
	 * @throws NullPointerException 
	 */
	@Test
	public void testDurationComparator2() throws NullPointerException, EmptyStringException, BusinessRule1Exception, IllegalStateCallException, BusinessRule3Exception
	{
		Task t1 = new Task("d", user, new TaskTimings(startDate, endDate, 2), manager.getClock());
		Task t2 = new Task("d", user, new TaskTimings(startDate, endDate, 1), manager.getClock());
		
		TaskDurationComparator c = new TaskDurationComparator();
		assertEquals(1, c.compare(t1, t2));
	}

	/**
	 * Testing DurationComparator
	 * @throws BusinessRule3Exception 
	 * @throws IllegalStateCallException 
	 * @throws BusinessRule1Exception 
	 * @throws EmptyStringException 
	 * @throws NullPointerException 
	 */
	@Test
	public void testDurationComparator3() throws NullPointerException, EmptyStringException, BusinessRule1Exception, IllegalStateCallException, BusinessRule3Exception
	{
		Task t1 = new Task("d", user, new TaskTimings(startDate, endDate, 1), manager.getClock());
		Task t2 = new Task("d", user, new TaskTimings(startDate, endDate, 1), manager.getClock());
		
		TaskDurationComparator c = new TaskDurationComparator();
		assertEquals(0, c.compare(t1, t2));
	}

	/**
	 * Testing DeadlineComparator
	 * @throws BusinessRule3Exception 
	 * @throws IllegalStateCallException 
	 * @throws BusinessRule1Exception 
	 * @throws EmptyStringException 
	 * @throws NullPointerException 
	 */
	@Test
	public void testDeadlineComparator() throws NullPointerException, EmptyStringException, BusinessRule1Exception, IllegalStateCallException, BusinessRule3Exception
	{
		GregorianCalendar end2 = (GregorianCalendar) endDate.clone();
		
		Task t1 = new Task("d", user, new TaskTimings(startDate, endDate, 1), manager.getClock());
		Task t2 = new Task("d", user, new TaskTimings(startDate, end2, 1), manager.getClock());
		
		TaskDeadlineComparator c = new TaskDeadlineComparator();
		assertEquals(0, c.compare(t1, t2));
	}

	/**
	 * Testing DeadlineComparator
	 * @throws BusinessRule3Exception 
	 * @throws IllegalStateCallException 
	 * @throws BusinessRule1Exception 
	 * @throws EmptyStringException 
	 * @throws NullPointerException 
	 */
	@Test
	public void testDeadlineComparator2() throws NullPointerException, EmptyStringException, BusinessRule1Exception, IllegalStateCallException, BusinessRule3Exception
	{
		GregorianCalendar end2 = (GregorianCalendar) endDate.clone();
		end2.add(Calendar.DAY_OF_WEEK, 1);
		
		Task t1 = new Task("d", user, new TaskTimings(startDate, endDate, 1), manager.getClock());
		Task t2 = new Task("d", user, new TaskTimings(startDate, end2, 1), manager.getClock());
		
		TaskDeadlineComparator c = new TaskDeadlineComparator();
		assertEquals(-1, c.compare(t1, t2));
	}

	/**
	 * Testing DeadlineComparator
	 * @throws BusinessRule3Exception 
	 * @throws IllegalStateCallException 
	 * @throws BusinessRule1Exception 
	 * @throws EmptyStringException 
	 * @throws NullPointerException 
	 */
	@Test
	public void testDeadlineComparator3() throws NullPointerException, EmptyStringException, BusinessRule1Exception, IllegalStateCallException, BusinessRule3Exception
	{
		GregorianCalendar end2 = (GregorianCalendar) endDate.clone();
		end2.add(Calendar.DAY_OF_WEEK, -1);
		
		Task t1 = new Task("d", user, new TaskTimings(startDate, endDate, 1), manager.getClock());
		Task t2 = new Task("d", user, new TaskTimings(startDate, end2, 1), manager.getClock());
		
		TaskDeadlineComparator c = new TaskDeadlineComparator();
		assertEquals(1, c.compare(t1, t2));
	}
}
