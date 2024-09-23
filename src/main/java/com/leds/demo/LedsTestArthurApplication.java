package com.leds.demo;

import com.leds.demo.Controller.ControllerConcurso;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.IOException;

@SpringBootApplication
public class LedsTestArthurApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(LedsTestArthurApplication.class, args);

		ControllerConcurso controllerConcurso = context.getBean(ControllerConcurso.class);

		try {
			controllerConcurso.init();
		} catch (IOException e) {
			e.printStackTrace(); // Ou trate o erro como preferir
		}
	}
}
