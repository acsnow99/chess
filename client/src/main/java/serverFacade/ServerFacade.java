package serverFacade;

import com.google.gson.Gson;
import exceptions.HttpResponseException;
import model.AuthData;
import model.User;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Objects;

public class ServerFacade {
    private final String serverURL;

    public ServerFacade(String URL) {
        serverURL = URL;
    }

    public <T> T makeHttpRequest(String method, String path, Object requestObject, Class<T> responseClass, AuthData authorization) throws HttpResponseException {
        try {
            URL url = (new URI(serverURL + path)).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);

            // some requests will not require authorization and will provide null for that argument
            if (authorization != null) {
                connection.setRequestProperty("Authorization", authorization.authToken());
            }
            connection.setDoOutput(true);
            if (!Objects.equals(method, "GET")) {
                writeToBody(requestObject, connection);
            }
            connection.connect();
            if (connection.getResponseCode() >= 300) {
                throw new Exception("Error: Server responded with error code " + connection.getResponseCode());
            }
            var responseBody = readBody(connection, responseClass);
            return responseBody;
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
