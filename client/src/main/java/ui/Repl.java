package ui;

import chess.ChessMove;
import chess.ChessPosition;
import exceptions.HttpResponseException;
import model.AuthData;
import model.GameData;
import model.User;
import org.springframework.security.core.parameters.P;
import serverFacade.ServerFacadeGame;
import serverFacade.ServerFacadeRegistration;
import serverFacade.ServerFacadeSession;
import serverFacade.ServerFacadeWebsocket;
import serverMessageObserver.ServerMessageObserver;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class Repl {

    enum userState {
        LOGGED_IN,
        LOGGED_OUT,
        IN_GAME
    }

    private ServerMessageObserver serverMessageObserver;
    private GameData gameDataLocal;
    private long gameID;
    private String serverURL = "://127.0.0.1:";
    private AuthData authorization;
    private boolean loggedIn = false;
    private ServerFacadeWebsocket facadeWebsocket;
    private ServerFacadeRegistration facadeRegistration;
    private ServerFacadeSession facadeSession;
    private ServerFacadeGame facadeGame;
    private String boardString = EscapeSequences.DEFAULT_BOARD_WHITE + "\n" + EscapeSequences.DEFAULT_BOARD_BLACK;
    private userState loggedInStatus = userState.LOGGED_OUT;

    public void run(int port) {
        initializeFacade(port);

        var clientReader = new Scanner(System.in);
        var line = "";
        String[] lineItems;
        String lineFirst;
        System.out.println(EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR);
        while (true) {
            printStatus();
            line = clientReader.nextLine();
            lineItems = line.split(" ");
            lineFirst = lineItems[0];

            if (Objects.equals(lineFirst, "q") || Objects.equals(lineFirst, "quit")) {
                return;
            } else if (loggedInStatus == userState.LOGGED_OUT) {
                switch (lineFirst) {
                    case "help" -> printHelpLoggedOut();
                    case "register" -> {
                        if (lineItems.length < 4) {
                            System.out.println("Missing username, password, or email");
                        } else {
                            registerUser(lineItems[1], lineItems[2], lineItems[3]);
                        }
                    }
                    case "login" -> {
                        if (lineItems.length < 3) {
                            System.out.println("Missing username or password");
                        } else {
                            loginUser(lineItems[1], lineItems[2]);
                        }
                    }
                    case null, default ->
                            System.out.println("Could not recognize command - try typing 'help' for a list of available commands.");
                }
            } else if (loggedInStatus == userState.LOGGED_IN) {
                switch (lineFirst) {
                    case "help" -> printHelpLoggedIn();
                    case "create" -> {
                        if (lineItems.length < 2) {
                            System.out.println("Missing game name");
                        } else {
                            createGame(lineItems[1]);
                        }
                    }
                    case "list" -> getGames();
                    case "join" -> {
                        if (lineItems.length < 2) {
                            System.out.println("Missing game ID number");
                        } else if (lineItems.length < 3) {
                            joinGame(port, null, lineItems[1]);
                        } else {
                            joinGame(port, lineItems[2], lineItems[1]);
                        }
                    }
                    case "observe" -> {
                        if (lineItems.length < 2) {
                            System.out.println("Missing game ID number");
                        } else {
                            joinGame(port, null, lineItems[1]);
                        }
                    }
                    case "logout" -> logoutUser();
                    case null, default ->
                            System.out.println("Could not recognize command - try typing 'help' for a list of available commands.");
                }
            } else if (loggedInStatus == userState.IN_GAME) {
                switch (lineFirst) {
                    case "help":
                        printHelpInGame();
                        break;
                    case "leave":
                        leaveGame();
                        break;
                    case "move":
                        if (lineItems.length < 2) {
                            System.out.println("Missing start and end positions");
                        } else if (lineItems.length < 3) {
                            System.out.println("Missing end position");
                        } else {
                            this.makeMove(lineItems[1], lineItems[2]);
                        }
                        break;
                    case "resign":
                        resign();
                        break;
                    case null, default:
                        System.out.println("Could not recognize command - try typing 'help' for a list of available commands.");
                }
            }
        }
    }

    private void initializeFacade(int port) {
        var http = "http";
        facadeRegistration = new ServerFacadeRegistration(http + serverURL + port);
        facadeSession = new ServerFacadeSession(http + serverURL + port);
        facadeGame = new ServerFacadeGame(http + serverURL + port);
        serverMessageObserver = new ServerMessageObserver(this);
    }

    private void initializeWebsocket(int port) {
        try {
            var websocket = "ws";
            facadeWebsocket = new ServerFacadeWebsocket(websocket + serverURL + port + "/connect", serverMessageObserver);
        } catch (Exception exception) {
            System.out.println("Error: Could not connect to server because " + exception.getMessage());
        }
    }

    private void sendWebsocketMessage(String message) {
        try {
            facadeWebsocket.send(message);
        } catch (Exception exception) {
            System.out.println("Error: could not send message to server because " + exception.getMessage());
        }
    }

    private void printStatus() {
        String cliString = "[" + loggedInStatus.toString() + "] >>> ";
        System.out.print(cliString);
    }

    private void printHelpLoggedOut() {
        System.out.println("register <USERNAME> <PASSWORD> <EMAIL> - to create an account");
        System.out.println("login <USERNAME> <PASSWORD> - to log in with an existing account and play");
        System.out.println("quit - exit the chess CLI");
        System.out.println("help - list possible commands (you just called this one)");
    }

    private void printHelpLoggedIn() {
        System.out.println("create <NAME> - create a game");
        System.out.println("list - see all game info");
        System.out.println("join <ID> [WHITE|BLACK|<empty>] - join a game (empty joins as observer)");
        System.out.println("observe <ID> - observe a game");
        System.out.println("logout - when you are finished playing");
        System.out.println("quit - exit the chess CLI");
        System.out.println("help - list possible commands (you just called this one)");
    }

    private void printHelpInGame() {
        System.out.println("redraw - draw the chess board again");
        System.out.println("leave - leave the game (stay logged in)");
        System.out.println("move <start position> <end position> - make a move. Type positions as lowercase followed by #");
        System.out.println("resign - ends the game; you will still have to leave the game");
        System.out.println("show <position> - shows valid moves for a specific piece");
        System.out.println("quit - exit the chess CLI");
        System.out.println("help - list possible commands (you just called this one)");
    }

    private void registerUser(String username, String password, String email) {
        try {
            var authData = facadeRegistration.register(new User(username, password, email));
            authorization = authData;
            loggedInStatus = userState.LOGGED_IN;
            System.out.println("User " + username + " logged in. Don't forget your password!");
        } catch (HttpResponseException exception) {
            System.out.println("Username already taken \nTry with a different name");
        }
    }

    private void loginUser(String username, String password) {
        try {
            authorization = facadeSession.login(new User(username, password, null));
            loggedInStatus = userState.LOGGED_IN;
            System.out.println("User " + username + " logged in. Type help to see available commands");
        } catch (HttpResponseException exception) {
            System.out.println("Username or password didn't match our records... \nTry the register keyword to make a new account");
        }
    }

    private void logoutUser() {
        try {
            facadeSession.logout(authorization);
            authorization = null;
            loggedInStatus = userState.LOGGED_OUT;
            System.out.println("User logged out");
        } catch (HttpResponseException exception) {
            System.out.println("Could not logout. Token is invalid.");
        }
    }

    private void createGame(String gameName) {
        try {
            facadeGame.createGame(authorization, gameName);
            System.out.println("Created game named " + gameName);
        } catch (HttpResponseException exception) {
            System.out.println("Game could not be created. Try again later.");
        }
    }

    private void getGames() {
        try {
            ArrayList<String> gameDataStrings = new ArrayList<>();
            gameDataStrings.add("Name:            ID:          White:          Black:           Watchers:");
            var games = facadeGame.getGames(authorization);
            for (GameData game : games) {
                var white = game.whiteUsername();
                if (white == null || white.isEmpty()) {
                    white = "none";
                }
                var black = game.blackUsername();
                if (black == null || black.isEmpty()) {
                    black = "none";
                }
                gameDataStrings.add(game.gameName() + " " + game.gameID() + " " + white + " "
                        + black + " " + game.watchers());
            }
            for (String gameDataString : gameDataStrings) {
                System.out.println(gameDataString);
            }
        } catch (HttpResponseException exception) {
            System.out.println("Could not list games because of a server error. Try again later.");
        }
    }

    private void joinGame(int port, String playerColor, String gameID) {
        try {
            long gameIDLong = Long.parseLong(gameID);
            this.gameID = gameIDLong;
            facadeGame.joinGame(authorization, playerColor, gameIDLong);

            initializeWebsocket(port);

            try {
                facadeWebsocket.joinPlayer(authorization, gameIDLong, playerColor);
            } catch (Exception exception) {
                System.out.println("Error: could not connect to server because " + exception.getMessage());
            }

            if (Objects.equals(playerColor, "WHITE")) {
                System.out.println("Joined game " + gameID + " as white");
            } else if (Objects.equals(playerColor, "BLACK")) {
                System.out.println("Joined game " + gameID + " as black");
            } else {
                System.out.println("Joined game " + gameID + " as observer");
            }

            loggedInStatus = userState.IN_GAME;
        } catch (HttpResponseException exception) {
            System.out.println("Spot taken. Try joining with a different color.");
        } catch (NumberFormatException exception) {
            System.out.println("Please provide the game ID number, not its name");
        }
    }

    private void makeMove(String start, String end) {
        var startSplit = start.split("");
        var endSplit = end.split("");
        var startColumn = this.getNumberFromLetter(startSplit[0]);
        var endColumn = this.getNumberFromLetter(endSplit[0]);
        var startPos = new ChessPosition(Integer.parseInt(startSplit[1]), startColumn);
        var endPos = new ChessPosition(Integer.parseInt(endSplit[1]), endColumn);
        try {
            this.facadeWebsocket.makeMove(this.authorization, this.gameID, new ChessMove(startPos, endPos, null));
        } catch (Exception exception) {
            System.out.println("Could not make move because " + exception.getMessage());
        }
    }

    private void leaveGame() {
        loggedInStatus = userState.LOGGED_IN;
        try {
            facadeWebsocket.leavePlayer(authorization, gameID);
            this.gameID = 0;
            System.out.println("Left the game");
        } catch (Exception exception) {
            System.out.println("Error: Could not communicate with the server");
        }
    }

    private void resign() {
        try {
            facadeWebsocket.resign(authorization, gameID);
        } catch (Exception exception) {
            System.out.println("Error: Could not communicate with the server");
        }
    }

    public void loadGame(GameData game) {
        this.gameDataLocal = game;
        printBoard(game);
        printStatus();
    }

    private void printBoard(GameData game) {
        System.out.println("\n" + boardString);
    }

    public void printMessage(String message) {
        System.out.println("\n" + message);
        printStatus();
    }

    private int getNumberFromLetter(String letter) {
        switch (letter) {
            case "a":
                return 1;
            case "b":
                return 2;
            case "c":
                return 3;
            case "d":
                return 4;
            case "e":
                return 5;
            case "f":
                return 6;
            case "g":
                return 7;
            case "h":
                return 8;
            default:
                return 0;
        }
    }
}
