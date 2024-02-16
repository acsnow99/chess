package registrationService;

import dataAccess.UserDataAccess;
import user.User;
import authData.AuthData;

public class RegistrationService {

    private UserDataAccess userDataAccess = new UserDataAccess();

    public AuthData registerUser(User user) {
        // username already taken
        if (userDataAccess.getUser(user) != null) {
            return null;
        }
        return userDataAccess.registerUser(user);
    }

}
