package dataAccess;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import exceptions.AlreadyTakenException;
import exceptions.NotFoundException;
import model.AuthData;
import generators.AuthTokenGenerator;
import exceptions.DataAccessException;
import requests.JoinGameRequest;
import model.User;
import model.GameData;

import java.util.ArrayList;
import java.util.Objects;

public class DataAccessMemory implements DataAccess {
    private ArrayList<AuthData> authDB = new ArrayList<>();
    private ArrayList<GameData> gameDB = new ArrayList<>();
    private ArrayList<User> userDB = new ArrayList<>();


    public void databaseInit() {

    }

    public User getUser(User user) {
        if (userDBContainsUsername(user)) {
            return user;
        } else {
            return null;
        }
    }

    public AuthData registerUser(User user) {
        userDB.add(user);
        return loginUser(user);
    }

    public AuthData loginUser(User user) {
        String authToken = new AuthTokenGenerator().generateToken();
        AuthData authDataResult = new AuthData(user.username(), authToken);
        // TO-DO: check for existing token based on authToken
        authDB.add(authDataResult);
        return authDataResult;
    }

    public boolean userIsAuthorized(User user) {
        for (var userEntry : userDB) {
            if (Objects.equals(user.username(), userEntry.username()) && Objects.equals(user.password(), userEntry.password())) {
                return true;
            }
        }
        return false;
    }


    public void logoutUser(AuthData authData) throws DataAccessException {
        for (var auth : authDB) {
            if (Objects.equals(auth.authToken(), authData.authToken())) {
                authDB.remove(auth);
                break;
            }
        }
    }

    public boolean userDBContainsUsername(User user) {
        for (var u : userDB) {
            if (Objects.equals(u.username(), user.username())) {
                return true;
            }
        }
        return false;
    }

    // NOTE: authData's username may be null. This function only compares authTokens only
    public AuthData getAuthDataFromToken(AuthData authData) {
        for (var auth : authDB) {
            if (Objects.equals(auth.authToken(), authData.authToken())) {
                return auth;
            }
        }
        return null;
    }

    public boolean authDataIsAuthorized(AuthData authData) {
        return getAuthDataFromToken(authData) != null;
    }

    public ArrayList<GameData> getGames() {
        return gameDB;
    }

    public void createGame(String gameName, long gameID) throws DataAccessException {
        var boardDefault = new ChessBoard();
        boardDefault.resetBoard();
        var gameDefault = new ChessGame();
        gameDefault.setBoard(boardDefault);
        var newGame = new GameData(gameID, null, null, gameName, new ArrayList<String>(), gameDefault);
        gameDB.add(newGame);
    }

    public void joinGame(AuthData authData, JoinGameRequest joinGameRequest) throws DataAccessException, NotFoundException, AlreadyTakenException {
        var foundGame = false;
        for (var game : gameDB) {
            if (game.gameID() == joinGameRequest.gameID()) {
                foundGame = true;
                GameData newGame;
                if (Objects.equals(joinGameRequest.playerColor(), "WHITE")) {
                    if (game.whiteUsername() != null && !Objects.equals(game.whiteUsername(), authData.username())) {
                        throw new AlreadyTakenException("Error: Already taken");
                    }
                    newGame = new GameData(joinGameRequest.gameID(), authData.username(), game.blackUsername(), game.gameName(), game.watchers(), game.game());
                } else if (Objects.equals(joinGameRequest.playerColor(), "BLACK")) {
                    if (game.blackUsername() != null && !Objects.equals(game.blackUsername(), authData.username())) {
                        throw new AlreadyTakenException("Error: Already taken");
                    }
                    newGame = new GameData(joinGameRequest.gameID(), game.whiteUsername(), authData.username(), game.gameName(), game.watchers(), game.game());
                } else {
                    var newWatchers = game.watchers();
                    newWatchers.add(authData.username());
                    newGame = new GameData(joinGameRequest.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), newWatchers, game.game());
                }
                gameDB.remove(game);
                gameDB.add(newGame);
                break;
            }
        }
        if (!foundGame) {
            throw new NotFoundException("Error: Could not find game");
        }
    }

    public String makeMoveGame(AuthData authData, long gameID, ChessMove move) throws DataAccessException, NotFoundException {

        return null;
    }

    public void removePlayer(AuthData authData, long gameID) {

    }

    public void endGame(long gameID) {

    }

    public Object clear() {
        userDB = new ArrayList<>();
        authDB = new ArrayList<>();
        gameDB = new ArrayList<>();
        return null;
    }
}
