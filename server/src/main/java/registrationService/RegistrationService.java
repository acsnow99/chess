package registrationService;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import user.User;
import authData.AuthData;

public class RegistrationService {

    private DataAccess dataAccess = new DataAccess();

    public Object clearDatabase() throws DataAccessException {
        return this.dataAccess.clear();
    }

    public AuthData registerUser(User user) throws DataAccessException {
        // username already taken
        if (dataAccess.getUser(user) != null) {
            return null;
        }
        return dataAccess.registerUser(user);
    }

}
