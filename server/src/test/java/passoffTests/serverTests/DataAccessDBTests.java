package passoffTests.serverTests;

import dataAccess.DataAccessDB;
import model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class DataAccessDBTests {

    private DataAccessDB dataAccessDB = new DataAccessDB();

    @BeforeAll
    public static void init() {

    }

    @Test
    public void getUser() {
        try {
            var testUser = new User("Testito", "pass", "mail@mail.org");
            Assertions.assertEquals(testUser, dataAccessDB.getUser(new User("Testito", "pass", "mail@mail.org")));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
