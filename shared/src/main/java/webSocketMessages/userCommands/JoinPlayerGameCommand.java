package webSocketMessages.userCommands;

public class JoinPlayerGameCommand extends UserGameCommand {
    private String playerColor;
    private long gameID;

    public JoinPlayerGameCommand(String authToken, long gameID, String playerColor) {
        super(authToken);
        commandType = CommandType.JOIN_PLAYER;
        this.playerColor = playerColor;
        this.gameID = gameID;
    }
}
