package services;

import model.AuthData;
import dataAccess.DataAccess;
import exceptions.DataAccessException;
import exceptions.MissingDataException;
import exceptions.UnauthorizedException;
import model.User;

public class SessionService {

    public AuthData loginUser(DataAccess dataAccess, User user) throws DataAccessException, MissingDataException, UnauthorizedException {
        if (user.username() == null) {
            throw new MissingDataException("Error: Missing username");
        } else if (user.password() == null) {
            throw new MissingDataException("Error: Missing password");
        }
        if (!dataAccess.userIsAuthorized(user)) {
            throw new UnauthorizedException("Error: Unauthorized");
        }
        return dataAccess.loginUser(user);
    }

    public void logoutUser(DataAccess dataAccess, AuthData authData) throws UnauthorizedException, DataAccessException {
        var authDataFromDB = dataAccess.getAuthDataFromToken(authData);
        if (authDataFromDB != null) {
            dataAccess.logoutUser(authDataFromDB);
        } else {
            throw new UnauthorizedException("Error: Unauthorized");
        }
    }

}
