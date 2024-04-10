package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;

import java.util.ArrayList;
import java.util.Objects;

public class BoardArtist {
    private final ChessPosition highlightPosition;
    private boolean willHighlight;
    private ChessGame game;
    private String currentColor = EscapeSequences.SET_BG_COLOR_DARK_GREY;

    public BoardArtist(ChessGame game, ChessPosition highlightPosition) {
        this.willHighlight = highlightPosition != null;
        this.highlightPosition = highlightPosition;
        this.game = game;
    }

    public String drawBoard(GameData game, ChessGame.TeamColor color) {
        ChessBoard boardData = game.game().getBoard();
        String board = "";


        if (color == ChessGame.TeamColor.WHITE) {
            board += EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR
                    + "\n    A  B  C  D  E  F  G  H  \n";
            for (var row = 8; row > 0; row--) {
                board += getRowString(row, boardData, ChessGame.TeamColor.WHITE);
            }
            board += EscapeSequences.RESET_BG_COLOR + "    A  B  C  D  E  F  G  H  \n";
        } else {
            board += EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR
                    + "\n    H  G  F  E  D  C  B  A  \n";
            for (var row = 1; row <= 8; row++) {
                board += getRowString(row, boardData, ChessGame.TeamColor.BLACK);
            }
            board += EscapeSequences.RESET_BG_COLOR + "    H  G  F  E  D  C  B  A  \n";
        }

        return board;
    }

    private String getRowString(int row, ChessBoard boardData, ChessGame.TeamColor color) {
        if (color == ChessGame.TeamColor.WHITE) {
            int column = 1;
            ArrayList<ChessPiece> rowPieces = new ArrayList<>(8);
            getCurrentColor(null);
            return (" " + row + " " + getCurrentColor(new ChessPosition(row, column)) + getPieceString(boardData.getPiece(new ChessPosition(row, column++)))
                    + getCurrentColor(new ChessPosition(row, column)) + getPieceString(boardData.getPiece(new ChessPosition(row, column++)))
                    + getCurrentColor(new ChessPosition(row, column)) + getPieceString(boardData.getPiece(new ChessPosition(row, column++)))
                    + getCurrentColor(new ChessPosition(row, column)) + getPieceString(boardData.getPiece(new ChessPosition(row, column++)))
                    + getCurrentColor(new ChessPosition(row, column)) + getPieceString(boardData.getPiece(new ChessPosition(row, column++)))
                    + getCurrentColor(new ChessPosition(row, column)) + getPieceString(boardData.getPiece(new ChessPosition(row, column++)))
                    + getCurrentColor(new ChessPosition(row, column)) + getPieceString(boardData.getPiece(new ChessPosition(row, column++)))
                    + getCurrentColor(new ChessPosition(row, column)) + getPieceString(boardData.getPiece(new ChessPosition(row, column))) +
                    EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR + " " + row + "\n");
        } else {
            int column = 8;
            ArrayList<ChessPiece> rowPieces = new ArrayList<>(8);
            getCurrentColor(null);
            return (" " + row + " " + getCurrentColor(new ChessPosition(row, column)) + getPieceString(boardData.getPiece(new ChessPosition(row, column--)))
                    + getCurrentColor(new ChessPosition(row, column)) + getPieceString(boardData.getPiece(new ChessPosition(row, column--)))
                    + getCurrentColor(new ChessPosition(row, column)) + getPieceString(boardData.getPiece(new ChessPosition(row, column--)))
                    + getCurrentColor(new ChessPosition(row, column)) + getPieceString(boardData.getPiece(new ChessPosition(row, column--)))
                    + getCurrentColor(new ChessPosition(row, column)) + getPieceString(boardData.getPiece(new ChessPosition(row, column--)))
                    + getCurrentColor(new ChessPosition(row, column)) + getPieceString(boardData.getPiece(new ChessPosition(row, column--)))
                    + getCurrentColor(new ChessPosition(row, column)) + getPieceString(boardData.getPiece(new ChessPosition(row, column--)))
                    + getCurrentColor(new ChessPosition(row, column)) + getPieceString(boardData.getPiece(new ChessPosition(row, column))) +
                    EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR + " " + row + "\n");
        }
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

    private String getCurrentColor(ChessPosition position) {
        if (willHighlight && position != null) {
            // Highlight moves that the piece can make
            var moves = game.validMoves(highlightPosition);
            for (var move : moves) {
                if (move.getEndPosition().equals(position)) {
                    currentColor = EscapeSequences.SET_BG_COLOR_MAGENTA;
                    break;
                }
            }
        }
        // Alternate between the gray colors
        if (Objects.equals(currentColor, EscapeSequences.SET_BG_COLOR_DARK_GREY)) {
            currentColor = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
        } else {
            currentColor = EscapeSequences.SET_BG_COLOR_DARK_GREY;
        }
        return currentColor;
    }
}
