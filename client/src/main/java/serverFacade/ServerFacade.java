package serverFacade;

import com.google.gson.Gson;
import model.User;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ServerFacade {
    private final String serverURL;

    public ServerFacade(String URL) {
        serverURL = URL;
    }

    public void register(User user) {
        try {
            URL url = (new URI(serverURL)).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            connection.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(user);
            try (OutputStream requestBody = connection.getOutputStream()) {
                requestBody.write(reqData.getBytes());
            }
        } catch (Exception exception) {
            System.out.println("An issue occurred");
        }
    }
}
