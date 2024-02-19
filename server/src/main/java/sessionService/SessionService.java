package sessionService;

import authData.AuthData;
import dataAccess.DataAccess;
import user.User;

public class SessionService {

    private DataAccess dataAccess = new DataAccess();

    public AuthData login(User user) {
        var authData = new AuthData(null, null);
        
        return authData;
    }

}
