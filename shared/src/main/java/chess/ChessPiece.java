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
                available_moves = this.getBishopMoves(board, myPosition);
                break;
            case KING:
                available_moves = this.getKingMoves(board, myPosition);
                break;
            case KNIGHT:
                available_moves = this.getKnightMoves(board, myPosition);
                break;
            case PAWN:
                available_moves = this.getPawnMoves(board, myPosition);
                break;
            case QUEEN:
                available_moves = this.getQueenMoves(board, myPosition);
                break;
            default: break;

        }

        return available_moves;

    }

    private Collection<ChessMove> getBishopMoves(ChessBoard board, ChessPosition myPosition) {

        Collection<ChessMove> available_moves = new HashSet<>();
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

            
            var move_to_add = new ChessMove(myPosition, position_to_check, null);

            // break out of while loop if there is a piece at that position
            if (piece_at_position != null) {
                if (piece_at_position.getTeamColor() != this.getTeamColor()) {
                    available_moves.add(move_to_add);
                }
                break;
            }

            available_moves.add(move_to_add);

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

            
            var move_to_add = new ChessMove(myPosition, position_to_check, null);

            // break out of while loop if there is a piece at that position
            if (piece_at_position != null) {
                if (piece_at_position.getTeamColor() != this.getTeamColor()) {
                    available_moves.add(move_to_add);
                }
                break;
            }

            available_moves.add(move_to_add);

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

            
            var move_to_add = new ChessMove(myPosition, position_to_check, null);

            // break out of while loop if there is a piece at that position
            if (piece_at_position != null) {
                if (piece_at_position.getTeamColor() != this.getTeamColor()) {
                    available_moves.add(move_to_add);
                }
                break;
            }

            available_moves.add(move_to_add);

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

            
            var move_to_add = new ChessMove(myPosition, position_to_check, null);

            // break out of while loop if there is a piece at that position
            if (piece_at_position != null) {
                if (piece_at_position.getTeamColor() != this.getTeamColor()) {
                    available_moves.add(move_to_add);
                }
                break;
            }

            available_moves.add(move_to_add);

        }

        return available_moves;

    }

    private Collection<ChessMove> getKingMoves(ChessBoard board, ChessPosition myPosition) {

        Collection<ChessMove> available_moves = new HashSet<>();

        var _row = myPosition.getRow();
        var _column = myPosition.getColumn();
        /**
         * Right directional check
         */
        _column++;
        var position_to_check = new ChessPosition(_row, _column);
        checkMoveIsValid(board, myPosition, position_to_check, available_moves, true, false);


        _row = myPosition.getRow();
        _column = myPosition.getColumn();
        /**
         * Up-right directional check
         */
        _row++;
        _column++;
        position_to_check = new ChessPosition(_row, _column);
        checkMoveIsValid(board, myPosition, position_to_check, available_moves, true, false);


        _row = myPosition.getRow();
        _column = myPosition.getColumn();
        /**
         * Up directional check
         */
        _row++;
        position_to_check = new ChessPosition(_row, _column);
        checkMoveIsValid(board, myPosition, position_to_check, available_moves, true, false);


        _row = myPosition.getRow();
        _column = myPosition.getColumn();
        /**
         * Up-left directional check
         */
        _row++;
        _column--;
        position_to_check = new ChessPosition(_row, _column);
        checkMoveIsValid(board, myPosition, position_to_check, available_moves, true, false);


        _row = myPosition.getRow();
        _column = myPosition.getColumn();
        /**
         * left directional check
         */
        _column--;
        position_to_check = new ChessPosition(_row, _column);
        checkMoveIsValid(board, myPosition, position_to_check, available_moves, true, false);


        _row = myPosition.getRow();
        _column = myPosition.getColumn();
        /**
         * Down-left directional check
         */
        _row--;
        _column--;
        position_to_check = new ChessPosition(_row, _column);
        checkMoveIsValid(board, myPosition, position_to_check, available_moves, true, false);


        _row = myPosition.getRow();
        _column = myPosition.getColumn();
        /**
         * Down directional check
         */
        _row--;
        position_to_check = new ChessPosition(_row, _column);
        checkMoveIsValid(board, myPosition, position_to_check, available_moves, true, false);


        _row = myPosition.getRow();
        _column = myPosition.getColumn();
        /**
         * Down-right directional check
         */
        _row--;
        _column++;
        position_to_check = new ChessPosition(_row, _column);
        checkMoveIsValid(board, myPosition, position_to_check, available_moves, true, false);

        return available_moves;

    }

    private Collection<ChessMove> getKnightMoves(ChessBoard board, ChessPosition myPosition) {

        Collection<ChessMove> available_moves = new HashSet<>();

        var _row = myPosition.getRow();
        var _column = myPosition.getColumn();
        /**
         * Up-right directional check
         */
        _row += 2;
        _column++;
        var position_to_check = new ChessPosition(_row, _column);
        checkMoveIsValid(board, myPosition, position_to_check, available_moves, true, false);


        _row = myPosition.getRow();
        _column = myPosition.getColumn();
        /**
         * Up-left directional check
         */
        _row += 2;
        _column--;
        position_to_check = new ChessPosition(_row, _column);
        checkMoveIsValid(board, myPosition, position_to_check, available_moves, true, false);


        _row = myPosition.getRow();
        _column = myPosition.getColumn();
        /**
         * Right-down directional check
         */
        _row--;
        _column += 2;
        position_to_check = new ChessPosition(_row, _column);
        checkMoveIsValid(board, myPosition, position_to_check, available_moves, true, false);


        _row = myPosition.getRow();
        _column = myPosition.getColumn();
        /**
         * Right-up directional check
         */
        _row++;
        _column += 2;
        position_to_check = new ChessPosition(_row, _column);
        checkMoveIsValid(board, myPosition, position_to_check, available_moves, true, false);


        _row = myPosition.getRow();
        _column = myPosition.getColumn();
        /**
         * Left-down directional check
         */
        _row--;
        _column -= 2;
        position_to_check = new ChessPosition(_row, _column);
        checkMoveIsValid(board, myPosition, position_to_check, available_moves, true, false);


        _row = myPosition.getRow();
        _column = myPosition.getColumn();
        /**
         * Left-up directional check
         */
        _row++;
        _column -= 2;
        position_to_check = new ChessPosition(_row, _column);
        checkMoveIsValid(board, myPosition, position_to_check, available_moves, true, false);


        _row = myPosition.getRow();
        _column = myPosition.getColumn();
        /**
         * Down-right directional check
         */
        _row -= 2;
        _column++;
        position_to_check = new ChessPosition(_row, _column);
        checkMoveIsValid(board, myPosition, position_to_check, available_moves, true, false);


        _row = myPosition.getRow();
        _column = myPosition.getColumn();
        /**
         * Down-left directional check
         */
        _row -= 2;
        _column--;
        position_to_check = new ChessPosition(_row, _column);
        checkMoveIsValid(board, myPosition, position_to_check, available_moves, true, false);

        return available_moves;

    }

    private Collection<ChessMove> getPawnMoves(ChessBoard board, ChessPosition myPosition) {

        Collection<ChessMove> available_moves = new HashSet<>();

        var _row = myPosition.getRow();
        var _column = myPosition.getColumn();
        /**
         * Forward directional check (cannot capture enemy pieces)
         */
        if (this.getTeamColor() == ChessGame.TeamColor.WHITE) {
            _row++;
        }
        else {
            _row--;
        }
        var position_to_check = new ChessPosition(_row, _column);
        var can_move_one_space_forward = checkMoveIsValid(board, myPosition, position_to_check, available_moves, false, false);


        if (can_move_one_space_forward && ((this.getTeamColor() == ChessGame.TeamColor.WHITE && myPosition.getRow() == 2)
                || (this.getTeamColor() == ChessGame.TeamColor.BLACK && myPosition.getRow() == 7))) {
            _row = myPosition.getRow();
            _column = myPosition.getColumn();
            /**
             * Initial forward directional check (can't capture enemy pieces)
             */
            if (this.getTeamColor() == ChessGame.TeamColor.WHITE) {
                _row += 2;
            } else {
                _row -= 2;
            }
            position_to_check = new ChessPosition(_row, _column);
            checkMoveIsValid(board, myPosition, position_to_check, available_moves, false, false);
        }


        _row = myPosition.getRow();
        _column = myPosition.getColumn();
        /**
         * Forward-left directional check (can capture enemy pieces)
         */
        if (this.getTeamColor() == ChessGame.TeamColor.WHITE) {
            _row++;
            _column--;
        }
        else {
            _row--;
            _column++;
        }
        position_to_check = new ChessPosition(_row, _column);
        checkMoveIsValid(board, myPosition, position_to_check, available_moves, true, true);


        _row = myPosition.getRow();
        _column = myPosition.getColumn();
        /**
         * Forward-right directional check (can capture enemy pieces)
         */
        if (this.getTeamColor() == ChessGame.TeamColor.WHITE) {
            _row++;
            _column++;
        }
        else {
            _row--;
            _column--;
        }
        position_to_check = new ChessPosition(_row, _column);
        checkMoveIsValid(board, myPosition, position_to_check, available_moves, true, true);

        return available_moves;

    }

    private Collection<ChessMove> getQueenMoves(ChessBoard board, ChessPosition myPosition) {

        Collection<ChessMove> available_moves = new HashSet<>();
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

            
            var move_to_add = new ChessMove(myPosition, position_to_check, null);

            // break out of while loop if there is a piece at that position
            if (piece_at_position != null) {
                if (piece_at_position.getTeamColor() != this.getTeamColor()) {
                    available_moves.add(move_to_add);
                }
                break;
            }

            available_moves.add(move_to_add);

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

            
            var move_to_add = new ChessMove(myPosition, position_to_check, null);

            // break out of while loop if there is a piece at that position
            if (piece_at_position != null) {
                if (piece_at_position.getTeamColor() != this.getTeamColor()) {
                    available_moves.add(move_to_add);
                }
                break;
            }

            available_moves.add(move_to_add);

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

            
            var move_to_add = new ChessMove(myPosition, position_to_check, null);

            // break out of while loop if there is a piece at that position
            if (piece_at_position != null) {
                if (piece_at_position.getTeamColor() != this.getTeamColor()) {
                    available_moves.add(move_to_add);
                }
                break;
            }

            available_moves.add(move_to_add);

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

            
            var move_to_add = new ChessMove(myPosition, position_to_check, null);

            // break out of while loop if there is a piece at that position
            if (piece_at_position != null) {
                if (piece_at_position.getTeamColor() != this.getTeamColor()) {
                    available_moves.add(move_to_add);
                }
                break;
            }

            available_moves.add(move_to_add);

        }


        _row = myPosition.getRow();
        _column = myPosition.getColumn();
        /**
         * Right directional check
         */
        while (_column < 8) {
            _column++;
            var position_to_check = new ChessPosition(_row, _column);
            var piece_at_position = board.getPiece(position_to_check);

            
            var move_to_add = new ChessMove(myPosition, position_to_check, null);

            // break out of while loop if there is a piece at that position
            if (piece_at_position != null) {
                if (piece_at_position.getTeamColor() != this.getTeamColor()) {
                    available_moves.add(move_to_add);
                }
                break;
            }

            available_moves.add(move_to_add);

        }

        _row = myPosition.getRow();
        _column = myPosition.getColumn();
        /**
         * Left directional check
         */
        while (_column > 1) {
            _column--;
            var position_to_check = new ChessPosition(_row, _column);
            var piece_at_position = board.getPiece(position_to_check);


            var move_to_add = new ChessMove(myPosition, position_to_check, null);

            // break out of while loop if there is a piece at that position
            if (piece_at_position != null) {
                if (piece_at_position.getTeamColor() != this.getTeamColor()) {
                    available_moves.add(move_to_add);
                }
                break;
            }

            available_moves.add(move_to_add);

        }

        _row = myPosition.getRow();
        _column = myPosition.getColumn();
        /**
         * Up directional check
         */
        while (_row < 8) {
            _row++;
            var position_to_check = new ChessPosition(_row, _column);
            var piece_at_position = board.getPiece(position_to_check);


            var move_to_add = new ChessMove(myPosition, position_to_check, null);

            // break out of while loop if there is a piece at that position
            if (piece_at_position != null) {
                if (piece_at_position.getTeamColor() != this.getTeamColor()) {
                    available_moves.add(move_to_add);
                }
                break;
            }

            available_moves.add(move_to_add);

        }

        _row = myPosition.getRow();
        _column = myPosition.getColumn();
        /**
         * Down directional check
         */
        while (_row > 1) {
            _row--;
            var position_to_check = new ChessPosition(_row, _column);
            var piece_at_position = board.getPiece(position_to_check);


            var move_to_add = new ChessMove(myPosition, position_to_check, null);

            // break out of while loop if there is a piece at that position
            if (piece_at_position != null) {
                if (piece_at_position.getTeamColor() != this.getTeamColor()) {
                    available_moves.add(move_to_add);
                }
                break;
            }

            available_moves.add(move_to_add);

        }

        return available_moves;

    }

    private boolean checkMoveIsValid(ChessBoard board, ChessPosition myPosition, ChessPosition position_to_check, Collection<ChessMove> available_moves, boolean can_capture, boolean must_capture) {
        ChessMove move_to_add;
        ChessPiece piece_at_position;
        if (!position_to_check.outOfBounds()) {
            piece_at_position = board.getPiece(position_to_check);

            move_to_add = new ChessMove(myPosition, position_to_check, null);

            if (piece_at_position != null) {
                if (can_capture && piece_at_position.getTeamColor() != this.getTeamColor()) {
                    if (canPromote(this.pieceType, position_to_check)) {
                        for (PieceType piece_type : PieceType.values()) {
                            if (piece_type != PieceType.KING && piece_type != PieceType.PAWN) {
                                move_to_add = new ChessMove(myPosition, position_to_check, piece_type);
                                available_moves.add(move_to_add);
                            }
                        }
                        return true;
                    }
                    else {
                        available_moves.add(move_to_add);
                        return true;
                    }
                }
            } else if (!must_capture) {
                if (canPromote(this.pieceType, position_to_check)) {
                    for (PieceType piece_type : PieceType.values()) {
                        if (piece_type != PieceType.KING && piece_type != PieceType.PAWN) {
                            move_to_add = new ChessMove(myPosition, position_to_check, piece_type);
                            available_moves.add(move_to_add);
                        }
                    }
                    return true;
                }
                else {
                    available_moves.add(move_to_add);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean canPromote(PieceType piece_type, ChessPosition position_to_check) {
        return piece_type == PieceType.PAWN &&
                ((position_to_check.getRow() == 8 && this.getTeamColor() == ChessGame.TeamColor.WHITE)
                        || (position_to_check.getRow() == 1 && this.getTeamColor() == ChessGame.TeamColor.BLACK));
    }

}
