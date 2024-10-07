package ch.unisg.frontend.controller;

import ch.unisg.ics.interactions.wot.td.ThingDescription;
import ch.unisg.ics.interactions.wot.td.affordances.ActionAffordance;
import ch.unisg.ics.interactions.wot.td.io.TDGraphReader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;

import static org.eclipse.rdf4j.model.util.Values.iri;

@Controller
public class ActionController {

    @GetMapping(path="/artifacts/actions")
    public String actions(
            @RequestParam(value = "artifactURI", required = false) String artifactURI,
            Model model
    ) {
        HashMap<String, String> actions = new HashMap<>();

        try {

            ThingDescription td = TDGraphReader.readFromURL(ThingDescription.TDFormat.RDF_TURTLE, artifactURI);
            final var graphModel = td.getGraph().get();

            List<ActionAffordance> actionsAffordances = td.getActions();

            if (actionsAffordances.isEmpty()) {
                System.out.println("No actions found");
            } else {

                for (ActionAffordance action : actionsAffordances) {
                    actions.put(action.getName().toString(), "");
                }
            }

            final var filtered = graphModel.filter(null, iri(""), null);

            System.out.println(filtered);
        } catch (Exception e) {

        }

        model.addAttribute("location", "yggdrasil/workspaces/artifacts/actions");
        model.addAttribute("actions", actions);

        return "actions";
    }
}
