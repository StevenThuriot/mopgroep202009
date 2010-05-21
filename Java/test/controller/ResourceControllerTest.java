package controller;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import model.Field;
import model.Reservation;
import model.Resource;
import model.ResourceType;
import model.Task;
import model.TaskFactory;
import model.TaskTimings;
import model.TaskType;
import model.TaskTypeConstraint;
import model.User;
import model.UserType;
import model.repositories.RepositoryManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import controller.ResourceController;
import exception.AssetAllocatedException;
import exception.EmptyStringException;
import exception.IllegalStateCallException;
import exception.NoReservationOverlapException;
import exception.NotAvailableException;
import exception.ResourceBusyException;

public class ResourceControllerTest {

	private ResourceController controller;
	private Resource resource;
	private User user;
	private RepositoryManager manager;
	private Task task;
	
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		manager = new RepositoryManager();
		controller = new ResourceController(manager);
		resource = new Resource("Room 101", new ResourceType(""));
		user = new User("John",new UserType(""));
		GregorianCalendar endDate = new GregorianCalendar();
		endDate.add(Calendar.DAY_OF_YEAR, 4);
		manager = new RepositoryManager();
		// 4 days to finish task
		TaskType taskType = new TaskType("reorganizing the test cases", 
				new ArrayList<Field>(), new ArrayList<TaskTypeConstraint>());
		task = TaskFactory.createTask("Descr", taskType, new ArrayList<Field>(),
				user, new TaskTimings(new GregorianCalendar(),endDate,1440), manager.getClock());
	}
	
	/**
     * Test for nullpointerexception on instantiating the controller with null as manager
     */
    @Test(expected=NullPointerException.class)
    public void createNullController()
    {
        controller = new ResourceController(null);
    }

	@After
	public void tearDown() throws Exception {
		controller = null;
		resource = null;
		user = null;
	}
	
	/**
	 * Create a correct reservation
	 * TODO: Duration aanpassen
	 * @throws NotAvailableException 
	 * @throws AssetAllocatedException 
	 * @throws NoReservationOverlapException 
	 * @throws IllegalStateCallException 
	 */
	@Test
	public void createReservation() throws NotAvailableException, NoReservationOverlapException, AssetAllocatedException, IllegalStateCallException
	
	{
		GregorianCalendar begin = new GregorianCalendar();
		Reservation reservatie = controller.createReservation(begin, 140, resource, task);
		assertEquals(reservatie, resource.getReservations().get(0));
	}
	
	/**
	 * Create an overlapping reservation. Should throw a NotAvailableException
	 * @throws NotAvailableException
	 * @throws AssetAllocatedException 
	 * @throws NoReservationOverlapException 
	 * @throws IllegalStateCallException 
	 */
	@Test(expected=NotAvailableException.class)
	public void createNoReservation() throws NotAvailableException, NoReservationOverlapException, AssetAllocatedException, IllegalStateCallException
	{
		controller.createReservation(new GregorianCalendar(), 140, resource, task);
		controller.createReservation(new GregorianCalendar(), 30, resource, task);
	}
	
	/**
	 * Create a resource and look it up in the ResourceManager
	 * @throws EmptyStringException
	 */
	@Test
	public void createResource() throws EmptyStringException
	{
		resource = controller.createResource("Room 101", new ResourceType(""));
	    assertTrue(manager.getResources().contains(resource));
	}
	
	/**
	 * Test the existence of a created resource throught the controller
	 * @throws EmptyStringException
	 */
	@Test
	public void testGetResources() throws EmptyStringException
	{
	    resource = controller.createResource("Room 101", new ResourceType(""));
	    assertTrue(controller.getResources().contains(resource));
	}
	
	/**
	 * Create a resource, create a reservation on that resource
	 * Get a general list of reservations, see of the reservation contains the one we created
	 * @throws NotAvailableException 
	 * @throws EmptyStringException 
	 * @throws AssetAllocatedException 
	 * @throws NoReservationOverlapException 
	 * @throws IllegalStateCallException 
	 */
	@Test
	public void testReservations() throws NotAvailableException, EmptyStringException, NoReservationOverlapException, AssetAllocatedException, IllegalStateCallException
	{
	    resource = controller.createResource("Room 101", new ResourceType(""));
	    Reservation reservation = controller.createReservation(new GregorianCalendar(), 101, resource, task);
	    assertTrue(resource.getReservations().contains(reservation));
	}
	
	@Test
	public void createTypeTest()
	{
		ResourceType type = controller.createResourceType("abc", "Alfabet resource");
		assertEquals(type,manager.getResourceTypeById("abc"));
	}
}
