package ch.unisg.db.controllers;

import ch.unisg.db.db.Database;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class TemperatureController {

    private final Database database = Database.getInstance();

    @GetMapping(path = "/temperature")
    public ResponseEntity<String> getTemperature() {
        System.out.println("GET /temperature");
        HttpHeaders headers = new HttpHeaders();

        return new ResponseEntity<>(database.getTemperature("room1"), headers, HttpStatus.ACCEPTED);
    }

    @PostMapping(path = "/temperature")
    public ResponseEntity<String> postTemperature() {
        System.out.println("GET /temperature");
        HttpHeaders headers = new HttpHeaders();

        return new ResponseEntity<>(database.getTemperature("room1"), headers, HttpStatus.ACCEPTED);
    }
}
