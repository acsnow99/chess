package server;

import com.google.gson.Gson;
import registrationService.RegistrationService;
import spark.*;
import user.User;

public class Server {

    private RegistrationService registrationService = new RegistrationService();

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.delete("/db", this::clearDatabase);
        Spark.post("/user", this::registerUser);

        Spark.awaitInitialization();
        return Spark.port();
    }


    private Object clearDatabase(Request request, Response response) {
        this.registrationService.clearDatabase();
        response.status(200);
        return new Gson().toJson(response.body());
    }

    private Object registerUser(Request request, Response response) {
        var user = new Gson().fromJson(request.body(), User.class);

        this.registrationService.registerUser(user);

        return new Gson().toJson(user);
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }


}
