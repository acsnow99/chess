package websocketService;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dataAccess.DataAccess;
import exceptions.DataAccessException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import services.GameService;
import services.RegistrationService;
import webSocketMessages.serverMessages.ServerError;
import webSocketMessages.serverMessages.ServerLoadGame;
import webSocketMessages.serverMessages.ServerNotification;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class WebSocketService {
    private GameService gameService;
    private RegistrationService registrationService;
    private DataAccess dataAccess;
    private ConcurrentHashMap<String, WebSocketConnection> sessions = new ConcurrentHashMap<>();

    public WebSocketService(DataAccess dataAccess, GameService gameService, RegistrationService registrationService) {
        this.dataAccess = dataAccess;
        this.gameService = gameService;
        this.registrationService = registrationService;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();
        if (jsonObject.has("commandType")) {
            var authToken = jsonObject.get("authToken").getAsString();
            var commandType = jsonObject.get("commandType").toString();
            String username;
            long gameID;
            GameData game;
            switch (commandType) {
                case "\"JOIN_PLAYER\"", "\"JOIN_OBSERVER\"":
                    username = registrationService.getUsernameFromToken(dataAccess, new AuthData("", authToken));
                    if (Objects.equals(username, "")) {
                        sendError(session, "Error: Bad token");
                        break;
                    }

                    String playerColor;
                    try {
                        playerColor = jsonObject.get("playerColor").getAsString();
                    } catch (Exception exception) {
                        playerColor = "an observer";
                    }

                    gameID = jsonObject.get("gameID").getAsLong();
                    game = gameService.getGameByID(dataAccess, gameID);
                    if (game == null) {
                        sendError(session, "Error: Game does not exist");
                        break;
                    }

                    if (Objects.equals(playerColor, "WHITE")) {
                        if (!(Objects.equals(game.whiteUsername(), username))) {
                            sendError(session, "Error: Spot taken");
                            break;
                        }
                    } else if (Objects.equals(playerColor, "BLACK")) {
                        if (!(Objects.equals(game.blackUsername(), username))) {
                            sendError(session, "Error: Spot taken");
                            break;
                        }
                    }
                    userLoadGame(session, gameID);
                    var wsSession = new WebSocketConnection(gameID, session);
                    addConnection(authToken, wsSession);
                    broadcast(username + " joined the game as " + playerColor.toLowerCase(), authToken);
                    break;
                case "\"MAKE_MOVE\"":
                    System.out.println("Trying to make move");
                    username = registrationService.getUsernameFromToken(dataAccess, new AuthData("", authToken));
                    if (Objects.equals(username, "")) {
                        sendError(session, "Error: Bad token");
                        break;
                    }
                    gameID = jsonObject.get("gameID").getAsLong();
                    game = gameService.getGameByID(dataAccess, gameID);
                    if (game == null) {
                        sendError(session, "Error: Game does not exist");
                        break;
                    }

                    try {
                        gameService.makeMoveGame(dataAccess, new AuthData("", authToken),
                                gameID,
                                new Gson().fromJson(jsonObject.get("move"), ChessMove.class));
                    } catch (InvalidMoveException exception) {
                        sendError(session, "Error: Invalid move because " + exception.getMessage());
                        break;
                    }
                    System.out.println("Trying to broadcast loadgame message");
                    // send game to everyone, including the one doing the move
                    broadcastLoadGame(gameID, "");
                    broadcast("Hey a move was made", authToken);
                    break;
                case "\"LEAVE\"":
                    System.out.println("Player leaving");
                    username = registrationService.getUsernameFromToken(dataAccess, new AuthData("", authToken));
                    if (Objects.equals(username, "")) {
                        sendError(session, "Error: Bad token");
                        break;
                    }
                    gameID = jsonObject.get("gameID").getAsLong();
                    game = gameService.getGameByID(dataAccess, gameID);
                    if (game == null) {
                        sendError(session, "Error: Game does not exist");
                        break;
                    }
                    sessions.remove(authToken);
                    broadcast(username + " left the game", authToken);
                    gameService.removePlayer(dataAccess, authToken, gameID);
                    break;
                case "\"RESIGN\"":
                    System.out.println("Player resigning");
                    username = registrationService.getUsernameFromToken(dataAccess, new AuthData("", authToken));
                    if (Objects.equals(username, "")) {
                        sendError(session, "Error: Bad token");
                        break;
                    }
                    gameID = jsonObject.get("gameID").getAsLong();
                    game = gameService.getGameByID(dataAccess, gameID);
                    if (game == null) {
                        sendError(session, "Error: Game does not exist");
                        break;
                    }
                    broadcast(username + " forfeited the game", "");
                    gameService.setGameFinished(dataAccess, gameID);
                    break;
                default:
                    sendError(session, "Error: Command type does not exist");
                    break;
            }
        } else {
            sendError(session, "Error: Command type not included");
        }
    }

    @OnWebSocketClose
    public void onWebsocketClose(Session session, int statusCode, String reason) {
        System.out.println("Closed connection");
    }

    @OnWebSocketError
    public void onWebsocketError(Throwable exception) {
        exception.printStackTrace();
    }

    private void userLoadGame(Session session, long gameID) throws Exception {
        var game = gameService.getGameByID(dataAccess, gameID);
        if (session.isOpen()) {
            session.getRemote().sendString(new Gson().toJson(new ServerLoadGame(game)));
            System.out.println("Loaded game individually");
        } else {
            System.out.println("Connection closed...");
        }
    }

    private void broadcastLoadGame(long gameID, String excludedAuthToken) throws DataAccessException {
        var game = gameService.getGameByID(dataAccess, gameID);
        for (var authToken : sessions.keySet()) {
            if (!Objects.equals(authToken, excludedAuthToken)) {
                try {
                    var s = sessions.get(authToken).session();
                    if (s.isOpen()) {
                        s.getRemote().sendString(new Gson().toJson(new ServerLoadGame(game)));
                    } else {
                        System.out.println("Connection closed...");
                    }
                } catch (Exception exception) {
                    System.out.println("Error: Json conversion failed");
                }
            }
        }
    }

    private void sendError(Session session, String message) throws Exception {
        System.out.println("Trying to send error message");
        if (session.isOpen()) {
            session.getRemote().sendString(new Gson().toJson(new ServerError(message)));
        } else {
            System.out.println("Connection closed...");
        }
    }

    private void addConnection(String authToken, WebSocketConnection session) {
        sessions.put(authToken, session);
    }

    private void broadcast(String message, String excludedAuthToken) {
        System.out.println("Trying to broadcast message");
        for (var authToken : sessions.keySet()) {
            if (!Objects.equals(authToken, excludedAuthToken)) {
                try {
                    var s = sessions.get(authToken).session();
                    if (s.isOpen()) {
                        s.getRemote().sendString(new Gson().toJson(new ServerNotification(message)));
                    } else {
                        System.out.println("Connection closed...");
                    }
                } catch (Exception exception) {
                    System.out.println("Error: Json conversion failed");
                }
            }
        }
    }
}
