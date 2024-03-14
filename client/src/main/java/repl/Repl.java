package repl;

import java.util.Objects;
import java.util.Scanner;

public class Repl {
    private String loggedInStatusString = "LOGGED OUT";
    private String cliString;

    public void run() {
        var clientReader = new Scanner(System.in);
        var line = "";
        while (true) {
            cliString = "[" + loggedInStatusString + "] >>> ";
            System.out.print(cliString);
            line = clientReader.nextLine();
            if (Objects.equals(line, "q") || Objects.equals(line, "quit")) {
                return;
            } else if (Objects.equals(line, "help")) {
                System.out.println("register <USERNAME> <PASSWORD> <EMAIL> - to create an account");
                System.out.println("login <USERNAME> <PASSWORD> - to log in with an existing account and play");
                System.out.println("quit - exit the chess CLI");
                System.out.println("help - list possible commands (you just called this one)");
            } else {
                System.out.println("Could not recognize command - try typing 'help' for a list of available commands.");
            }
        }
    }
}
