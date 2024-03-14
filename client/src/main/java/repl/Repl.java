package repl;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class Repl {
    private String loggedInStatusString = "LOGGED OUT";
    private boolean loggedIn = false;
    private String cliString;

    public void run() {
        var clientReader = new Scanner(System.in);
        var line = "";
        String[] lineItems;
        String lineFirst;
        while (true) {
            if (loggedIn) {
                loggedInStatusString = "LOGGED IN";
            } else {
                loggedInStatusString = "LOGGED OUT";
            }
            cliString = "[" + loggedInStatusString + "] >>> ";
            System.out.print(cliString);
            line = clientReader.nextLine();
            lineItems = line.split(" ");
            lineFirst = lineItems[0];

            if (Objects.equals(lineFirst, "q") || Objects.equals(lineFirst, "quit")) {
                return;
            } else if (!loggedIn) {
                if (Objects.equals(lineFirst, "help")) {
                    System.out.println("register <USERNAME> <PASSWORD> <EMAIL> - to create an account");
                    System.out.println("login <USERNAME> <PASSWORD> - to log in with an existing account and play");
                    System.out.println("quit - exit the chess CLI");
                    System.out.println("help - list possible commands (you just called this one)");
                } else if (Objects.equals(lineFirst, "register")) {
                    if (lineItems.length < 4) {
                        System.out.println("Missing username, password, or email");
                    } else {
                        // TO-DO: actually register and login the user :)
                        System.out.println("User " + lineItems[1] + " logged in. Don't forget your password!");
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
                    System.out.println("create <NAME> - create a game");
                    System.out.println("list - see all game info");
                    System.out.println("join <ID> [WHITE|BLACK|<empty>] - join a game (empty joins as observer)");
                    System.out.println("observe <ID> - observe a game");
                    System.out.println("logout - when you are finished playing");
                    System.out.println("quit - exit the chess CLI");
                    System.out.println("help - list possible commands (you just called this one)");
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
}
