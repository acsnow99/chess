package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private ChessGame.TeamColor pieceColor;
    private PieceType pieceType;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.pieceType = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> allMoves = new HashSet<ChessMove>();

        switch (this.getPieceType()) {
            case BISHOP:
                this.getMoves(board, myPosition, allMoves, 1, 1, true, false, true);
                this.getMoves(board, myPosition, allMoves, 1, -1, true, false, true);
                this.getMoves(board, myPosition, allMoves, -1, 1, true, false, true);
                this.getMoves(board, myPosition, allMoves, -1, -1, true, false, true);
                break;
            case KING:
                this.getMoves(board, myPosition, allMoves, 1, 0, false, false, true);
                this.getMoves(board, myPosition, allMoves, 1, 1, false, false, true);
                this.getMoves(board, myPosition, allMoves, 1, -1, false, false, true);
                this.getMoves(board, myPosition, allMoves, -1, 1, false, false, true);
                this.getMoves(board, myPosition, allMoves, -1, 0, false, false, true);
                this.getMoves(board, myPosition, allMoves, -1, -1, false, false, true);
                this.getMoves(board, myPosition, allMoves, 0, 1, false, false, true);
                this.getMoves(board, myPosition, allMoves, 0, -1, false, false, true);
                break;
            case KNIGHT:
                this.getMoves(board, myPosition, allMoves, 2, 1, false, false, true);
                this.getMoves(board, myPosition, allMoves, 2, -1, false, false, true);
                this.getMoves(board, myPosition, allMoves, 1, 2, false, false, true);
                this.getMoves(board, myPosition, allMoves, 1, -2, false, false, true);
                this.getMoves(board, myPosition, allMoves, -2, 1, false, false, true);
                this.getMoves(board, myPosition, allMoves, -2, -1, false, false, true);
                this.getMoves(board, myPosition, allMoves, -1, 2, false, false, true);
                this.getMoves(board, myPosition, allMoves, -1, -2, false, false, true);
                break;
            case ROOK:
                this.getMoves(board, myPosition, allMoves, 1, 0, true, false, true);
                this.getMoves(board, myPosition, allMoves, -1, 0, true, false, true);
                this.getMoves(board, myPosition, allMoves, 0, 1, true, false, true);
                this.getMoves(board, myPosition, allMoves, 0, -1, true, false, true);
                break;
            case QUEEN:
                this.getMoves(board, myPosition, allMoves, 1, 1, true, false, true);
                this.getMoves(board, myPosition, allMoves, 1, -1, true, false, true);
                this.getMoves(board, myPosition, allMoves, -1, 1, true, false, true);
                this.getMoves(board, myPosition, allMoves, -1, -1, true, false, true);

                this.getMoves(board, myPosition, allMoves, 1, 0, true, false, true);
                this.getMoves(board, myPosition, allMoves, -1, 0, true, false, true);
                this.getMoves(board, myPosition, allMoves, 0, 1, true, false, true);
                this.getMoves(board, myPosition, allMoves, 0, -1, true, false, true);
                break;
            case PAWN:
                var vertDir = 0;
                if (this.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    vertDir = 1;
                } else {
                    vertDir = -1;
                }

                // forward march
                this.getMoves(board, myPosition, allMoves, vertDir, 0, false, true, false);
                // attack!
                this.getMoves(board, myPosition, allMoves, vertDir, 1, false, true, true);
                this.getMoves(board, myPosition, allMoves, vertDir, -1, false, true, true);
                break;
        }

        return allMoves;
    }

    private void getMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> allMoves, int vertDir, int horizDir, boolean isRecursive, boolean isPawn, boolean canCapture) {
        var row = myPosition.getRow();
        var col = myPosition.getColumn();
        ChessPosition newPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn());
        ChessPiece pieceAtPosition;

        row += vertDir;
        col += horizDir;
        newPosition = new ChessPosition(row, col);

        if (isRecursive) {
            while (newPosition.isInBounds()) {
                pieceAtPosition = board.getPiece(newPosition);
                if (pieceAtPosition != null) {
                    if (canCapture && pieceAtPosition.getTeamColor() != this.getTeamColor()) {
                        allMoves.add(new ChessMove(myPosition, newPosition, null));
                    }
                    break;
                } else {
                    allMoves.add(new ChessMove(myPosition, newPosition, null));
                }
                row += vertDir;
                col += horizDir;
                newPosition = new ChessPosition(row, col);
            }
        } else {
            if (newPosition.isInBounds()) {
                pieceAtPosition = board.getPiece(newPosition);
                if (pieceAtPosition != null) {
                    if (canCapture && pieceAtPosition.getTeamColor() != this.getTeamColor()) {
                        if (isPawn && myPosition.canPromotePawn(this.getTeamColor())) {
                            allMoves = addMoveWithEachType(allMoves, myPosition, newPosition);
                        } else {
                            allMoves.add(new ChessMove(myPosition, newPosition, null));
                        }
                    }
                } else if (!(isPawn && canCapture)) {
                    // ^^ pawns can't move into an empty space if they are moving diagonally - they must capture
                    if (isPawn && myPosition.canPromotePawn(this.getTeamColor())) {
                        allMoves = addMoveWithEachType(allMoves, myPosition, newPosition);
                    } else {
                        allMoves.add(new ChessMove(myPosition, newPosition, null));
                        // pawns can move an extra space on their first move
                        if (isPawn && myPosition.isInitialPawnMove(this.getTeamColor())) {
                            row += vertDir;
                            newPosition = new ChessPosition(row, col);
                            pieceAtPosition = board.getPiece(newPosition);
                            if (pieceAtPosition == null) {
                                allMoves.add(new ChessMove(myPosition, newPosition, null));
                            }
                        }
                    }
                }
            }
        }
    }

    private Collection<ChessMove> addMoveWithEachType(Collection<ChessMove> allMoves, ChessPosition startPos, ChessPosition endPos) {
        for (PieceType type : PieceType.values()) {
            if (type != PieceType.KING && type != PieceType.PAWN) {
                allMoves.add(new ChessMove(startPos, endPos, type));
            }
        }
        return allMoves;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && pieceType == that.pieceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, pieceType);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", pieceType=" + pieceType +
                '}';
    }
}
