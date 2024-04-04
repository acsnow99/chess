package serverFacade;

import com.google.gson.Gson;
import model.AuthData;
import webSocketMessages.userCommands.JoinPlayerGameCommand;
import webSocketMessages.userCommands.LeaveCommand;
import websocketConnectionClient.WebsocketConnectionClient;

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
}
