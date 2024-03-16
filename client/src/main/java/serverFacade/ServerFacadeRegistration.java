package serverFacade;

import exceptions.HttpResponseException;
import model.User;

public class ServerFacadeRegistration extends ServerFacade {
    public ServerFacadeRegistration(String URL) {
        super(URL);
    }

    public void clearDatabase() throws HttpResponseException {
        try {
            makeHttpRequest("DELETE", "/db", null, null);
        } catch (Exception exception) {
            throw new HttpResponseException(exception.getMessage());
        }
    }

    public void register(User user) throws HttpResponseException {
        try {
            makeHttpRequest("POST", "/user", user, null);
        } catch (Exception exception) {
            throw new HttpResponseException(exception.getMessage());
        }
    }
}
