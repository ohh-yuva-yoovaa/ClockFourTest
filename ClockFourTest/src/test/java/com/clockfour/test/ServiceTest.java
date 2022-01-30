package com.clockfour.test;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.web.multipart.MultipartFile;

import com.clockfour.controller.TestController;
import com.clockfour.model.Employee;
import com.clockfour.model.EmployeesHolder;
import com.clockfour.model.Response;
import com.clockfour.service.TestService;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class, loader = AnnotationConfigContextLoader.class)
public class ServiceTest extends TestCase {
	
	@Autowired
	TestController testController;
	
	@Autowired
	TestService testService;
	
	EmployeesHolder e;
	String [] postiveNameTestValues;
	String [] negativeNameTestValues;
	MultipartFile file;
	String eStr;
	
	@Before
	@Override
	public void setUp() throws Exception{
    	postiveNameTestValues = new String[] {"John", "Jane", "Sam", "samAntha"};
    	negativeNameTestValues = new String[] {"Jack1", "Jane.", "S@m", "!samantha"};
    	e = new EmployeesHolder();
    	e.setEmployees(new Employee [] {
    			new Employee("John","Doe","11/12/1990"),
    			new Employee("Jane","Moe","12/11/1980"),
    			new Employee("Jack","Mae","13/13/1990"),
    			new Employee("Jill","Ray","10/10/1970"),
    	});
    	file = new MockMultipartFile("test.png", "test.png", "image/png", new byte[] {10, 20, 30, 40});
    	eStr = "<employees> <employee> <firstname>a1</firstname> <lastname>b</lastname> <dob>c</dob> </employee> </employees>";
	}
	
    @Test
    public void testFirstNameValidation() {
    	Employee e = new Employee();
    	for(String value : postiveNameTestValues) {
	    	e.setFirstname(value);
	    	assertTrue(e.getFirstname().equals(value));
    	}
    	
    	for(String value : negativeNameTestValues) {
	    	e.setFirstname(value);
	    	assertTrue(!e.getFirstname().equals(value));
    	}
    }
    
    @Test
    public void testLastNameValidation() {
    	Employee e = new Employee();
    	for(String value : postiveNameTestValues) {
	    	e.setLastname(value);
	    	assertTrue(e.getLastname().equals(value));
    	}
    	
    	for(String value : negativeNameTestValues) {
	    	e.setLastname(value);
	    	assertTrue(!e.getLastname().equals(value));
    	}
    }
    
    @Test
    public void testWriteExcel() {
	    assertTrue(testService.writeExcel(e));    
    }

    @Test
    public void testWritePDF() {
	    assertTrue(testService.writeExcel(e));    
    }
    
    @Test
    public void testProcessEmployees() {
	    assertTrue(testService.processEmployees(e));    
    }
    
    @Test
    public void testProcessImage() {
	    assertTrue(testService.processImage(file));    
    }
    
    @Test
    public void testProcess() {
	    Response r = testService.process(file, e);
	    assertTrue(r.getImageName().equals(file.getOriginalFilename()));
	    assertTrue(r.getImageFileSize() == file.getSize());
	    assertTrue(r.getEmployees()!= null && r.getEmployees().length == e.getEmployees().length);
    }
    
    @Test
    public void testController() {
	    Response r = testController.process(file, eStr);
	    assertTrue(r.getImageName().equals(file.getOriginalFilename()));
	    assertTrue(r.getImageFileSize() == file.getSize());
	    assertTrue(r.getEmployees()!= null && r.getEmployees().length == 1);
    }
}
