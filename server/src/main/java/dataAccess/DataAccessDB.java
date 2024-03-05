package dataAccess;

import exceptions.AlreadyTakenException;
import exceptions.DataAccessException;
import exceptions.NotFoundException;
import generators.AuthTokenGenerator;
import model.AuthData;
import model.GameData;
import model.User;
import requests.JoinGameRequest;

import java.sql.SQLException;
import java.util.ArrayList;

public class DataAccessDB implements DataAccess {
    @Override
    public User getUser(User user) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement("SELECT * from user where username=\"" + user.username() + "\"")) {
                var rs = preparedStatement.executeQuery();
                rs.next();
                return new User(rs.getString("username"), rs.getString("password"), rs.getString("email"));
            }
        } catch (SQLException exception) {
            return null;
        }
    }

    @Override
    public AuthData registerUser(User user) {
        return null;
    }

    @Override
    public AuthData loginUser(User user) throws DataAccessException {
        String authToken = new AuthTokenGenerator().generateToken();
        try (var connection = DatabaseManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement("INSERT INTO auth (username, authToken) " +
                    "VALUES (\"" + user.username() + "\", \"" + authToken + "\")")) {
                var rs = preparedStatement.executeUpdate();
            }
        } catch (SQLException exception) {
            return null;
        }
        return new AuthData(user.username(), authToken);
    }

    @Override
    public boolean userIsAuthorized(User user) {
        return false;
    }

    @Override
    public void logoutUser(AuthData authData) throws DataAccessException {

    }

    @Override
    public boolean userDBContainsUsername(String username) {
        return false;
    }

    @Override
    public AuthData getAuthDataFromToken(AuthData authData) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement("select * from auth where authToken=\""
                    + authData.authToken() + "\"")) {
                var rs = preparedStatement.executeQuery();
                rs.next();
                return new AuthData(rs.getString("username"), rs.getString("authtoken"));
            }
        } catch (SQLException exception) {
            return null;
        }
    }

    @Override
    public boolean authDataIsAuthorized(AuthData authData) {
        return false;
    }

    @Override
    public ArrayList<GameData> getGames() {
        return null;
    }

    @Override
    public void createGame(String gameName, long gameID) throws DataAccessException {

    }

    @Override
    public void joinGame(AuthData authData, JoinGameRequest joinGameRequest) throws DataAccessException, NotFoundException, AlreadyTakenException {

    }

    @Override
    public Object clear() {
        return null;
    }
}