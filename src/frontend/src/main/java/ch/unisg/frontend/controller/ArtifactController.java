package ch.unisg.frontend.controller;

import ch.unisg.frontend.service.YggdrasilService;
import ch.unisg.ics.interactions.wot.td.ThingDescription;
import ch.unisg.ics.interactions.wot.td.io.TDGraphReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.eclipse.rdf4j.model.util.Values.iri;

@Controller
public class ArtifactController {
    private final String yggdrasilBaseURI = "http://yggdrasil:8080/";

    @GetMapping(path = "/artifacts")
    public String artifacts(Model model) {
        return "artifacts";
    }
}