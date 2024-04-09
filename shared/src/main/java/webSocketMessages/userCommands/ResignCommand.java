package webSocketMessages.userCommands;

public class ResignCommand extends UserGameCommand {
    private long gameID;

    public ResignCommand(String authToken, long gameID) {
        super(authToken);
        commandType = CommandType.RESIGN;
        this.gameID = gameID;
    }
}
