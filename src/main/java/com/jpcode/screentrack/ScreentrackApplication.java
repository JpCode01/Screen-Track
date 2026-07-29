package com.jpcode.screentrack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ScreentrackApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScreentrackApplication.class, args);
	}

}
