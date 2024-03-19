package serverFacade;

import exceptions.HttpResponseException;
import model.AuthData;
import model.User;

public class ServerFacadeSession extends ServerFacade {
    public ServerFacadeSession(String URL) {
        super(URL);
    }

    public AuthData login(User user) throws HttpResponseException {
        try {
            return makeHttpRequest("POST", "/session", user, AuthData.class);
        } catch (Exception exception) {
            throw new HttpResponseException(exception.getMessage());
        }
    }
}
