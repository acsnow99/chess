package chess;

import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor currentTeamTurn = TeamColor.WHITE;

    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.currentTeamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.currentTeamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        var pieceAtPosition = this.getBoard().getPiece(startPosition);
        if (pieceAtPosition == null) {
            return null;
        }
        return pieceAtPosition.pieceMoves(this.getBoard(), startPosition);
    }

    private boolean moveResultsInCheckOnSelf(ChessMove move, TeamColor color) {
        var startPosition = move.getStartPosition();
        var endPosition = move.getEndPosition();
        var pieceAtStart = this.board.getPiece(startPosition);
        var pieceAtEnd = this.board.getPiece(endPosition);
        this.board.movePiece(move, pieceAtStart);
        var moveBack = new ChessMove(endPosition, startPosition, null);
        var inCheck = this.isInCheck(color);
        this.board.movePiece(moveBack, pieceAtStart);
        this.board.addPiece(endPosition, pieceAtEnd);
        return inCheck;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        var endPosition = move.getEndPosition();
        if (endPosition.outOfBounds()) {
            throw new InvalidMoveException("Move tries to move piece out of bounds");
        }
        var pieceToMove = board.getPiece(move.getStartPosition());
        if (pieceToMove == null) {
            throw new InvalidMoveException("Piece doesn't exist at position that it trying to move");
        }
        var colorOfPiece = pieceToMove.getTeamColor();
        if (colorOfPiece != this.getTeamTurn()) {
            throw new InvalidMoveException("Piece is not on current player's team");
        }
        var valid = false;
        var startPosition = move.getStartPosition();
        var validMoves = this.validMoves(startPosition);
        for (var moveToCheck : validMoves) {
            var endPosToCheck = moveToCheck.getEndPosition();
            if (endPosToCheck.equals(endPosition)) {
                valid = true;
                break;
            }
        }
        if (!valid) {
            throw new InvalidMoveException("Piece cannot make provided move");
        }
        if (this.isInCheck(this.getTeamTurn()) && this.moveResultsInCheckOnSelf(move, this.getTeamTurn())) {
            throw new InvalidMoveException("Player is in check and provided move will not get them out");
        }
        this.board.movePiece(move, pieceToMove);
        if (this.getTeamTurn() == TeamColor.WHITE) {
            this.setTeamTurn(TeamColor.BLACK);
        } else {
            this.setTeamTurn(TeamColor.WHITE);
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        var numRows = this.board.getBoardNumRows();
        var numCols = this.board.getBoardNumCols();
        for (var r = 0; r < numRows; r++) {
            for (var c = 0; c < numCols; c++) {
                var positionToCheck = new ChessPosition(r, c);
                var pieceToCheck = this.board.getPiece(positionToCheck);
                // if there is no piece there, or if the piece there is the same color
                //  as the king being checked
                if (pieceToCheck == null || pieceToCheck.getTeamColor() == teamColor) {
                    continue;
                } else {
                    var movesToCheck = pieceToCheck.pieceMoves(this.board, positionToCheck);
                    for (ChessMove move : movesToCheck) {
                        var endPosition = move.getEndPosition();
                        var endPieceToCheck = board.getPiece(endPosition);
                        if (endPieceToCheck != null && endPieceToCheck.getPieceType() == ChessPiece.PieceType.KING) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        var isInCheck = this.isInCheck(teamColor);
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }
}
