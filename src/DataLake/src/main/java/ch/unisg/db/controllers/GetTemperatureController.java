package ch.unisg.db.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GetTemperatureController {

    @GetMapping(path = "/temperatures")
    public ResponseEntity<String> getTemperature() {
        HttpHeaders headers = new HttpHeaders();

        return new ResponseEntity<>("temperature: 30", headers, HttpStatus.ACCEPTED);
    }
}
