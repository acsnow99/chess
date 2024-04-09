package serverMessageObserver;

import model.GameData;
import ui.Repl;

public class ServerMessageObserver {
    private Repl repl;

    public ServerMessageObserver(Repl repl) {
        this.repl = repl;
    }

    public void printMessage(String message) {
        repl.printMessage(message);
    }

    public void loadGame(GameData game) {
        repl.loadGame(game);
    }
}
