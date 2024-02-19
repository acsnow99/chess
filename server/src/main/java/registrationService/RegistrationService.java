package registrationService;

import dataAccess.DataAccess;
import user.User;
import authData.AuthData;

public class RegistrationService {

    private DataAccess dataAccess = new DataAccess();

    public Object clearDatabase() {
        return this.dataAccess.clear();
    }

    public AuthData registerUser(User user) {
        // username already taken
        if (dataAccess.getUser(user) != null) {
            return null;
        }
        return dataAccess.registerUser(user);
    }

}
