package websocketConnectionClient;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.GameData;
import serverMessageObserver.ServerMessageObserver;
import webSocketMessages.serverMessages.ServerLoadGame;

import javax.websocket.*;
import java.net.URI;

public class WebsocketConnectionClient extends Endpoint {
    private Session session;
    private ServerMessageObserver serverMessageObserver;

    public WebsocketConnectionClient(String URL, ServerMessageObserver serverMessageObserver) throws Exception {
        URI uri = new URI(URL);
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);
        this.serverMessageObserver = serverMessageObserver;

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();
                switch (jsonObject.get("serverMessageType").getAsString()) {
                    case "ERROR":
                        serverMessageObserver.printMessage(jsonObject.get("errorMessage").getAsString());
                        break;
                    case "LOAD_GAME":
                        serverMessageObserver.loadGame(new Gson().fromJson(message, ServerLoadGame.class).game);
                        break;
                    default:
                        serverMessageObserver.printMessage("Received unrecognized server message");
                        break;
                }

            }
        });
    }

    public void send(String message) throws Exception {
        this.session.getBasicRemote().sendText(message);
    }

    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}
