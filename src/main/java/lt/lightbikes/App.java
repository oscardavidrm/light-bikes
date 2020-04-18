/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package lt.lightbikes;

import io.github.cdimascio.dotenv.Dotenv;
import io.socket.client.IO;
import io.socket.client.Socket;
import lt.lightbikes.network.HTTPClient;
import okhttp3.Response;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class App {
  public static void main(String[] args) throws URISyntaxException {
    Dotenv dotenv = Dotenv.load();
    HTTPClient httpClient = new HTTPClient(dotenv.get("SERVER_URI"));
    Socket wsClient = IO.socket(dotenv.get("SERVER_URI"));

    wsClient
            .on(Socket.EVENT_CONNECT, data -> System.out.println("Connected to server!"))
            .on(
                    "serverUpdate",
                    data -> {
                      System.out.println(Arrays.toString(data));
                      wsClient.emit("clientUpdate", "Update from client!");
                    })
            .on(Socket.EVENT_DISCONNECT, data -> System.out.println("Disconnected from server"));

    wsClient.connect();

    try {
      Response response = httpClient.query("/test");

      String payload = Objects.requireNonNull(response.body()).string();
      System.out.println(payload);
      //            JSONObject jsonPayload = new JSONObject(payload);
      //
      //            System.out.println(jsonPayload.get("data"));
    } catch (ExecutionException | InterruptedException | IOException e) {
      e.printStackTrace();
    }
  }
}
