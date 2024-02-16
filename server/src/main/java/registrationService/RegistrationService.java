package registrationService;

import user.User;
import authData.AuthData;

public class RegistrationService {

    public AuthData registerUser(User user) {
        var authToken = "abcdefg";
        return new AuthData(user.username(), authToken);
    }

}
