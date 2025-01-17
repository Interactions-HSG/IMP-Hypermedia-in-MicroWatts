package ch.unisg.db.controllers;

import ch.unisg.db.db.Database;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HumidityController {

    private final Database database = Database.getInstance();

    @GetMapping(path = "/humidity")
    public ResponseEntity<String> getHumidity() {

        HttpHeaders headers = new HttpHeaders();

        return new ResponseEntity<>(database.getHumidity("room1"), headers, HttpStatus.ACCEPTED);
    }

    @PostMapping(path = "/humidity")
    public ResponseEntity<String> postHumidity(@RequestBody String payload) {

        String key = payload.split(",")[0];
        database.put(key, payload);

        HttpHeaders headers = new HttpHeaders();

        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }
}

