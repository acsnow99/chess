package serverFacade;

import chess.ChessMove;
import com.google.gson.Gson;
import model.AuthData;
import serverMessageObserver.ServerMessageObserver;
import webSocketMessages.userCommands.JoinPlayerGameCommand;
import webSocketMessages.userCommands.LeaveCommand;
import webSocketMessages.userCommands.MakeMoveGameCommand;
import webSocketMessages.userCommands.ResignCommand;
import websocketConnectionClient.WebsocketConnectionClient;

import javax.websocket.OnMessage;

public class ServerFacadeWebsocket {
    private final String serverURL;
    private WebsocketConnectionClient websocketConnection;

    public ServerFacadeWebsocket(String url, ServerMessageObserver serverMessageObserver) throws Exception {
        serverURL = url;
        websocketConnection = new WebsocketConnectionClient(url, serverMessageObserver);
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

    public void resign(AuthData authorization, long gameID) throws Exception {
        var command = new ResignCommand(authorization.authToken(), gameID);
        var message = new Gson().toJson(command);
        websocketConnection.send(message);
    }
}
