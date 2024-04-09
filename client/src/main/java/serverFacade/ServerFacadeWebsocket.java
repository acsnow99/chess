package serverFacade;

import chess.ChessMove;
import com.google.gson.Gson;
import model.AuthData;
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
