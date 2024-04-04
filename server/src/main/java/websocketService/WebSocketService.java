package websocketService;

import chess.ChessMove;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dataAccess.DataAccess;
import exceptions.DataAccessException;
import model.AuthData;
import org.eclipse.jetty.websocket.api.Session;
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
            switch (commandType) {
                case "\"JOIN_PLAYER\"", "\"JOIN_OBSERVER\"":
                    String username = registrationService.getUsernameFromToken(dataAccess, new AuthData("", authToken));
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

                    long gameID = jsonObject.get("gameID").getAsLong();
                    var game = gameService.getGameByID(dataAccess, gameID);
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
                    gameService.makeMoveGame(dataAccess, new AuthData("", authToken),
                            jsonObject.get("gameID").getAsLong(),
                            new Gson().fromJson(jsonObject.get("move"), ChessMove.class));
                    userLoadGame(session, jsonObject.get("gameID").getAsLong());
                    break;
                default:
                    sendError(session, "Error: Command type does not exist");
                    break;
            }
        } else {
            sendError(session, "Error: Command type not included");
        }
    }

    private void userLoadGame(Session session, long gameID) throws Exception {
        var game = gameService.getGameByID(dataAccess, gameID);
        session.getRemote().sendString(new Gson().toJson(new ServerLoadGame(game)));
        System.out.println("Sent message");
    }

    private void sendError(Session session, String message) throws Exception {
        session.getRemote().sendString(new Gson().toJson(new ServerError(message)));
    }

    private void addConnection(String authToken, WebSocketConnection session) {
        sessions.put(authToken, session);
    }

    private void broadcast(String message, String excludedAuthToken) throws Exception {
        for (var authToken : sessions.keySet()) {
            if (!Objects.equals(authToken, excludedAuthToken)) {
                sessions.get(authToken).session().getRemote().sendString(new Gson().toJson(new ServerNotification(message)));
            }
        }
    }
}
