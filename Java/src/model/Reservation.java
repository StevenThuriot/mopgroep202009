package model;

import java.util.Calendar;
import java.util.GregorianCalendar;

import exception.AssetAllocatedException;
import exception.InvitationInvitesOwnerException;
import exception.NoReservationOverlapException;
import exception.NotAvailableException;

public class Reservation implements AssetAllocation{
	/**
	 * The resource for which this reservation is made.
	 */
	private Resource reservedResource;
	
	/**
	 * The time for which this reservation is made.
	 */
	private GregorianCalendar time;
	
	/**
	 * The duration of this reservation.
	 */
	private int duration;
	
	/**
	 * The task to whom the resource is reserved
	 */
	private Task task;

	/**
	 * Makes a new Reservation with the given time, duration and resource, and
	 * for the given user.
	 * @param 	newUser
	 * 			The user for which this reservation is made.
	 * @param 	newTime
	 * 			The time for which this reservation is made. 
	 * @param 	newDuration
	 * 			The duration the reservation will be made for.
	 * @param 	newResource
	 * 			The resource that the reservation applies to.
	 * @throws 	NotAvailableException
	 * 			The resource is not available at the given time.
	 * 			|!newResource.availableAt(newTime)
	 * @throws  NoReservationOverlapException 
	 * 			This reservation does not have a overlapping time span with the other reservations
	 * 			|!newTask.checkOverlap(newTime, newDuration)
	 * @throws AssetAllocatedException 
	 */
	//TODO : update status of resource object
	public Reservation(Task task, GregorianCalendar newTime, int newDuration, Resource newResource) throws NotAvailableException, NoReservationOverlapException, AssetAllocatedException{
		setTime(newTime);
		setDuration(newDuration);
		setTask(task);
		
		if(!newResource.availableAt(newTime, newDuration))
			throw new NotAvailableException(
					"This resource is not available at the given time.");
		if(!getTask().checkOverlap(newTime, newDuration))
			throw new NoReservationOverlapException("This reservation does not have a overlapping time span with the other reservations");
		
		getTask().addAssetAllocation(this);
		newResource.addReservation(this);		
		setReservedResource(newResource);
	}
	


	private void setTask(Task task) {
		this.task = task;
	}


//	/**
//	 * Sets the user that made this reservation.
//	 * @post	| new.getUser() == newUser
//	 */
//	private void setUser(User newUser) throws NullPointerException{		
//		if (newUser == null)
//			throw new NullPointerException("Null was passed");
//		
//		user = newUser;
//	}
	
	/**
	 * Returns the start date for this reservation.
	 */
	public GregorianCalendar getTime(){
		return (GregorianCalendar) time.clone();
	}
	
	/**
	 * Sets the start date for this reservation to be <newTime>.
	 * @post	| new.getTime() == newTime
	 */
	private void setTime(GregorianCalendar newTime) throws NullPointerException{
		if (newTime == null)
			throw new NullPointerException("Null was passed");
		time = (GregorianCalendar) newTime.clone();
	}
	
	/**
	 * Returns the duration of this reservation.
	 * This is expressed in minutes.
	 */
	public int getDuration(){
		return duration;
	}
	
	/**
	 * Sets the duration of this reservation to be <newDuration>.
	 * This is expressed in minutes.
	 * @post	| new.getDuration() == newDuration
	 */
	private void setDuration(int newDuration){		
		duration = newDuration;
	}
	
	/**
	 * Returns the resource that is reservation is made for.
	 */
	public Resource getReservedResource(){
		return reservedResource;
	}
	
	/**
	 * Sets the resource reserved by this reservation to be <newResource>.
	 * @post	|new.getReservedResource() == newResource
	 */
	private void setReservedResource(Resource newResource) throws NullPointerException{
		if (newResource == null)
			throw new NullPointerException("Null was passed");
		
		reservedResource = newResource;
	}


	@Override
	public boolean isAvailableAt(GregorianCalendar begin, int duration) {
		GregorianCalendar end = (GregorianCalendar) begin.clone();
		end.add(Calendar.MINUTE, duration);
		GregorianCalendar endReservation = (GregorianCalendar) this.getTime().clone();
		endReservation.add(Calendar.MINUTE, duration);
		if(this.getTime().compareTo(begin)<=0 && endReservation.compareTo(end)>=0 )
			return true;
		return false;
	}


	@Override
	public Task getTask() {
		return task;
	}


	@Override
	public boolean hasOverlap(GregorianCalendar begin, int duration) {
		GregorianCalendar end = (GregorianCalendar) begin.clone();
		end.add(Calendar.MINUTE, duration);
		GregorianCalendar endReservation = (GregorianCalendar) this.getTime().clone();
		endReservation.add(Calendar.MINUTE, duration);
		if(this.getTime().compareTo(end)<=0 && endReservation.compareTo(begin)>=0 )
			return true;
		return false;
	}
	
//	/**
//	 * Returns the user that made this reservation.
//	 */
//	public User getUser(){
//		return user;
//	}
}
