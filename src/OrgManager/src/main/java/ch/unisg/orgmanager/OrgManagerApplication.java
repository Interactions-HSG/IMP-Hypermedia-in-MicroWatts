package ch.unisg.orgmanager;

import ch.unisg.orgmanager.config.CoapServerConfig;
import ch.unisg.orgmanager.controller.coap.AgentResource;
import ch.unisg.orgmanager.controller.coap.GroupsResource;
import ch.unisg.orgmanager.controller.coap.RoleResource;
import ch.unisg.orgmanager.controller.coap.RolesResource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrgManagerApplication {

	public static void main(String[] args) {

		System.out.println("Starting server...");
		SpringApplication.run(OrgManagerApplication.class, args);

		System.out.println("Setting up Coap");

		CoapServerConfig server = CoapServerConfig.getInstance();

		server.add(new RolesResource("roles"));
		server.add(new AgentResource("agents"));
		server.add(new GroupsResource("groups"));

		server.start();

		/*
		System.out.println("Setting up Yggdrasil");
		YggdrasilConfig yggdrasil = new YggdrasilConfig();
		 */
	}

}
