package server;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import registrationService.RegistrationService;
import sessionService.SessionService;
import spark.*;
import user.User;

public class Server {

    private RegistrationService registrationService = new RegistrationService();
    private SessionService sessionService = new SessionService();

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.delete("/db", this::clearDatabase);
        Spark.post("/user", this::registerUser);
        Spark.post("/session", this::login);

        Spark.awaitInitialization();
        return Spark.port();
    }


    private Object clearDatabase(Request request, Response response) {
        try {
            this.registrationService.clearDatabase();
            response.status(200);
        } catch (DataAccessException exception) {
            response.status(500);
            response.body(exception.getMessage());
        }
        return new Gson().toJson(response.body());
    }

    private Object registerUser(Request request, Response response) {
        var user = new Gson().fromJson(request.body(), User.class);
        try {
            this.registrationService.registerUser(user);
        } catch (DataAccessException exception) {
            return new User(null, null, null);
        }
        return new Gson().toJson(user);
    }

    private Object login(Request request, Response response) {
        var user = new Gson().fromJson(request.body(), User.class);
        var authData = this.sessionService.login(user);
        return new Gson().toJson(authData);
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }


}
