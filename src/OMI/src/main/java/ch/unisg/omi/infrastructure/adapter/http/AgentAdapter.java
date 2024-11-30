package ch.unisg.omi.infrastructure.adapter.http;

import ch.unisg.omi.core.port.out.AgentPort;
import com.google.gson.Gson;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

@Primary
@Component
public class AgentAdapter implements AgentPort {

    @Override
    public void sendGroupName(String groupName) {

        HashMap<String, String> body = new HashMap<>();

        body.put("performative", "tell");
        body.put("sender",  "omi");
        body.put("receiver", "bob");
        body.put("content", "group('automation')");
        body.put("msgId", "50");

        Gson gson = new Gson();
        String jsonBody = gson.toJson(body);

        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .uri(URI.create("http://aa:8081/agents/bob/inbox"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendRoles() {

        HttpRequest request = HttpRequest.newBuilder()
                .build();

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
