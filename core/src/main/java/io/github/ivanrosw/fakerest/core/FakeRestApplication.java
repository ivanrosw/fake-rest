package io.github.ivanrosw.fakerest.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "io.github.ivanrosw.fakerest")
public class FakeRestApplication {

	public static void main(String[] args) {
		SpringApplication.run(FakeRestApplication.class, args);
	}

}
