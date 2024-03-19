package ui;

import exceptions.HttpResponseException;
import model.User;
import serverFacade.ServerFacadeGame;
import serverFacade.ServerFacadeRegistration;
import serverFacade.ServerFacadeSession;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class Repl {
    private String serverURL = "http://127.0.0.1:";
    private boolean loggedIn = false;
    private ServerFacadeRegistration facadeRegistration;
    private ServerFacadeSession facadeSession;
    private ServerFacadeGame facadeGame;

    public void run(int port) {
        initializeFacade(port);

        var clientReader = new Scanner(System.in);
        var line = "";
        String[] lineItems;
        String lineFirst;
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
                        // TO-DO: actually login the user :)
                        loggedIn = true;
                        System.out.println("User " + lineItems[1] + " logged in. Type help to see available commands");
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
                        // TO-DO: actually create game :)
                        var gameName = lineItems[1];

                        System.out.println("Created game named " + gameName);
                    }
                } else if (Objects.equals(lineFirst, "list")) {
                    ArrayList<String> gameDataStrings = new ArrayList<>();
                    gameDataStrings.add("Name:            ID:          White:          Black:           Watchers:");
                    // TO-DO: get the gamedata strings from the server
                    for (String gameDataString : gameDataStrings) {
                        System.out.println(gameDataString);
                    }
                } else if (Objects.equals(lineFirst, "join")) {
                    var whiteTaken = false;
                    var blackTaken = false;
                    String inputColor;
                    if (lineItems.length < 2) {
                        System.out.println("Missing game ID number");
                    } else if (lineItems.length < 3) {
                        // TO-DO: actually join the user as observer
                        System.out.println("Joined game " + lineItems[1] + " as observer");
                    } else {
                        // TO-DO: actually join the user as player
                        inputColor = lineItems[2];
                        System.out.println("Joined game " + lineItems[1] + " as " + inputColor);
                    }
                } else if (Objects.equals(lineFirst, "observe")) {
                    if (lineItems.length < 2) {
                        System.out.println("Missing game ID number");
                    } else {
                        // TO-DO: actually join the user as observer
                        System.out.println("Joined game " + lineItems[1] + " as observer");
                    }
                } else if (Objects.equals(lineFirst, "logout")) {
                    // TO-DO: actually logout user :)
                    loggedIn = false;
                    System.out.println("User logged out");
                } else {
                    System.out.println("Could not recognize command - try typing 'help' for a list of available commands.");
                }
            }
        }
    }

    private void initializeFacade(int port) {
        facadeRegistration = new ServerFacadeRegistration(serverURL + port);
        facadeSession = new ServerFacadeSession(serverURL + port);
        facadeGame = new ServerFacadeGame(serverURL + port);
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
            facadeRegistration.register(new User(username, password, email));
            loggedIn = true;
            System.out.println("User " + username + " logged in. Don't forget your password!");
        } catch (HttpResponseException exception) {
            System.out.println("Could not register user because of a server error... try again later");
        }
    }
}
