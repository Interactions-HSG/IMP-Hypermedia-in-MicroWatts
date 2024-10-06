package ch.unisg.db;

import ch.unisg.db.config.DBServer;
import ch.unisg.db.controllers.TelemetryController;
import org.eclipse.californium.core.config.CoapConfig;
import org.eclipse.californium.elements.config.TcpConfig;
import org.eclipse.californium.elements.config.UdpConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class DataLakeApplication {

    static {
        CoapConfig.register();
        UdpConfig.register();
        TcpConfig.register();
    }

    public static void main(String[] args) throws IOException {

        System.out.println("Starting server...");

        SpringApplication.run(DataLakeApplication.class, args);

        /** set up yggdrasil */
        System.out.println("Setting up yggdrasil...");
        Launcher.main();

        /** Set up coap */
        System.out.println("Setting up Coap...");
        boolean udp = true;
        boolean tcp = false;

        int port = 5685;
        DBServer server = new DBServer(udp, tcp, port);

        server.add(new TelemetryController("data"));

        server.start();
        System.out.println("Server started");
    }
}
