package websocketService;

import chess.ChessMove;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dataAccess.DataAccess;
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
    private ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();

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
                    addConnection(authToken, session);
                    userLoadGame(session, jsonObject.get("gameID").getAsLong());
                    var username = registrationService.getUsernameFromToken(dataAccess, new AuthData("", authToken));
                    var playerColor = jsonObject.get("playerColor").getAsString();
                    if (Objects.equals(playerColor, "") || playerColor == null) {
                        playerColor = "an observer";
                    }
                    broadcast(username + " joined the game as " + playerColor.toLowerCase(), authToken);
                    break;
                case "\"MAKE_MOVE\"":
                    gameService.makeMoveGame(dataAccess, new AuthData("", authToken),
                            jsonObject.get("gameID").getAsLong(),
                            new Gson().fromJson(jsonObject.get("move"), ChessMove.class));
                    userLoadGame(session, jsonObject.get("gameID").getAsLong());
                    break;
                default:
                    session.getRemote().sendString(new Gson().toJson(new ServerError("Error: Command type does not exist")));
                    break;
            }
        } else {
            session.getRemote().sendString(new Gson().toJson(new ServerError("Error: Command type not included")));
        }
    }

    private void userLoadGame(Session session, long gameID) throws Exception {
        var game = gameService.getGameByID(dataAccess, gameID);
        session.getRemote().sendString(new Gson().toJson(new ServerLoadGame(game)));
        System.out.println("Sent message");
    }

    private void addConnection(String authToken, Session session) {
        sessions.put(authToken, session);
    }

    private void broadcast(String message, String excludedAuthToken) throws Exception {
        for (var authToken : sessions.keySet()) {
            if (!Objects.equals(authToken, excludedAuthToken)) {
                sessions.get(authToken).getRemote().sendString(new Gson().toJson(new ServerNotification(message)));
            }
        }
    }
}
