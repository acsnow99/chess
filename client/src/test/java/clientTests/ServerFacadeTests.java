package clientTests;

import model.User;
import org.junit.jupiter.api.*;
import serverFacade.ServerFacade;

import server.Server;
import serverFacade.*;


public class ServerFacadeTests {

    private static Server server;
    private static int port;

    @BeforeAll
    public static void init() {
        server = new Server();
        port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearDBBeforeTest() {
        try {
            var facade = new ServerFacadeRegistration("http://127.0.0.1:" + port);
            facade.clearDatabase();
        } catch (Exception e) {
        }
    }


    @Test
    @DisplayName("Clear database with no errors")
    public void clearDB() {
        try {
            var facade = new ServerFacadeRegistration("http://127.0.0.1:" + port);
            Assertions.assertDoesNotThrow(facade::clearDatabase);
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("Register user with no errors")
    public void registerUser() {
        try {
            var facade = new ServerFacadeRegistration("http://127.0.0.1:" + port);
            var user = new User("user", "pass", "mail@mail.mail");
            Assertions.assertDoesNotThrow(() -> facade.register(user));
        } catch (Exception e) {
            Assertions.fail();
        }
    }
}
