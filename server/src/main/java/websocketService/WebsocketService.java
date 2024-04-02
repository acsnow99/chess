package websocketService;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import webSocketMessages.serverMessages.ServerError;

@WebSocket
public class WebsocketService {
    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();
        if (jsonObject.has("commandType")) {
            var commandType = jsonObject.get("commandType").toString();
            switch (commandType) {
                case "\"JOIN_PLAYER\"":
                    session.getRemote().sendString("Joined");
                    break;
                default:
                    session.getRemote().sendString(new Gson().toJson(new ServerError("Error: Command type does not exist")));
                    break;
            }
        } else {
            session.getRemote().sendString(new Gson().toJson(new ServerError("Error: Command type not included")));
        }
    }
}
