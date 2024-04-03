package websocketService;

import org.eclipse.jetty.websocket.api.Session;

public record WebSocketConnection(long gameID, Session session) {
}
