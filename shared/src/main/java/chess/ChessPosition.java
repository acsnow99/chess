package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {
    /**
     * Position 0,0 is outside the game board. Used indices go from 1-8
     */
    private int row = 0;
    private int column = 0;
    public ChessPosition(int row, int col) {

        this.row = row;
        this.column = col;

    }

    /**
     * Generated via wizard
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPosition that = (ChessPosition) o;
        return row == that.row && column == that.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {

        return this.row;

    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {

        return this.column;

    }

    public void setRow(int row) {

        this.row = row;

    }

    public void setColumn(int column) {

        this.column = column;

    }

    public void setPosition(int row, int column) {

        this.setRow(row);
        this.setColumn(column);

    }

}
