package ch.unisg.db;

import ch.unisg.db.config.DBServer;
import ch.unisg.db.controllers.TelemetryController;
import org.eclipse.californium.core.config.CoapConfig;
import org.eclipse.californium.elements.config.Configuration;
import org.eclipse.californium.elements.config.TcpConfig;
import org.eclipse.californium.elements.config.UdpConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class DbApplication {

    static {
        CoapConfig.register();
        UdpConfig.register();
        TcpConfig.register();
    }

    public static void main(String[] args) throws IOException {

        boolean udp = true;
        boolean tcp = false;

        int port = Configuration.getStandard().get(CoapConfig.COAP_PORT);
        System.out.println("Using port " + port);

        SpringApplication.run(DbApplication.class, args);
        Launcher.main();

        DBServer server = new DBServer(udp, tcp, port);

        server.add(new TelemetryController("telemetry"));

        server.start();
    }
}
