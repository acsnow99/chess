package serverFacade;

import com.google.gson.Gson;
import exceptions.HttpResponseException;
import model.User;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ServerFacade {
    private final String serverURL;

    public ServerFacade(String URL) {
        serverURL = URL;
    }

    public <T> T makeHttpRequest(String method, String path, Object requestObject, Class<T> responseClass) throws HttpResponseException {
        try {
            URL url = (new URI(serverURL + path)).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);

            connection.setDoOutput(true);
            writeToBody(requestObject, connection);
            connection.connect();
            if (connection.getResponseCode() >= 300) {
                throw new Exception("Error: Could not register user");
            }
            return readBody(connection, responseClass);
        } catch (Exception exception) {
            throw new HttpResponseException(exception.getMessage());
        }
    }

    private void writeToBody(Object requestObject, HttpURLConnection connection) throws IOException {
        connection.addRequestProperty("Content-Type", "application/json");
        String reqData = new Gson().toJson(requestObject);
        try (OutputStream requestBody = connection.getOutputStream()) {
            requestBody.write(reqData.getBytes());
        }
    }

    private <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }
}
