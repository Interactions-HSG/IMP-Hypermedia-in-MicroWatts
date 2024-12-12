package ch.unisg.db.controllers;

import ch.unisg.db.db.Database;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GetTemperatureController {

    private final Database database = Database.getInstance();

    @GetMapping(path = "/temperatures")
    public ResponseEntity<String> getTemperature(@RequestParam("sensor") String sensor) {
        HttpHeaders headers = new HttpHeaders();

        return new ResponseEntity<>(database.get(sensor), headers, HttpStatus.ACCEPTED);
    }
}
