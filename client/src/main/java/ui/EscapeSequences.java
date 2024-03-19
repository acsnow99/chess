package ui;

/**
 * This class contains constants and functions relating to ANSI Escape Sequences that are useful in the Client display
 */
public class EscapeSequences {

    private static final String UNICODE_ESCAPE = "\u001b";
    private static final String ANSI_ESCAPE = "\033";

    public static final String ERASE_SCREEN = UNICODE_ESCAPE + "[H" + UNICODE_ESCAPE + "[2J";
    public static final String ERASE_LINE = UNICODE_ESCAPE + "[2K";

    public static final String SET_TEXT_BOLD = UNICODE_ESCAPE + "[1m";
    public static final String SET_TEXT_FAINT = UNICODE_ESCAPE + "[2m";
    public static final String RESET_TEXT_BOLD_FAINT = UNICODE_ESCAPE + "[22m";
    public static final String SET_TEXT_ITALIC = UNICODE_ESCAPE + "[3m";
    public static final String RESET_TEXT_ITALIC = UNICODE_ESCAPE + "[23m";
    public static final String SET_TEXT_UNDERLINE = UNICODE_ESCAPE + "[4m";
    public static final String RESET_TEXT_UNDERLINE = UNICODE_ESCAPE + "[24m";
    public static final String SET_TEXT_BLINKING = UNICODE_ESCAPE + "[5m";
    public static final String RESET_TEXT_BLINKING = UNICODE_ESCAPE + "[25m";

    private static final String SET_TEXT_COLOR = UNICODE_ESCAPE + "[38;5;";
    private static final String SET_BG_COLOR = UNICODE_ESCAPE + "[48;5;";

    public static final String SET_TEXT_COLOR_BLACK = SET_TEXT_COLOR + "0m";
    public static final String SET_TEXT_COLOR_LIGHT_GREY = SET_TEXT_COLOR + "242m";
    public static final String SET_TEXT_COLOR_DARK_GREY = SET_TEXT_COLOR + "235m";
    public static final String SET_TEXT_COLOR_RED = SET_TEXT_COLOR + "160m";
    public static final String SET_TEXT_COLOR_GREEN = SET_TEXT_COLOR + "46m";
    public static final String SET_TEXT_COLOR_YELLOW = SET_TEXT_COLOR + "226m";
    public static final String SET_TEXT_COLOR_BLUE = SET_TEXT_COLOR + "12m";
    public static final String SET_TEXT_COLOR_MAGENTA = SET_TEXT_COLOR + "5m";
    public static final String SET_TEXT_COLOR_WHITE = SET_TEXT_COLOR + "15m";
    public static final String RESET_TEXT_COLOR = SET_TEXT_COLOR + "46m";

    public static final String SET_BG_COLOR_BLACK = SET_BG_COLOR + "0m";
    public static final String SET_BG_COLOR_LIGHT_GREY = SET_BG_COLOR + "242m";
    public static final String SET_BG_COLOR_DARK_GREY = SET_BG_COLOR + "235m";
    public static final String SET_BG_COLOR_RED = SET_BG_COLOR + "160m";
    public static final String SET_BG_COLOR_GREEN = SET_BG_COLOR + "46m";
    public static final String SET_BG_COLOR_DARK_GREEN = SET_BG_COLOR + "22m";
    public static final String SET_BG_COLOR_YELLOW = SET_BG_COLOR + "226m";
    public static final String SET_BG_COLOR_BLUE = SET_BG_COLOR + "12m";
    public static final String SET_BG_COLOR_MAGENTA = SET_BG_COLOR + "5m";
    public static final String SET_BG_COLOR_WHITE = SET_BG_COLOR + "15m";
    public static final String RESET_BG_COLOR = SET_BG_COLOR + "0m";

    public static final String WHITE_KING = EscapeSequences.SET_TEXT_COLOR_WHITE + " K ";
    public static final String WHITE_QUEEN = EscapeSequences.SET_TEXT_COLOR_WHITE + " Q ";
    public static final String WHITE_BISHOP = EscapeSequences.SET_TEXT_COLOR_WHITE + " B ";
    public static final String WHITE_KNIGHT = EscapeSequences.SET_TEXT_COLOR_WHITE + " N ";
    public static final String WHITE_ROOK = EscapeSequences.SET_TEXT_COLOR_WHITE + " R ";
    public static final String WHITE_PAWN = EscapeSequences.SET_TEXT_COLOR_WHITE + " P ";
    public static final String BLACK_KING = EscapeSequences.SET_TEXT_COLOR_BLACK + " K ";
    public static final String BLACK_QUEEN = EscapeSequences.SET_TEXT_COLOR_BLACK + " Q ";
    public static final String BLACK_BISHOP = EscapeSequences.SET_TEXT_COLOR_BLACK + " B ";
    public static final String BLACK_KNIGHT = EscapeSequences.SET_TEXT_COLOR_BLACK + " N ";
    public static final String BLACK_ROOK = EscapeSequences.SET_TEXT_COLOR_BLACK + " R ";
    public static final String BLACK_PAWN = EscapeSequences.SET_TEXT_COLOR_BLACK + " P ";
    public static final String EMPTY = EscapeSequences.SET_TEXT_COLOR_DARK_GREY + "   " + EscapeSequences.RESET_TEXT_COLOR;

