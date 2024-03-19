package serverFacade;

import exceptions.HttpResponseException;
import model.AuthData;
import model.User;

public class ServerFacadeRegistration extends ServerFacade {
    public ServerFacadeRegistration(String URL) {
        super(URL);
    }

    public void clearDatabase() throws HttpResponseException {
        try {
            makeHttpRequest("DELETE", "/db", null, null, null);
        } catch (Exception exception) {
            throw new HttpResponseException(exception.getMessage());
        }
    }

    public AuthData register(User user) throws HttpResponseException {
        try {
            return makeHttpRequest("POST", "/user", user, AuthData.class, null);
        } catch (Exception exception) {
            throw new HttpResponseException(exception.getMessage());
        }
    }
}
