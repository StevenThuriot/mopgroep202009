package model;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import model.repositories.RepositoryManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import exception.BusinessRule1Exception;
import exception.BusinessRule3Exception;
import exception.EmptyStringException;
import exception.IllegalStateCallException;
import exception.AssetAllocatedException;
import exception.InvitationInvitesOwnerException;
import exception.WrongFieldsForChosenTypeException;
public class UserTaskManagerTest {

	/**
	 * The user we'll be using
	 */
	private User user;
	/**
	 * A task we can use
	 */
	private Task taskMain;
	/**
	 * A repositoryManager for the time
	 */
	private RepositoryManager manager;
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws NullPointerException, EmptyStringException, BusinessRule1Exception, IllegalStateCallException, BusinessRule3Exception, WrongFieldsForChosenTypeException
	{
		user = new User("Bart",new UserType(""));
		manager = new RepositoryManager();
		GregorianCalendar startDate = new GregorianCalendar();
		GregorianCalendar endDate = new GregorianCalendar();
		endDate.add(Calendar.DAY_OF_YEAR, 4);
		// 4 days to finish the task from now on
		int duration = 1;
		TaskType taskType = new TaskType("reorganizing the test cases", 
				new ArrayList<Field>(), new ArrayList<TaskTypeConstraint>());
		taskMain = TaskFactory.createTask("Main Task", taskType, new ArrayList<Field>(),
				user, new TaskTimings(startDate, endDate, duration), manager.getClock());
	}
	@After
	public void tearDown()
	{
		user = null;
	}
	@Test
	public void adding() throws AssetAllocatedException, InvitationInvitesOwnerException, IllegalStateCallException
	{
		User user1 = new User("John",new UserType(""));
		Invitation invitation = new Invitation(taskMain, user1);
		assertTrue(user1.getUserTaskManager().getInvitations().contains(invitation));
	}
	@Test
	public void removing() throws AssetAllocatedException, InvitationInvitesOwnerException, IllegalStateCallException
	{
		User user1 = new User("John",new UserType(""));
		Invitation invitation = new Invitation(taskMain, user1);
		assertTrue(user1.getUserTaskManager().getInvitations().contains(invitation));
		user1.removeInvitation(invitation);
		assertFalse(user1.getUserTaskManager().getInvitations().contains(invitation));
	}
}
