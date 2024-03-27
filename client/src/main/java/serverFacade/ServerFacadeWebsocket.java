package serverFacade;

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
}
