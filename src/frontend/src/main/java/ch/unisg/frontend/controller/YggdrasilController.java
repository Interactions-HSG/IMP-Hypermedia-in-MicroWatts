package ch.unisg.frontend.controller;


import ch.unisg.frontend.service.YggdrasilService;
import ch.unisg.ics.interactions.wot.td.ThingDescription;
import ch.unisg.ics.interactions.wot.td.io.TDGraphReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import static org.eclipse.rdf4j.model.util.Values.iri;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class YggdrasilController {

    private final YggdrasilService yggdrasilService;

    private final String yggdrasilBaseURI = "http://yggdrasil:8080/";

    @Autowired
    public YggdrasilController(YggdrasilService yggdrasilService) {
        this.yggdrasilService = yggdrasilService;
    }

    @GetMapping(path = "/")
    public String yggdrasil(Model model) {

        HashMap<String, String> workspaces = new HashMap<>();

        try {
            ThingDescription td = TDGraphReader.readFromURL(ThingDescription.TDFormat.RDF_TURTLE, yggdrasilBaseURI);
            final var graphModel = td.getGraph().get();

            final var artifacts = graphModel.filter(null, iri("https://purl.org/hmas/hosts"), null);

            if (artifacts.isEmpty()) {
                System.out.println("No workspace exists");
            } else {

                artifacts.objects().forEach(artifact -> {
                    System.out.println("Found workspace: " + artifact.stringValue());

                    Pattern pattern = Pattern.compile("workspaces/(.*?)/");
                    Matcher matcher = pattern.matcher(artifact.stringValue());

                    if (matcher.find()) {
                        String name = matcher.group(1);
                        workspaces.put(name, artifact.stringValue());

                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        model.addAttribute("location", "yggdrasil/workspaces");
        model.addAttribute("workspaces", workspaces);

        return "index";
    }
}
