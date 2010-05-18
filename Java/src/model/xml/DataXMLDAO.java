package model.xml;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

import javax.naming.NameNotFoundException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import controller.*;

import model.Project;
import model.Resource;
import model.ResourceType;
import model.Task;
import model.TaskTimings;
import model.User;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import exception.BusinessRule1Exception;
import exception.BusinessRule2Exception;
import exception.BusinessRule3Exception;
import exception.DependencyCycleException;
import exception.DependencyException;
import exception.EmptyStringException;
import exception.IllegalStateCallException;
import exception.IllegalStateChangeException;
import exception.NotAvailableException;
import exception.UnknownStateException;

/**
 * Usage: Make new XMLParser object and pass along the file location. 
 * Ask the object to Parse() and it gives back the user.
 */
public class DataXMLDAO {
	private XMLParser parser;
	
	private DispatchController controller;
	
	private HashMap<String, Project> projectMap = new HashMap<String, Project>();
	private HashMap<String, Task> taskMap = new HashMap<String, Task>();
	private HashMap<String, Resource> resourceMap = new HashMap<String, Resource>();
	
	/**
	 * Constructor
	 * @param filename
	 */
	public DataXMLDAO(String filename, DispatchController controller)
	{
		this.controller = controller;
		this.parser = new XMLParser(filename);
	}

	
	/**
	 * Creates the all elements in the XML file and gives back a user.
	 * @return
	 * @throws NameNotFoundException
	 * @throws DOMException
	 * @throws EmptyStringException
	 * @throws ParseException 
	 * @throws DependencyCycleException 
	 * @throws BusinessRule1Exception 
	 * @throws DependencyException 
	 * @throws IllegalStateCallException 
	 * @throws NullPointerException 
	 * @throws BusinessRule3Exception 
	 * @throws NotAvailableException 
	 * @throws UnknownStateException 
	 * @throws BusinessRule2Exception 
	 * @throws IllegalStateChangeException 
	 */
	public User Parse() throws NameNotFoundException, DOMException, EmptyStringException, ParseException, BusinessRule1Exception, DependencyCycleException, DependencyException, NullPointerException, IllegalStateCallException, BusinessRule3Exception, NotAvailableException, UnknownStateException, IllegalStateChangeException, BusinessRule2Exception
	{
		Node userNode = parser.getNodeByName(parser.getRootNode(), "mop:user");
		Node userName = parser.getNodeByName(userNode, "mop:name");
		
		User user = new User(userName.getTextContent());
		
		
		parseResources();
		
		parseProjects();
		
		parseReservations(userNode, user);

		parseTasks(userNode, user);
		
		return user;
	}

	private void parseTasks(Node userNode, User user) throws NameNotFoundException, ParseException, EmptyStringException, BusinessRule1Exception,
			DependencyCycleException, IllegalStateCallException, BusinessRule3Exception, UnknownStateException, IllegalStateChangeException, BusinessRule2Exception {
		Node tasks = parser.getNodeByName(userNode, "mop:tasks");
		NodeList taskList = tasks.getChildNodes();
		
		HashMap<Task, String> stateMap = new HashMap<Task, String>();
		injectTasks(user, taskList, stateMap);
		
		linkDepedencies(taskList);
		
		setTaskStates(stateMap);
	}

	private void setTaskStates(HashMap<Task, String> stateMap) throws UnknownStateException, BusinessRule3Exception, IllegalStateChangeException, BusinessRule2Exception {
		//Set states
		for (Task task : stateMap.keySet()) {
				controller.getTaskController().parseStateString(task, stateMap.get(task));
		}
	}

	private void linkDepedencies(NodeList taskList) throws NameNotFoundException, IllegalStateCallException, BusinessRule1Exception, DependencyCycleException {
		//Link dependencies and resources
		for (int i = 0; i < taskList.getLength(); i++) {
			Node childNode = taskList.item(i);
			
			if (childNode.getNodeName() != "#text" && childNode.getNodeName().length() > 0)
		    {
				String id = childNode.getAttributes().item(0).getTextContent();
				
				Node resNode = parser.getNodeByName(childNode, "mop:requiredResources");
				NodeList resList = resNode.getChildNodes();
				
				ArrayList<Resource> requiredResources = new ArrayList<Resource>();
				
				for (int j = 0; j < resList.getLength(); j++) {
					Node resChild = resList.item(j);
					if (resChild.getNodeName() != "#text" && resChild.getNodeName().length() > 0) {
						String requiredResourceID = resChild.getTextContent();
						
						if(requiredResourceID.length() > 0)
						{
							requiredResources.add(resourceMap.get(requiredResourceID));
						}
					}
				}
				
				Node dependsNode = parser.getNodeByName(childNode, "mop:dependsOn");
				NodeList dependsList = dependsNode.getChildNodes();		    
				ArrayList<Task> dependencyList = new ArrayList<Task>();
				
				for (int j = 0; j < dependsList.getLength(); j++) {
					Node depChild = dependsList.item(j);
					if (depChild.getNodeName() != "#text" && depChild.getNodeName().length() > 0) {
						String requiredTaskID = depChild.getTextContent();

						if(requiredTaskID.length() > 0)
						{
							dependencyList.add(taskMap.get(requiredTaskID));
						}
					}
				}
				
				Task task = taskMap.get(id);				
				
				if (requiredResources.size() > 0) {
					for (Resource r : requiredResources)
						task.addRequiredResource(r);
				}
				
				if (dependencyList.size() > 0) {
					for (Task t : dependencyList)
						task.addDependency(t);
				}
		    }
		}
	}

