package server;

import model.AuthData;
import com.google.gson.Gson;
import dataAccess.DataAccess;
import exceptions.DataAccessException;
import responses.CreateGameResponse;
import responses.GetGamesResponse;
import services.GameService;
import exceptions.MissingDataException;
import exceptions.UnauthorizedException;
import services.RegistrationService;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import services.SessionService;
import spark.*;
import model.User;

public class Server {

    private DataAccess dataAccess = new DataAccess();
    private RegistrationService registrationService = new RegistrationService();
    private SessionService sessionService = new SessionService();
    private GameService gameService = new GameService();

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.delete("/db", this::clearDatabase);
        Spark.post("/user", this::registerUser);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::getGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);

        Spark.awaitInitialization();
        return Spark.port();
    }

    private String joinGame(Request request, Response response) {
        try {
            String authToken = request.headers("Authorization");
            var authData = new AuthData(null, authToken);
            var joinGameRequest = new Gson().fromJson(request.body(), JoinGameRequest.class);
            gameService.joinGame(dataAccess, authData, joinGameRequest);
            response.status(200);
            return "";
        } catch (UnauthorizedException exception) {
            response.status(401);
            return "{ \"message\": \"" + exception.getMessage() + "\" }";
        } catch (DataAccessException exception) {
            response.status(500);
            return "{ \"message\": \"" + exception.getMessage() + "\" }";
        }
    }

    private Object createGame(Request request, Response response) {
        try {
            String authToken = request.headers("Authorization");
            var authData = new AuthData(null, authToken);
            CreateGameRequest createGameRequest = new Gson().fromJson(request.body(), CreateGameRequest.class);
            var gameID = this.gameService.createGame(dataAccess, createGameRequest, authData);
            var responseObj = new CreateGameResponse(gameID);
            response.status(200);
            return new Gson().toJson(responseObj);
        } catch (MissingDataException exception) {
            response.status(400);
            return "{ \"message\": \"" + exception.getMessage() + "\" }";
        } catch (UnauthorizedException exception) {
            response.status(401);
            return "{ \"message\": \"" + exception.getMessage() + "\" }";
        } catch (DataAccessException exception) {
            response.status(500);
            return "{ \"message\": \"" + exception.getMessage() + "\" }";
        }
    }

    private String getGames(Request request, Response response) {
        try {
            String authToken = request.headers("Authorization");
            var authData = new AuthData(null, authToken);
            var gamesData = this.gameService.getGames(dataAccess, authData);
            var responseObj = new GetGamesResponse(gamesData);
            response.status(200);
            var responseJson = new Gson().toJson(responseObj);
            return responseJson;
        } catch (UnauthorizedException exception) {
            response.status(401);
            return "{ \"message\": \"" + exception.getMessage() + "\" }";
        } catch (DataAccessException exception) {
            response.status(500);
            return "{ \"message\": \"" + exception.getMessage() + "\" }";
        }
    }

    private String clearDatabase(Request request, Response response) {
        try {
            this.registrationService.clearDatabase(dataAccess);
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
            AuthData authData = this.registrationService.registerUser(dataAccess, user);
            return new Gson().toJson(authData);
        } catch (DataAccessException exception) {
            response.status(500);
            response.body(exception.getMessage());
            return response;
        } catch (MissingDataException exception) {
            response.status(400);
            response.body(exception.getMessage());
            return response;
        }
    }

    private Object login(Request request, Response response) {
        var user = new Gson().fromJson(request.body(), User.class);
        try {
            var authData = this.sessionService.loginUser(dataAccess, user);
            return new Gson().toJson(authData);
        } catch (UnauthorizedException exception) {
            response.status(401);
            return "{ \"message\": \"" + exception.getMessage() + "\" }";
        } catch (MissingDataException | DataAccessException exception) {
            response.status(500);
            return "{ \"message\": \"" + exception.getMessage() + "\" }";
        }
    }

    private String logout(Request request, Response response) {
        try {
            String authToken = request.headers("Authorization");
            var authData = new AuthData(null, authToken);
            sessionService.logoutUser(dataAccess, authData);
            response.status(200);
            return "";
        } catch (UnauthorizedException exception) {
            response.status(401);
            return "{ \"message\": \"" + exception.getMessage() + "\" }";
        } catch (DataAccessException exception) {
            response.status(500);
            return "{ \"message\": \"" + exception.getMessage() + "\" }";
        }
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }


}
