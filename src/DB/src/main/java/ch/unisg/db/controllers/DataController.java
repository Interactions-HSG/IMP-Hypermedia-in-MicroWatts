package ch.unisg.db.controllers;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DataController {


  @GetMapping(path = "/data")
  public String getData() {
    return "data";
  }
}
