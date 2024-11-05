package ch.unisg.omi;

import ch.unisg.omi.config.CoapServerConfig;
import ch.unisg.omi.config.YggdrasilConfig;
import ch.unisg.omi.controller.coap.AgentResource;
import ch.unisg.omi.controller.coap.GroupsResource;
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
		server.add(new AgentResource("agents"));
		server.add(new GroupsResource("groups"));
		server.start();

		System.out.println("Setting up Yggdrasil...");
		YggdrasilConfig yggdrasil = new YggdrasilConfig();
		yggdrasil.subscribe();

	}

}
