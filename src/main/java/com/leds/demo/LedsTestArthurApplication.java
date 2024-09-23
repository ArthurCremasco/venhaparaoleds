package com.leds.demo;

import com.leds.demo.Controller.ControllerConcurso;
import com.leds.demo.Controller.ControllerPessoa;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.IOException;

@SpringBootApplication
public class LedsTestArthurApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(LedsTestArthurApplication.class, args);
		LedsTestArthurApplication app = new LedsTestArthurApplication();

		try {
			app.execute(context);
		} catch (IOException e) {
			// Trate o erro conforme necessário
		} catch (Exception e) {
			// Tratamento para outras exceções
		}
	}



	private void execute(ApplicationContext context) throws IOException {
		ControllerPessoa controllerPessoa = context.getBean(ControllerPessoa.class);
		ControllerConcurso controllerConcurso = context.getBean(ControllerConcurso.class);

		// Chama as funções principais
		controllerPessoa.readPessoasFromFile();
		controllerConcurso.readConcursosFromFile();
	}
}
