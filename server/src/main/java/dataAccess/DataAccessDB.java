package dataAccess;

import chess.ChessBoard;
import chess.ChessMove;
import com.google.gson.Gson;
import exceptions.AlreadyTakenException;
import exceptions.DataAccessException;
import exceptions.NotFoundException;
import exceptions.UnauthorizedException;
import generators.AuthTokenGenerator;
import model.AuthData;
import model.GameData;
import model.User;
import requests.JoinGameRequest;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

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

    public void databaseInit() throws DataAccessException {
        DatabaseManager.createDatabase();
        DatabaseManager.createTables();
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
    public boolean userDBContainsUsername(User user) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement("SELECT COUNT(*) from user where username=\""
                    + user.username() + "\";")) {
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
    public boolean authDataIsAuthorized(AuthData authData) throws DataAccessException {
        return getAuthDataFromToken(authData) != null;
    }

    @Override
    public ArrayList<GameData> getGames() throws DataAccessException {
        var gamesList = new ArrayList<GameData>();
        String gameDataJson;
        try (var connection = DatabaseManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement("SELECT * FROM game")) {
                var rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    gameDataJson = rs.getString("gamedatajson");
                    gamesList.add(new Gson().fromJson(gameDataJson, GameData.class));
                }
                return gamesList;
            }
        } catch (SQLException exception) {
            return null;
        }
    }

    private GameData getGameByID(long gameID) throws DataAccessException, NotFoundException {
        var gamesList = getGames();
        for (var game : gamesList) {
            if (game.gameID() == gameID) {
                return game;
            }
        }
        throw new NotFoundException("Error: Could not find game");
    }

    @Override
    public void createGame(String gameName, long gameID) throws DataAccessException {
        var boardDefault = new ChessBoard();
        boardDefault.resetBoard();
        var newGame = new GameData(gameID, null, null, gameName, null, boardDefault);
        var newGameJson = new Gson().toJson(newGame);
        try (var connection = DatabaseManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement("INSERT INTO game (gameid, gamedatajson) VALUES ( ?, ? );")) {
                preparedStatement.setLong(1, gameID);
                preparedStatement.setString(2, newGameJson);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Error: Could not create game");
        }
    }

    @Override
    public void joinGame(AuthData authData, JoinGameRequest joinGameRequest) throws DataAccessException, NotFoundException, AlreadyTakenException {
        var gameToUpdate = getGameByID(joinGameRequest.gameID());
        GameData gameToInsert;
        if (Objects.equals(joinGameRequest.playerColor(), "WHITE")) {
            if (!(gameToUpdate.whiteUsername() == null || gameToUpdate.whiteUsername().isEmpty())) {
                throw new AlreadyTakenException("Error: Already taken");
            }
            gameToInsert = new GameData(joinGameRequest.gameID(), authData.username(),
                    gameToUpdate.blackUsername(), gameToUpdate.gameName(), gameToUpdate.watchers(), gameToUpdate.board());
        } else if (Objects.equals(joinGameRequest.playerColor(), "BLACK")) {
            if (!(gameToUpdate.blackUsername() == null || gameToUpdate.blackUsername().isEmpty())) {
                throw new AlreadyTakenException("Error: Already taken");
            }
            gameToInsert = new GameData(joinGameRequest.gameID(), gameToUpdate.whiteUsername(),
                    authData.username(), gameToUpdate.gameName(), gameToUpdate.watchers(), gameToUpdate.board());
        } else {
            var watchersUpdated = gameToUpdate.watchers();
            if (watchersUpdated == null) {
                watchersUpdated = new ArrayList<String>();
            }
            watchersUpdated.add(authData.username());
            gameToInsert = new GameData(joinGameRequest.gameID(), gameToUpdate.whiteUsername(),
                    gameToUpdate.blackUsername(), gameToUpdate.gameName(), watchersUpdated, gameToUpdate.board());
        }
        try (var connection = DatabaseManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement("UPDATE game SET gamedatajson = ? WHERE gameid = ?")) {
                preparedStatement.setString(1, new Gson().toJson(gameToInsert));
                preparedStatement.setLong(2, joinGameRequest.gameID());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Error: Could not join game");
        }
    }

    public void makeMoveGame(AuthData authData, long gameID, ChessMove move) throws DataAccessException, NotFoundException {
        var game = getGameByID(gameID);
        if (game == null) {
            throw new NotFoundException("Error: Game not found");
        } else {
            var board = game.board();
            //TODO: Run the move through a chess game object first
            board.movePiece(move);
        }
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
