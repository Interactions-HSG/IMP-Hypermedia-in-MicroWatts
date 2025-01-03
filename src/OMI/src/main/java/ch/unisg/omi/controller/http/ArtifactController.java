package ch.unisg.omi.controller.http;

import ch.unisg.omi.core.port.in.AgentUseCase;
import java.io.StringReader;
import lombok.RequiredArgsConstructor;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ArtifactController {

    private final AgentUseCase agentUseCase;

    @PostMapping(path = "/workspaces/artifacts")
    public ResponseEntity<String> newArtifact(
            @RequestHeader("Location") String agentId,
            @RequestBody String body
    ) {
        try {
            // Parse RDF body
            Model rdfModel = Rio.parse(new StringReader(body), "http://yggdrasil:8080/", RDFFormat.TURTLE);

            // Check if the agentId is part of the RDF graph
            boolean containsAgent = Models.subjectIRIs(rdfModel)
                .stream()
                .anyMatch(iri -> iri.stringValue().contains(agentId));

            if (containsAgent) {
                System.out.println("New artifact " + agentId + " is created.");
                agentUseCase.addAgent(agentId);
            } else {
                System.out.println("Artifact " + agentId + " is deleted.");
                agentUseCase.removeAgent(agentId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Invalid RDF body.");
        }

        return ResponseEntity.ok().body("OK");
    }

}
