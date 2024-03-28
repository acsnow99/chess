package ui;

import exceptions.HttpResponseException;
import model.AuthData;
import model.GameData;
import model.User;
import org.springframework.security.core.parameters.P;
import serverFacade.ServerFacadeGame;
import serverFacade.ServerFacadeRegistration;
import serverFacade.ServerFacadeSession;
import serverFacade.ServerFacadeWebsocket;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class Repl {
    private String serverURL = "://127.0.0.1:";
    private AuthData authorization;
    private boolean loggedIn = false;
    private ServerFacadeWebsocket facadeWebsocket;
    private ServerFacadeRegistration facadeRegistration;
    private ServerFacadeSession facadeSession;
    private ServerFacadeGame facadeGame;
    private String boardString = EscapeSequences.DEFAULT_BOARD_WHITE + "\n" + EscapeSequences.DEFAULT_BOARD_BLACK;

    public void run(int port) {
        initializeFacade(port);

        var clientReader = new Scanner(System.in);
        var line = "";
        String[] lineItems;
        String lineFirst;
        System.out.println(EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR);
        while (true) {
            String loggedInStatusString = "LOGGED OUT";
            if (loggedIn) {
                loggedInStatusString = "LOGGED IN";
            } else {
                loggedInStatusString = "LOGGED OUT";
            }
            String cliString = "[" + loggedInStatusString + "] >>> ";
            System.out.print(cliString);
            line = clientReader.nextLine();
            lineItems = line.split(" ");
            lineFirst = lineItems[0];

            if (Objects.equals(lineFirst, "q") || Objects.equals(lineFirst, "quit")) {
                return;
            } else if (!loggedIn) {
                if (Objects.equals(lineFirst, "help")) {
                    printHelpLoggedOut();
                } else if (Objects.equals(lineFirst, "register")) {
                    if (lineItems.length < 4) {
                        System.out.println("Missing username, password, or email");
                    } else {
                        registerUser(lineItems[1], lineItems[2], lineItems[3]);
                    }
                } else if (Objects.equals(lineFirst, "login")) {
                    if (lineItems.length < 3) {
                        System.out.println("Missing username or password");
                    } else {
                        loginUser(lineItems[1], lineItems[2]);
                    }
                } else {
                    System.out.println("Could not recognize command - try typing 'help' for a list of available commands.");
                }
            } else {
                if (Objects.equals(lineFirst, "help")) {
                    printHelpLoggedIn();
                } else if (Objects.equals(lineFirst, "create")) {
                    if (lineItems.length < 2) {
                        System.out.println("Missing game name");
                    } else {
                        createGame(lineItems[1]);
                    }
                } else if (Objects.equals(lineFirst, "list")) {
                    getGames();
                } else if (Objects.equals(lineFirst, "join")) {
                    if (lineItems.length < 2) {
                        System.out.println("Missing game ID number");
                    } else if (lineItems.length < 3) {
                        joinGame(null, lineItems[1]);
                    } else {
                        initializeWebsocket(port);
                        joinGame(lineItems[2], lineItems[1]);
                    }
                } else if (Objects.equals(lineFirst, "observe")) {
                    if (lineItems.length < 2) {
                        System.out.println("Missing game ID number");
                    } else {
                        joinGame(null, lineItems[1]);
                    }
                } else if (Objects.equals(lineFirst, "logout")) {
                    logoutUser();
                } else {
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
    }

    private void initializeWebsocket(int port) {
        try {
            var websocket = "ws";
            facadeWebsocket = new ServerFacadeWebsocket(websocket + serverURL + port);
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

    private void registerUser(String username, String password, String email) {
        try {
            var authData = facadeRegistration.register(new User(username, password, email));
            authorization = authData;
            loggedIn = true;
            System.out.println("User " + username + " logged in. Don't forget your password!");
        } catch (HttpResponseException exception) {
            System.out.println("Username already taken \nTry with a different name");
        }
    }

    private void loginUser(String username, String password) {
        try {
            var authData = facadeSession.login(new User(username, password, null));
            authorization = authData;
            loggedIn = true;
            System.out.println("User " + username + " logged in. Type help to see available commands");
        } catch (HttpResponseException exception) {
            System.out.println("Username or password didn't match our records... \nTry the register keyword to make a new account");
        }
    }

    private void logoutUser() {
        try {
            facadeSession.logout(authorization);
            authorization = null;
            loggedIn = false;
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
                // TODO: replace nulls with "none"
                gameDataStrings.add(game.gameName() + " " + game.gameID() + " " + game.whiteUsername() + " "
                        + game.blackUsername() + " " + game.watchers());
            }
            for (String gameDataString : gameDataStrings) {
                System.out.println(gameDataString);
            }
        } catch (HttpResponseException exception) {
            System.out.println("Could not list games because of a server error. Try again later.");
        }
    }

    private void joinGame(String playerColor, String gameID) {
        try {
            var gameIDLong = Long.parseLong(gameID);
            facadeGame.joinGame(authorization, playerColor, gameIDLong);

            try {
                facadeWebsocket.joinPlayer(authorization, gameIDLong, playerColor);
            } catch (Exception exception) {
                System.out.println("Error: could not access server because " + exception.getMessage());
            }

            if (Objects.equals(playerColor, "WHITE")) {
                System.out.println("Joined game " + gameID + " as white");
            } else if (Objects.equals(playerColor, "BLACK")) {
                System.out.println("Joined game " + gameID + " as black");
            } else {
                System.out.println("Joined game " + gameID + " as observer");
            }
            System.out.println(boardString);
        } catch (HttpResponseException exception) {
            System.out.println("Spot taken. Try joining with a different color.");
        }
    }
}