	private void injectTasks(User user, NodeList taskList, HashMap<Task, String> stateMap) throws NameNotFoundException, ParseException, EmptyStringException,
			BusinessRule1Exception, DependencyCycleException, IllegalStateCallException, BusinessRule3Exception {
		//Inject tasks
		for (int i = 0; i < taskList.getLength(); i++) {
			Node childNode = taskList.item(i);
			
			if (childNode.getNodeName() != "#text" && childNode.getNodeName().length() > 0)
		    {
				String id = childNode.getAttributes().item(0).getTextContent();
				String description = parser.getNodeByName(childNode, "mop:description").getTextContent();
				String startString = parser.getNodeByName(childNode, "mop:startDate").getTextContent();
			    			    
			    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
			    Date start = sdf.parse(startString);
			    
			    GregorianCalendar startDate = new GregorianCalendar();
			    startDate.setTime(start);
			    
			    String dueString = parser.getNodeByName(childNode, "mop:endDate").getTextContent();  
			    Date due = sdf.parse(dueString);
			    GregorianCalendar dueDate = new GregorianCalendar();
			    dueDate.setTime(due);
			    
			    int duration = Integer.parseInt(parser.getNodeByName(childNode, "mop:duration").getTextContent());
			    
			    String state = parser.getNodeByName(childNode, "mop:status").getTextContent();
			    
			    String projectID = parser.getNodeByName(childNode, "mop:refProject").getTextContent();
				
			    Task task = controller.getTaskController().createTask(description, new TaskTimings(startDate, dueDate, duration), user);
			    
			    stateMap.put(task, state);
			    			    
			    if (projectID.length() > 0 && projectID != null)
			    {
				    Project project = projectMap.get(projectID);
				    controller.getProjectController().bind(project, task);
			    }
			    
			    taskMap.put(id, task);
		    }
		}
	}

	private void parseReservations(Node userNode, User user) throws NameNotFoundException, ParseException, NotAvailableException {
		//Make reservations
		Node reservations = parser.getNodeByName(userNode, "mop:reservations");
		NodeList reservationList = reservations.getChildNodes();
		for (int i = 0; i < reservationList.getLength(); i++) {
			Node childNode = reservationList.item(i);
			
			if (childNode.getNodeName() != "#text")
		    {
				String startString = parser.getNodeByName(childNode, "mop:time").getTextContent();
			    
			    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
			    Date start = sdf.parse(startString);
			    
			    GregorianCalendar startTime = new GregorianCalendar();
			    startTime.setTime(start);
				
				Integer duration = Integer.parseInt( parser.getNodeByName(childNode, "mop:duration").getTextContent() );
				String refResource = parser.getNodeByName(childNode, "mop:refResource").getTextContent();
				
				Resource resource = resourceMap.get(refResource);
				
				controller.getResourceController().createReservation(startTime, duration, resource, user);	
		    }
		}
	}

	private void parseProjects() throws NameNotFoundException, EmptyStringException {
		//Inject projects
		Node projects = parser.getNodeByName(parser.getRootNode(), "mop:projects");
		NodeList projectList = projects.getChildNodes();
		for (int i = 0; i < projectList.getLength(); i++) {
			Node childNode = projectList.item(i);
			
			if (childNode.getNodeName() != "#text")
		    {
				String id = childNode.getAttributes().item(0).getTextContent();
				String description = parser.getNodeByName(childNode, "mop:description").getTextContent();
				
				projectMap.put(id, controller.getProjectController().createProject(description));				
		    }
		}
	}

	private void parseResources() throws NameNotFoundException, EmptyStringException {
		//Get Resources
		Node resources = parser.getNodeByName(parser.getRootNode(), "mop:resources");
		NodeList resourceList = resources.getChildNodes();
		for (int i = 0; i < resourceList.getLength(); i++) {
			Node childNode = resourceList.item(i);
			
			if (childNode.getNodeName() != "#text")
		    {
				String id = childNode.getAttributes().item(0).getTextContent();
				String description = parser.getNodeByName(childNode, "mop:description").getTextContent();
				ResourceType type = ResourceType.valueOf(parser.getNodeByName(childNode, "mop:type").getTextContent());
				 
				resourceMap.put(id, controller.getResourceController().createResource(description, type));
		    }
		}
	}
}