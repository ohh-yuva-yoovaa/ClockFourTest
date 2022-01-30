package com.clockfour.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.clockfour.controller.TestController;
import com.clockfour.service.TestService;

@Configuration
@ComponentScan
@PropertySource("classpath:config.properties")
public class TestConfig {
	@Bean
	public TestService getTestService() {
		return new TestService();
	}
	
	@Bean
	public TestController getTestController() {
		return new TestController();
	}

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
		return new PropertySourcesPlaceholderConfigurer();
	}
}
