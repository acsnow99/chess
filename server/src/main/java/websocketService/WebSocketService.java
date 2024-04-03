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
import webSocketMessages.serverMessages.ServerError;
import webSocketMessages.serverMessages.ServerLoadGame;

@WebSocket
public class WebSocketService {
    private GameService gameService;
    private DataAccess dataAccess;

    public WebSocketService(DataAccess dataAccess, GameService gameService) {
        this.dataAccess = dataAccess;
        this.gameService = gameService;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();
        if (jsonObject.has("commandType")) {
            var commandType = jsonObject.get("commandType").toString();
            switch (commandType) {
                case "\"JOIN_PLAYER\"", "\"JOIN_OBSERVER\"":
                    userLoadGame(session, jsonObject.get("gameID").getAsInt());
                    break;
                case "\"MAKE_MOVE\"":
                    gameService.makeMoveGame(dataAccess, new AuthData("", jsonObject.get("authToken").getAsString()),
                            jsonObject.get("gameID").getAsLong(),
                            new Gson().fromJson(jsonObject.get("move"), ChessMove.class));
                    userLoadGame(session, jsonObject.get("gameID").getAsInt());
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
    }
}
