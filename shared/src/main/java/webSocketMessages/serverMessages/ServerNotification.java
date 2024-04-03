package webSocketMessages.serverMessages;

public class ServerNotification extends ServerMessage {
    private String message;

    public ServerNotification(String message) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }
}
