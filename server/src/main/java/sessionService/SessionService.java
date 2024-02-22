package sessionService;

import authData.AuthData;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import otherExceptions.MissingDataException;
import otherExceptions.UnauthorizedException;
import user.User;

public class SessionService {

    private DataAccess dataAccess = new DataAccess();

    public AuthData loginUser(User user) throws DataAccessException, MissingDataException {
        if (user.username() == null) {
            throw new MissingDataException("Error: Missing username");
        } else if (user.password() == null) {
            throw new MissingDataException("Error: Missing password");
        }
        if (!dataAccess.userIsAuthorized(user)) {
            return null;
        }
        return dataAccess.loginUser(user);
    }

    public void logoutUser(AuthData authData) throws UnauthorizedException, DataAccessException {
        var authDataFromDB = dataAccess.getAuthDataFromToken(authData);
        if (authDataFromDB != null) {
            dataAccess.logoutUser(authDataFromDB);
        } else {
            throw new UnauthorizedException("Error: Unauthorized");
        }
    }

}