    public static final String DEFAULT_BOARD_WHITE = EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR
            + "    A  B  C  D  E  F  G  H  \n" +

            //TODO: Refactor the below chunk of code into a function that takes a row number and a list of pieces
            " 8 " + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.BLACK_ROOK
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.BLACK_KNIGHT
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.BLACK_BISHOP
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.BLACK_KING
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.BLACK_QUEEN
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.BLACK_BISHOP
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.BLACK_KNIGHT
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.BLACK_ROOK +
            EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR + " 8" + "\n" +

            " 7 " + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.BLACK_PAWN
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.BLACK_PAWN
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.BLACK_PAWN
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.BLACK_PAWN
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.BLACK_PAWN
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.BLACK_PAWN
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.BLACK_PAWN
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.BLACK_PAWN +
            EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR + " 7" + "\n" +

            " 6 " + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.EMPTY +
            EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR + " 6" + "\n" +

            " 5 " + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.EMPTY +
            EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR + " 5" + "\n" +

            " 4 " + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.EMPTY +
            EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR + " 4" + "\n" +

            " 3 " + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.EMPTY +
            EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR + " 3" + "\n" +

            " 2 " + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.WHITE_PAWN
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.WHITE_PAWN
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.WHITE_PAWN
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.WHITE_PAWN
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.WHITE_PAWN
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.WHITE_PAWN
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.WHITE_PAWN
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.WHITE_PAWN +
            EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR + " 2" + "\n" +

            " 1 " + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.WHITE_ROOK
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.WHITE_KNIGHT
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.WHITE_BISHOP
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.WHITE_QUEEN
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.WHITE_KING
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.WHITE_BISHOP
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.WHITE_KNIGHT
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.WHITE_ROOK +
            EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR + " 1" + "\n" +

            EscapeSequences.RESET_BG_COLOR + "    A  B  C  D  E  F  G  H  \n";

    public static final String DEFAULT_BOARD_BLACK = EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR
            + "    H  G  F  E  D  C  B  A  \n" +

            //TODO: Refactor the below chunk of code into a function that takes a row number and a list of pieces
            " 8 " + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.BLACK_ROOK
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.BLACK_KNIGHT
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.BLACK_BISHOP
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.BLACK_KING
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.BLACK_QUEEN
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.BLACK_BISHOP
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.BLACK_KNIGHT
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.BLACK_ROOK +
            EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR + " 8" + "\n" +

            " 7 " + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.BLACK_PAWN
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.BLACK_PAWN
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.BLACK_PAWN
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.BLACK_PAWN
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.BLACK_PAWN
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.BLACK_PAWN
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.BLACK_PAWN
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.BLACK_PAWN +
            EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR + " 7" + "\n" +

            " 6 " + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.EMPTY +
            EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR + " 6" + "\n" +

            " 5 " + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.EMPTY +
            EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR + " 5" + "\n" +

            " 4 " + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.EMPTY +
            EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR + " 4" + "\n" +

            " 3 " + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.EMPTY
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.EMPTY +
            EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR + " 3" + "\n" +

            " 2 " + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.WHITE_PAWN
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.WHITE_PAWN
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.WHITE_PAWN
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.WHITE_PAWN
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.WHITE_PAWN
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.WHITE_PAWN
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.WHITE_PAWN
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.WHITE_PAWN +
            EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR + " 2" + "\n" +

            " 1 " + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.WHITE_ROOK
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.WHITE_KNIGHT
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.WHITE_BISHOP
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.WHITE_QUEEN
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.WHITE_KING
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.WHITE_BISHOP
            + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.WHITE_KNIGHT
            + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.WHITE_ROOK +
            EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR + " 1" + "\n" +

            EscapeSequences.RESET_BG_COLOR + "    H  G  F  E  D  C  B  A  \n";

    public static String moveCursorToLocation(int x, int y) {
        return UNICODE_ESCAPE + "[" + y + ";" + x + "H";
    }
}
