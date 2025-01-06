package ch.unisg.omi;

import ch.unisg.omi.config.CoapServerConfig;
import ch.unisg.omi.config.YggdrasilConfig;
import ch.unisg.omi.controller.coap.RolesResource;
import ch.unisg.omi.controller.http.WorkspaceController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class OMIApplication {
	private static final Logger LOGGER = LoggerFactory.getLogger(OMIApplication.class);

	public static void main(String[] args) {
		LOGGER.info("Starting server...");
		final var context = SpringApplication.run(OMIApplication.class, args);

		final var env = context.getBean(Environment.class);
		final var entryPoint = env.getProperty("ENTRYPOINT", "http://127.0.0.1:8080/");
		final var base = env.getProperty("BASE", "http://127.0.0.1:7500/");

		LOGGER.info("Setting up CoAP server...");
		CoapServerConfig server = CoapServerConfig.getInstance();
		server.add(new RolesResource("roles"));
		server.start();

		LOGGER.info("Setting up Yggdrasil...");
		YggdrasilConfig yggdrasil = YggdrasilConfig.getInstance(env);

		yggdrasil.subscribe(entryPoint + "workspaces/",
			base + "workspaces");
	}
}
