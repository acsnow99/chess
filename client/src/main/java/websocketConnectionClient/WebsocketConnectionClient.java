package websocketConnectionClient;

import javax.websocket.*;
import java.net.URI;

public class WebsocketConnectionClient extends Endpoint {
    private Session session;

    public WebsocketConnectionClient(String URL) throws Exception {
        URI uri = new URI(URL + "/joinws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                System.out.println(message);
            }
        });
    }

    public void send(String message) throws Exception {
        this.session.getBasicRemote().sendText(message);
    }

    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}
