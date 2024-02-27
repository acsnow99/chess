package generators;

import java.util.Random;

public class GameIDGenerator {
    private Random random = new Random();

    public long generateGameID() {
        return random.nextLong(10000000, 99999999);
    }

}
