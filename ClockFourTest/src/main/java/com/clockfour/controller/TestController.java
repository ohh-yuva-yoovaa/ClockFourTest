package com.clockfour.controller;

import java.io.IOException;
import java.io.StringReader;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.clockfour.model.EmployeesHolder;
import com.clockfour.model.Response;
import com.clockfour.service.TestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

@RestController
public class TestController {

	@Autowired
	TestService service;
	
	static final Logger log = Logger.getLogger(TestService.class);
	
	@RequestMapping(value = "/test", method = RequestMethod.POST, headers = "Accept=application/json")
	@ResponseBody 
	public Response process(@RequestParam("image") MultipartFile image, @RequestParam("xml") String xml) {
		log.info("Received image file " + image.getOriginalFilename() + " with size " + image.getSize());

		EmployeesHolder employees = null;

		JacksonXmlModule xmlModule = new JacksonXmlModule();
		xmlModule.setDefaultUseWrapper(false);
		ObjectMapper objectMapper = new XmlMapper(xmlModule);
		
		try {
	        employees = objectMapper.readValue(new StringReader(xml), EmployeesHolder.class);
			log.info("Received proper employees xml");
		} catch (IOException e) {
			log.error(e);
		}
		
		return service.process(image, employees);
	}
}