package controller;

import java.text.ParseException;

import javax.naming.NameNotFoundException;

import org.w3c.dom.DOMException;

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
import model.User;
import model.XMLParser;

public class XMLController {
	
	/**
	 * Parses the given XML file
	 * @param filename
	 * @param controller
	 * @return
	 * @throws NameNotFoundException
	 * @throws DOMException
	 * @throws NullPointerException
	 * @throws EmptyStringException
	 * @throws ParseException
	 * @throws BusinessRule1Exception
	 * @throws DependencyCycleException
	 * @throws DependencyException
	 * @throws IllegalStateCallException
	 * @throws BusinessRule3Exception
	 * @throws NotAvailableException
	 * @throws UnknownStateException
	 * @throws IllegalStateChangeException
	 * @throws BusinessRule2Exception
	 */
	public User parse(String filename, DispatchController controller) throws NameNotFoundException, DOMException, NullPointerException, EmptyStringException, ParseException, BusinessRule1Exception, DependencyCycleException, DependencyException, IllegalStateCallException, BusinessRule3Exception, NotAvailableException, UnknownStateException, IllegalStateChangeException, BusinessRule2Exception
	{
		XMLParser parser = new XMLParser(filename, controller);
		
		return parser.Parse();
	}
}