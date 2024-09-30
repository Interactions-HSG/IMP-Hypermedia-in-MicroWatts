
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import cartago.Artifact;
import cartago.OPERATION;
import cartago.OpFeedbackParam;



public class HttpClientArtifact extends Artifact{

    private static String ENTRYPOINT = "http://yggdrasil:8080/";

    void init(){}

    @OPERATION
    public void readEndpoint(OpFeedbackParam<String> result) {
        try {
            URI uri = new URI(ENTRYPOINT);

            HttpRequest request = HttpRequest.newBuilder()
            .uri(uri)
            .GET()
            .build();



            HttpClient client = HttpClient.newHttpClient();
            try {
                System.out.println(request.toString());
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 200) {
                    System.out.println("Error: " + response.statusCode());
                }
                String responseBody = response.body();
                result.set(responseBody);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    @OPERATION
    public void readTD(String response, OpFeedbackParam<String> result) {
        System.out.println("TD: " + response);
    }

}
