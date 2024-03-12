package repl;

import java.util.Objects;
import java.util.Scanner;

public class Repl {
    public void run() {
        var clientReader = new Scanner(System.in);
        var line = "";
        while (true) {
            line = clientReader.nextLine();
            if (Objects.equals(line, "q")) {
                return;
            }
        }
    }
}
