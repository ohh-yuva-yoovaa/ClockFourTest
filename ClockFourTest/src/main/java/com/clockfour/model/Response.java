package com.clockfour.model;

import java.util.Arrays;

public class Response {
	String imageName;
	long imageFileSize;
	Employee [] employees;
	
	public String getImageName() {
		return imageName;
	}
	public void setImageName(String name) {
		this.imageName = name;
	}
	public long getImageFileSize() {
		return imageFileSize;
	}
	public void setImageFileSize(long imageFileSize) {
		this.imageFileSize = imageFileSize;
	}
	public Employee[] getEmployees() {
		return employees;
	}
	public void setEmployees(Employee[] employees) {
		this.employees = employees;
	}
	@Override
	public String toString() {
		return "Response [imageName=" + imageName + ", imageFileSize=" + imageFileSize + ", employees="
				+ Arrays.toString(employees) + "]";
	}
}
