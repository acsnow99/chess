package websocketService;

import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import spark.Spark;

import javax.websocket.Session;

@WebSocket
public class WebsocketService {
    public WebsocketService(String path) {
        Spark.webSocket(path, WebsocketService.class);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        System.out.printf("Received: %s", message);
        session.getBasicRemote().sendText("WebSocket response: " + message);
    }
}
