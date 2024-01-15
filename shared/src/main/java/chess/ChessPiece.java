package chess;

import java.util.Collection;
import java.util.HashSet;

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

        Collection<ChessMove> available_moves = new HashSet<>();
        PieceType piece_type = this.getPieceType();

        switch (piece_type) {

            case BISHOP:
                var _row = myPosition.getRow();
                var _column = myPosition.getColumn();

                /**
                 * Down-right directional check
                 */
                while (_row < 8 && _column < 8) {
                    _row++;
                    _column++;
                    var position_to_check = new ChessPosition(_row, _column);
                    var piece_at_position = board.getPiece(position_to_check);

                    /**
                     * Add the move from start position to the position we're checking for pieces,
                     * whether there's a piece there or not
                     */
                    var move_to_add = new ChessMove(myPosition, position_to_check, null);
                    available_moves.add(move_to_add);

                    /**
                     * break out of while loop if there is a piece at that position
                     */
                    if (piece_at_position != null) {
                        _row = 9;
                    }
                }

                _row = myPosition.getRow();
                _column = myPosition.getColumn();
                /**
                 * Down-left directional check
                 */
                while (_row > 1 && _column < 8) {
                    _row--;
                    _column++;
                    var position_to_check = new ChessPosition(_row, _column);
                    var piece_at_position = board.getPiece(position_to_check);

                    /**
                     * Add the move from start position to the position we're checking for pieces,
                     * whether there's a piece there or not
                     */
                    var move_to_add = new ChessMove(myPosition, position_to_check, null);
                    available_moves.add(move_to_add);

                    /**
                     * break out of while loop if there is a piece at that position
                     */
                    if (piece_at_position != null) {
                        _row = 0;
                    }
                }

                _row = myPosition.getRow();
                _column = myPosition.getColumn();
                /**
                 * Up-left directional check
                 */
                while (_row > 1 && _column > 1) {
                    _row--;
                    _column--;
                    var position_to_check = new ChessPosition(_row, _column);
                    var piece_at_position = board.getPiece(position_to_check);

                    /**
                     * Add the move from start position to the position we're checking for pieces,
                     * whether there's a piece there or not
                     */
                    var move_to_add = new ChessMove(myPosition, position_to_check, null);
                    available_moves.add(move_to_add);

                    /**
                     * break out of while loop if there is a piece at that position
                     */
                    if (piece_at_position != null) {
                        _row = 0;
                    }
                }

                _row = myPosition.getRow();
                _column = myPosition.getColumn();
                /**
                 * Up-right directional check
                 */
                while (_row < 8 && _column > 1) {
                    _row++;
                    _column--;
                    var position_to_check = new ChessPosition(_row, _column);
                    var piece_at_position = board.getPiece(position_to_check);

                    /**
                     * Add the move from start position to the position we're checking for pieces,
                     * whether there's a piece there or not
                     */
                    var move_to_add = new ChessMove(myPosition, position_to_check, null);
                    available_moves.add(move_to_add);

                    /**
                     * break out of while loop if there is a piece at that position
                     */
                    if (piece_at_position != null) {
                        _column = 0;
                    }
                }
                break;
            default: break;

        }

        return available_moves;

    }
}
