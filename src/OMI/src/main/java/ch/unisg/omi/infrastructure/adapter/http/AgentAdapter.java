package ch.unisg.omi.infrastructure.adapter.http;

import ch.unisg.omi.core.port.out.AgentPort;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Primary
@Component
public class AgentAdapter implements AgentPort {

    @Override
    public void sendGroupName(String groupName) {

        // TODO: Implement request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("agent"))
                .POST(HttpRequest.BodyPublishers.ofString(groupName))
                .build();

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
