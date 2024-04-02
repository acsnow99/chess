package webSocketMessages.userCommands;

import chess.ChessMove;

public class MakeMoveGameCommand extends UserGameCommand {
    private long gameID;
    private ChessMove move;

    public MakeMoveGameCommand(String authToken, long gameID, ChessMove move) {
        super(authToken);
        commandType = CommandType.MAKE_MOVE;
        this.gameID = gameID;
        this.move = move;
    }
}
