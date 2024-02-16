package server;

import com.google.gson.Gson;
import spark.*;
import user.User;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.post("/register", this::registerUser);

        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object registerUser(Request request, Response response) {
        var user = new Gson().fromJson(request.body(), User.class);
        return new Gson().toJson(user);
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }


}
