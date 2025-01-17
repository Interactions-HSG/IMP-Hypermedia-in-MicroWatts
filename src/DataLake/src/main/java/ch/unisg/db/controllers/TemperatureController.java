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

        HttpHeaders headers = new HttpHeaders();

        return new ResponseEntity<>(database.get("temperature"), headers, HttpStatus.ACCEPTED);
    }

    @PostMapping(path = "/temperature")
    public ResponseEntity<String> postTemperature(@RequestBody String payload) {

        String key = payload.split(",")[0];
        database.put(key, payload);

        HttpHeaders headers = new HttpHeaders();

        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }
}
