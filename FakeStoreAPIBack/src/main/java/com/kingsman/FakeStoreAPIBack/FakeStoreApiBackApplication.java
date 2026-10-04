package com.kingsman.FakeStoreAPIBack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

// @EnableScheduling activa las tareas con @Scheduled (ej. OrderStatusScheduler)
@SpringBootApplication
@EnableScheduling
public class FakeStoreApiBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(FakeStoreApiBackApplication.class, args);
	}

}
