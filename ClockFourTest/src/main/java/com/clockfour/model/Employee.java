package com.clockfour.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "employee") 
public class Employee {
	private String firstname;
	private String lastname;
	private String dob;
	
	SimpleDateFormat sdf = new SimpleDateFormat("MM/DD/yyyy");

	public Employee() {
		sdf.setLenient(false);
	}

	public Employee(String firstname, String lastname, String dob) {
		super();
		this.firstname = firstname;
		this.lastname = lastname;
		this.dob = dob;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstName) {
		if(!firstName.matches("[a-zA-Z]+"))
			firstName = "Invalid non alpha value";
		this.firstname = firstName;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastName) {
		if(!lastName.matches("[a-zA-Z]+"))
			lastName = "Invalid non alpha value";
		this.lastname = lastName;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		try {
			sdf.parse(dob);
			this.dob = dob;
		} catch (ParseException e) {
			this.dob = "Invalid format, requires MM/DD/YYYY";
		}
	}

	@Override
	public String toString() {
		return "Employee [firstname=" + firstname + ", lastname=" + lastname + ", dob=" + dob + "]";
	}
	
	public String toPDString() {
		return firstname + "|" + lastname + "|" + dob;
	}
}
