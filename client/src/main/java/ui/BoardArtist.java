package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;

import java.util.ArrayList;
import java.util.Objects;

public class BoardArtist {
    private String currentColor = EscapeSequences.SET_BG_COLOR_DARK_GREY;

    public String drawBoard(GameData game) {
        ChessBoard boardData = game.game().getBoard();
        String board = "";

        board += EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR
                + "\n    A  B  C  D  E  F  G  H  \n";
        for (var row = 8; row > 0; row--) {
            board += getRowString(row, boardData);
        }
        board += EscapeSequences.RESET_BG_COLOR + "    A  B  C  D  E  F  G  H  \n";

        return board;
    }

    private String getRowString(int row, ChessBoard boardData) {
        int column = 1;
        ArrayList<ChessPiece> rowPieces = new ArrayList<>(8);
        getCurrentColor();
        return (" " + row + " " + getCurrentColor() + getPieceString(boardData.getPiece(new ChessPosition(row, column++)))
                + getCurrentColor() + getPieceString(boardData.getPiece(new ChessPosition(row, column++)))
                + getCurrentColor() + getPieceString(boardData.getPiece(new ChessPosition(row, column++)))
                + getCurrentColor() + getPieceString(boardData.getPiece(new ChessPosition(row, column++)))
                + getCurrentColor() + getPieceString(boardData.getPiece(new ChessPosition(row, column++)))
                + getCurrentColor() + getPieceString(boardData.getPiece(new ChessPosition(row, column++)))
                + getCurrentColor() + getPieceString(boardData.getPiece(new ChessPosition(row, column++)))
                + getCurrentColor() + getPieceString(boardData.getPiece(new ChessPosition(row, column))) +
                EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR + " " + row + "\n");
    }

    private String getPieceString(ChessPiece piece) {
        if (piece == null) {
            return EscapeSequences.EMPTY;
        }
        boolean isWhite = piece.getTeamColor() == ChessGame.TeamColor.WHITE;
        String returnString = switch (piece.getPieceType()) {
            case BISHOP -> isWhite ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
            case KING -> isWhite ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
            case KNIGHT -> isWhite ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
            case PAWN -> isWhite ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
            case QUEEN -> isWhite ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
            case ROOK -> isWhite ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
            default -> "";
        };
        return returnString;
    }

    private String getCurrentColor() {
        if (Objects.equals(currentColor, EscapeSequences.SET_BG_COLOR_DARK_GREY)) {
            currentColor = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
        } else {
            currentColor = EscapeSequences.SET_BG_COLOR_DARK_GREY;
        }
        return currentColor;
    }
}