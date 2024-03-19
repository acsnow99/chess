import chess.*;
import server.Server;
import ui.Repl;

public class Main {

    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        var server = new Server();
        int port = 8080;
        port = server.run(port);
        new Repl().run(port);
    }
}