package websocketService;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;

@WebSocket
public class WebsocketService {
    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();
        if (jsonObject.has("commandType")) {
            var commandType = jsonObject.get("commandType").toString();
            switch (commandType) {
                case "JOIN_PLAYER":

                    session.getRemote().sendString("Joined");
                    break;
            }
        } else {
            session.getRemote().sendString("Error: Command type not included");
        }
    }
}
