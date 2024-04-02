package webSocketMessages.serverMessages;

public class ServerError extends ServerMessage {
    private String errorMessage;

    public ServerError(String message) {
        super(ServerMessageType.ERROR);
        this.errorMessage = message;
    }
}
