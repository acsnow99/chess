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
    public AuthData registerUser(User user) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement("INSERT INTO user (username, password, email) " +
                    "VALUES (\"" + user.username() + "\", \"" + user.password() + "\",\"" + user.email() + "\")")) {
                preparedStatement.executeUpdate();
                return loginUser(user);
            }
        } catch (SQLException exception) {
            return null;
        }
    }

    @Override
    public AuthData loginUser(User user) throws DataAccessException {
        String authToken = new AuthTokenGenerator().generateToken();
        try (var connection = DatabaseManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement("INSERT INTO auth (username, authToken) " +
                    "VALUES (\"" + user.username() + "\", \"" + authToken + "\")")) {
                preparedStatement.executeUpdate();
                return new AuthData(user.username(), authToken);
            }
        } catch (SQLException exception) {
            return null;
        }
    }

    @Override
    public boolean userIsAuthorized(User user) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement("SELECT COUNT(*) from user where username=\""
                    + user.username() + "\" and password=\"" + user.password() + "\";")) {
                var rs = preparedStatement.executeQuery();
                rs.next();
                var usersCount = rs.getInt(1);
                return usersCount >= 1;
            }
        } catch (SQLException exception) {
            return false;
        }
    }

    @Override
    public void logoutUser(AuthData authData) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement("DELETE FROM auth WHERE authtoken=\"" + authData.authToken() + "\"")) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Error: Could not logout user");
        }
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
    public Object clear() throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement("TRUNCATE auth")) {
                preparedStatement.executeUpdate();
            }
            try (var preparedStatement = connection.prepareStatement("TRUNCATE user")) {
                preparedStatement.executeUpdate();
            }
            try (var preparedStatement = connection.prepareStatement("TRUNCATE game")) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Error: Could not clear database");
        }
        return null;
    }
}
