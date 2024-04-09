package serverFacade;

import chess.ChessMove;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.AuthData;
import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.userCommands.JoinPlayerGameCommand;
import webSocketMessages.userCommands.LeaveCommand;
import webSocketMessages.userCommands.MakeMoveGameCommand;
import websocketConnectionClient.WebsocketConnectionClient;

import javax.websocket.OnMessage;

public class ServerFacadeWebsocket {
    private final String serverURL;
    private WebsocketConnectionClient websocketConnection;

    public ServerFacadeWebsocket(String URL) throws Exception {
        serverURL = URL;
        websocketConnection = new WebsocketConnectionClient(URL);
    }

    @OnMessage
    public void onServerMessage(Session session, String message) {
        JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();
        switch (jsonObject.get("commandType").getAsString()) {
            case "\"ERROR\"":
                System.out.println(jsonObject.get("errorMessage"));
                break;
            default:
                System.out.println("Received unrecognized server message");
                break;
        }
    }

    public void send(String message) throws Exception {
        websocketConnection.send(message);
    }

    public void joinPlayer(AuthData authData, long gameID, String playerColor) throws Exception {
        var command = new JoinPlayerGameCommand(authData.authToken(), gameID, playerColor);
        var message = new Gson().toJson(command);
        websocketConnection.send(message);
    }

    public void leavePlayer(AuthData authData, long gameID) throws Exception {
        var command = new LeaveCommand(authData.authToken(), gameID);
        var message = new Gson().toJson(command);
        websocketConnection.send(message);
    }

    public void makeMove(AuthData authData, long gameID, ChessMove move) throws Exception {
        var command = new MakeMoveGameCommand(authData.authToken(), gameID, move);
        var message = new Gson().toJson(command);
        websocketConnection.send(message);
    }
}
