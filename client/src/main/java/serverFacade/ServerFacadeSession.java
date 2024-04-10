package serverFacade;

import exceptions.HttpResponseException;
import model.AuthData;
import model.User;

public class ServerFacadeSession extends ServerFacade {
    public ServerFacadeSession(String url) {
        super(url);
    }

    public AuthData login(User user) throws HttpResponseException {
        try {
            return makeHttpRequest("POST", "/session", user, AuthData.class, null);
        } catch (Exception exception) {
            throw new HttpResponseException(exception.getMessage());
        }
    }

    public void logout(AuthData authData) throws HttpResponseException {
        try {
            makeHttpRequest("DELETE", "/session", null, null, authData);
        } catch (Exception exception) {
            throw new HttpResponseException(exception.getMessage());
        }
    }
}
