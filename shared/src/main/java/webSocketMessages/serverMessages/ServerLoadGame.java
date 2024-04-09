package webSocketMessages.serverMessages;

import model.GameData;

public class ServerLoadGame extends ServerMessage {
    public GameData game;

    public ServerLoadGame(GameData game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }
}
