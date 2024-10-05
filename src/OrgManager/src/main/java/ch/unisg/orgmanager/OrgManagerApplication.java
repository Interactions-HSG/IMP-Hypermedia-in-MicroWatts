package ch.unisg.orgmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrgManagerApplication {

	public static void main(String[] args) {

		System.out.println("Starting server...");
		SpringApplication.run(OrgManagerApplication.class, args);

		System.out.println("Setting up Moise");

		System.out.println("Setting up Coap");

		System.out.println("Setting up Yggdrasil");
	}

}
