import chess.*;
import server.Server;
import spark.Spark;
import spark.servlet.SparkApplication;

public class Main {
    public static void main(String[] args) {
        var port = 8080;
        var chessServer = new Server();
        chessServer.run(port);

        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);
    }
}