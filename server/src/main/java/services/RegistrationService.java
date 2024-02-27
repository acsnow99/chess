package services;

import dataAccess.DataAccess;
import exceptions.DataAccessException;
import model.User;
import model.AuthData;
import exceptions.MissingDataException;

public class RegistrationService {

    public Object clearDatabase(DataAccess dataAccess) throws DataAccessException {
        return dataAccess.clear();
    }

    public AuthData registerUser(DataAccess dataAccess, User user) throws DataAccessException, MissingDataException {
        if (user.username() == null || user.username().equals("")
                || user.password() == null || user.password().equals("")
                || user.email() == null || user.email().equals("")) {
            throw new MissingDataException("Error: bad request");
        }
        // username already taken
        if (dataAccess.getUser(user) != null) {
            return null;
        }
        return dataAccess.registerUser(user);
    }

}
