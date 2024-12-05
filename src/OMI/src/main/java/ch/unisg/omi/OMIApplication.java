package ch.unisg.omi;

import ch.unisg.omi.config.CoapServerConfig;
import ch.unisg.omi.config.YggdrasilConfig;
import ch.unisg.omi.controller.coap.RolesResource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OMIApplication {

	public static void main(String[] args) {

		System.out.println("Starting server...");
		SpringApplication.run(OMIApplication.class, args);

		System.out.println("Setting up Coap...");
		CoapServerConfig server = CoapServerConfig.getInstance();
		server.add(new RolesResource("roles"));
		server.start();

		System.out.println("Setting up Yggdrasil...");
		YggdrasilConfig yggdrasil = YggdrasilConfig.getInstance();

		yggdrasil.subscribe("http://yggdrasil:8080/workspaces/", "http://omi:7500/workspaces");
	}
}
