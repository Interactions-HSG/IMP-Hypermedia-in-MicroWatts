package ch.unisg.frontend.controller;

import ch.unisg.frontend.service.YggdrasilService;
import ch.unisg.ics.interactions.wot.td.ThingDescription;
import ch.unisg.ics.interactions.wot.td.io.TDGraphReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.eclipse.rdf4j.model.util.Values.iri;

@Controller
public class ArtifactController {

    @GetMapping(path = "/artifacts")
    public String artifacts(
            @RequestParam(value = "workspaceURI", required = false) String workspaceURI,
            Model model
    ) {
        HashMap<String, String> artifacts = new HashMap<>();
        String backURI;

        try {

            ThingDescription td = TDGraphReader.readFromURL(ThingDescription.TDFormat.RDF_TURTLE, workspaceURI);
            final var graphModel = td.getGraph().get();

            final var filtered = graphModel.filter(null, iri("https://purl.org/hmas/contains"), null);

            if (filtered.isEmpty()) {
                System.out.println("No artifacts found");
            } else {
                filtered.objects().forEach(artifact -> {
                    System.out.println("Found artifact: " + artifact.stringValue());

                    Pattern pattern = Pattern.compile("artifacts/(.*?)/");
                    Matcher matcher = pattern.matcher(artifact.stringValue());

                    if (matcher.find()) {
                        String name = matcher.group(1);
                        artifacts.put(name, artifact.stringValue());

                    }

                });
            }

            backURI = graphModel.filter(null, iri("https://purl.org/hmas/isHostedOn"), null).objects().stream().findFirst().get().stringValue();
            System.out.println(backURI);

        } catch (Exception e) {
            e.printStackTrace();
        }

        model.addAttribute("location", "yggdrasil/workspaces/artifacts");
        model.addAttribute("artifacts", artifacts);

        return "artifacts";
    }
}


