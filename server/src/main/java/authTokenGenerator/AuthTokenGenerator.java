package authTokenGenerator;

import java.util.Random;

public class AuthTokenGenerator {

    private Random random = new Random();

    public String generateToken() {
        String token = "abcdefg" + random.nextInt(100000, 999999);
        return token;
    }

}
