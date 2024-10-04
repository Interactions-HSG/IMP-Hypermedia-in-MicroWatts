package ch.unisg.db;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class DbApplication {

    public static void main(String[] args) throws IOException {

        SpringApplication.run(DbApplication.class, args);
        Launcher.main();
    }
}
