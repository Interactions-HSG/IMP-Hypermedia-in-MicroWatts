package ch.unisg.orgmanager;

import ch.unisg.orgmanager.config.CoapServerConfig;
import ch.unisg.orgmanager.config.MoiseConfig;
import ch.unisg.orgmanager.config.YggdrasilConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrgManagerApplication {

	public static void main(String[] args) {

		System.out.println("Starting server...");
		SpringApplication.run(OrgManagerApplication.class, args);

		System.out.println("Setting up Moise");
		MoiseConfig moise = new MoiseConfig("org.xml");

		System.out.println("Setting up Coap");
		CoapServerConfig server = new CoapServerConfig(true, false, 5686);
		server.start();

		System.out.println("Setting up Yggdrasil");
		YggdrasilConfig yggdrasil = new YggdrasilConfig();
	}

}
