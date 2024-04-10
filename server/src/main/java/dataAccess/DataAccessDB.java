package dataAccess;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
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
        var gameDefault = new ChessGame();
        gameDefault.setBoard(boardDefault);
        var newGame = new GameData(gameID, null, null, gameName, null, gameDefault);
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
                    gameToUpdate.blackUsername(), gameToUpdate.gameName(), gameToUpdate.watchers(), gameToUpdate.game());
        } else if (Objects.equals(joinGameRequest.playerColor(), "BLACK")) {
            if (!(gameToUpdate.blackUsername() == null || gameToUpdate.blackUsername().isEmpty())) {
                throw new AlreadyTakenException("Error: Already taken");
            }
            gameToInsert = new GameData(joinGameRequest.gameID(), gameToUpdate.whiteUsername(),
                    authData.username(), gameToUpdate.gameName(), gameToUpdate.watchers(), gameToUpdate.game());
        } else {
            var watchersUpdated = gameToUpdate.watchers();
            if (watchersUpdated == null) {
                watchersUpdated = new ArrayList<String>();
            }
            watchersUpdated.add(authData.username());
            gameToInsert = new GameData(joinGameRequest.gameID(), gameToUpdate.whiteUsername(),
                    gameToUpdate.blackUsername(), gameToUpdate.gameName(), watchersUpdated, gameToUpdate.game());
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

    public String makeMoveGame(AuthData authData, long gameID, ChessMove move) throws DataAccessException, NotFoundException, InvalidMoveException {
        var gameData = getGameByID(gameID);
        var game = gameData.game();
        var pieceAtStart = game.getBoard().getPiece(move.getStartPosition());
        if (pieceAtStart != null) {
            if ((pieceAtStart.getTeamColor() == ChessGame.TeamColor.WHITE && !Objects.equals(authData.username(), gameData.whiteUsername())) ||
                    (pieceAtStart.getTeamColor() == ChessGame.TeamColor.BLACK && !Objects.equals(authData.username(), gameData.blackUsername()))) {
                throw new InvalidMoveException("Piece is on opponent's team");
            }
        }

        game.makeMove(move);
        if (game.isInCheck(ChessGame.TeamColor.WHITE) || game.isInCheck(ChessGame.TeamColor.BLACK)) {
            if (checkGameFinished(game)) {
                saveGame(gameID, gameData);
                if (game.isInCheckmate(ChessGame.TeamColor.WHITE)) {
                    saveGame(gameID, gameData);
                    return gameData.whiteUsername() + " is in checkmate! GAME OVER";
                } else if (game.isInCheckmate(ChessGame.TeamColor.BLACK)) {
                    saveGame(gameID, gameData);
                    return gameData.blackUsername() + " is in checkmate! GAME OVER";
                } else if (game.isInStalemate(ChessGame.TeamColor.WHITE) || game.isInStalemate(ChessGame.TeamColor.BLACK)) {
                    saveGame(gameID, gameData);
                    return "Stalemate! GAME OVER";
                }
            }
            if (game.isInCheck(ChessGame.TeamColor.WHITE)) {
                saveGame(gameID, gameData);
                return gameData.whiteUsername() + " is in check! Look out!";
            } else if (game.isInCheck(ChessGame.TeamColor.BLACK)) {
                saveGame(gameID, gameData);
                return gameData.blackUsername() + " is in check! Look out!";
            }
        }
        saveGame(gameID, gameData);
        return null;
    }

    private boolean checkGameFinished(ChessGame game) {
        if (game.isInCheckmate(ChessGame.TeamColor.WHITE) || game.isInCheckmate(ChessGame.TeamColor.BLACK) ||
                game.isInStalemate(ChessGame.TeamColor.WHITE) || game.isInStalemate(ChessGame.TeamColor.BLACK)) {
            game.setFinished(true);
            return true;
        }
        return false;
    }

    public void removePlayer(AuthData authData, long gameID) throws NotFoundException, DataAccessException {
        var gameToUpdate = getGameByID(gameID);
        GameData gameToInsert;
        var authDataNew = getAuthDataFromToken(authData);
        if (Objects.equals(authDataNew.username(), gameToUpdate.whiteUsername())) {
            gameToInsert = new GameData(gameID, "",
                    gameToUpdate.blackUsername(), gameToUpdate.gameName(), gameToUpdate.watchers(), gameToUpdate.game());
        } else if (Objects.equals(authDataNew.username(), gameToUpdate.blackUsername())) {
            gameToInsert = new GameData(gameID, gameToUpdate.whiteUsername(),
                    "", gameToUpdate.gameName(), gameToUpdate.watchers(), gameToUpdate.game());
        } else {
            var watchers = gameToUpdate.watchers();
            for (var i = 0; i < watchers.size(); i++) {
                if (Objects.equals(watchers.get(i), authDataNew.username())) {
                    watchers.remove(i);
                    i = watchers.size();
                }
            }
            gameToInsert = new GameData(gameID, gameToUpdate.whiteUsername(),
                    "", gameToUpdate.gameName(), gameToUpdate.watchers(), gameToUpdate.game());
        }
        try (var connection = DatabaseManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement("UPDATE game SET gamedatajson = ? WHERE gameid = ?")) {
                preparedStatement.setString(1, new Gson().toJson(gameToInsert));
                preparedStatement.setLong(2, gameID);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Error: Could not leave game");
        }
    }

    private void saveGame(long gameID, GameData game) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement("UPDATE game SET gamedatajson = ? WHERE gameid = ?")) {
                preparedStatement.setString(1, new Gson().toJson(game));
                preparedStatement.setLong(2, gameID);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Error: Could not update game");
        }
    }

    public void endGame(long gameID) throws DataAccessException, NotFoundException {
        var game = this.getGameByID(gameID);
        game.game().setFinished(true);
        this.saveGame(gameID, game);
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
