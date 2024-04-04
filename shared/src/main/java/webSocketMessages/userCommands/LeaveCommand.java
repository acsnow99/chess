package webSocketMessages.userCommands;

import chess.ChessMove;

public class LeaveCommand extends UserGameCommand {
    private long gameID;

    public LeaveCommand(String authToken, long gameID) {
        super(authToken);
        commandType = CommandType.LEAVE;
        this.gameID = gameID;
    }
}
