package repl;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class Repl {
    private String loggedInStatusString = "LOGGED OUT";
    private String cliString;

    public void run() {
        var clientReader = new Scanner(System.in);
        var line = "";
        String[] lineItems;
        String lineFirst;
        while (true) {
            cliString = "[" + loggedInStatusString + "] >>> ";
            System.out.print(cliString);
            line = clientReader.nextLine();
            lineItems = line.split(" ");
            lineFirst = lineItems[0];

            if (Objects.equals(lineFirst, "q") || Objects.equals(lineFirst, "quit")) {
                return;
            } else if (Objects.equals(lineFirst, "help")) {
                System.out.println("register <USERNAME> <PASSWORD> <EMAIL> - to create an account");
                System.out.println("login <USERNAME> <PASSWORD> - to log in with an existing account and play");
                System.out.println("quit - exit the chess CLI");
                System.out.println("help - list possible commands (you just called this one)");
            } else if (Objects.equals(lineFirst, "register")) {
                if (lineItems.length < 4) {
                    System.out.println("Missing username, password, or email");
                } else {
                    // TO-DO: actually register the user :)
                    System.out.println("User " + lineItems[1] + " registered. Don't forget your password!");
                }
            } else if (Objects.equals(lineFirst, "login")) {
                if (lineItems.length < 3) {
                    System.out.println("Missing username or password");
                } else {
                    // TO-DO: actually login the user :)
                    System.out.println("User " + lineItems[1] + " logged in. Type help to see available commands");
                }
            } else {
                System.out.println("Could not recognize command - try typing 'help' for a list of available commands.");
            }
        }
    }
}
