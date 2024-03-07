package server;

import dataAccess.DataAccessDB;
import dataAccess.DataAccessMemory;
import exceptions.*;
import model.AuthData;
import com.google.gson.Gson;
import dataAccess.DataAccess;
import responses.CreateGameResponse;
import responses.ExceptionResponse;
import responses.GetGamesResponse;
import services.GameService;
import services.RegistrationService;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import services.SessionService;
import spark.*;
import model.User;

public class Server {

    private DataAccess dataAccess = new DataAccessDB();
    private RegistrationService registrationService = new RegistrationService();
    private SessionService sessionService = new SessionService();
    private GameService gameService = new GameService();

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        try {
            dataAccess.databaseInit();
        } catch (DataAccessException exception) {
            throw new RuntimeException("Error: Could not initialize database");
        }

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
            var exceptionResponse = new ExceptionResponse(exception.getMessage());
            return new Gson().toJson(exceptionResponse);
        } catch (NotFoundException exception) {
            response.status(400);
            var exceptionResponse = new ExceptionResponse(exception.getMessage());
            return new Gson().toJson(exceptionResponse);
        } catch (AlreadyTakenException exception) {
            response.status(403);
            var exceptionResponse = new ExceptionResponse(exception.getMessage());
            return new Gson().toJson(exceptionResponse);
        } catch (DataAccessException exception) {
            response.status(500);
            var exceptionResponse = new ExceptionResponse(exception.getMessage());
            return new Gson().toJson(exceptionResponse);
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
            var exceptionResponse = new ExceptionResponse(exception.getMessage());
            return new Gson().toJson(exceptionResponse);
        } catch (UnauthorizedException exception) {
            response.status(401);
            var exceptionResponse = new ExceptionResponse(exception.getMessage());
            return new Gson().toJson(exceptionResponse);
        } catch (DataAccessException exception) {
            response.status(500);
            var exceptionResponse = new ExceptionResponse(exception.getMessage());
            return new Gson().toJson(exceptionResponse);
        }
    }

    private String getGames(Request request, Response response) {
        try {
            String authToken = request.headers("Authorization");
            var authData = new AuthData(null, authToken);
            var gamesData = this.gameService.getGames(dataAccess, authData);
            var responseObj = new GetGamesResponse(gamesData);
            response.status(200);
            return new Gson().toJson(responseObj);
        } catch (UnauthorizedException exception) {
            response.status(401);
            var exceptionResponse = new ExceptionResponse(exception.getMessage());
            return new Gson().toJson(exceptionResponse);
        } catch (DataAccessException exception) {
            response.status(500);
            var exceptionResponse = new ExceptionResponse(exception.getMessage());
            return new Gson().toJson(exceptionResponse);
        }
    }

    private String clearDatabase(Request request, Response response) {
        try {
            this.registrationService.clearDatabase(dataAccess);
            response.status(200);
            return "";
        } catch (DataAccessException exception) {
            response.status(500);
            var exceptionResponse = new ExceptionResponse(exception.getMessage());
            return new Gson().toJson(exceptionResponse);
        }
    }

    private Object registerUser(Request request, Response response) {
        var user = new Gson().fromJson(request.body(), User.class);
        try {
            AuthData authData = this.registrationService.registerUser(dataAccess, user);
            if (authData == null) {
                throw new UnauthorizedException("Error: already taken");
            }
            return new Gson().toJson(authData);
        } catch (DataAccessException exception) {
            response.status(500);
            var exceptionResponse = new ExceptionResponse(exception.getMessage());
            return new Gson().toJson(exceptionResponse);
        } catch (MissingDataException exception) {
            response.status(400);
            var exceptionResponse = new ExceptionResponse(exception.getMessage());
            return new Gson().toJson(exceptionResponse);
        } catch (UnauthorizedException exception) {
            response.status(403);
            var exceptionResponse = new ExceptionResponse(exception.getMessage());
            return new Gson().toJson(exceptionResponse);
        }
    }

    private Object login(Request request, Response response) {
        var user = new Gson().fromJson(request.body(), User.class);
        try {
            var authData = this.sessionService.loginUser(dataAccess, user);
            return new Gson().toJson(authData);
        } catch (UnauthorizedException exception) {
            response.status(401);
            var exceptionResponse = new ExceptionResponse(exception.getMessage());
            return new Gson().toJson(exceptionResponse);
        } catch (MissingDataException | DataAccessException exception) {
            response.status(500);
            var exceptionResponse = new ExceptionResponse(exception.getMessage());
            return new Gson().toJson(exceptionResponse);
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
            var exceptionResponse = new ExceptionResponse(exception.getMessage());
            return new Gson().toJson(exceptionResponse);
        } catch (DataAccessException exception) {
            response.status(500);
            var exceptionResponse = new ExceptionResponse(exception.getMessage());
            return new Gson().toJson(exceptionResponse);
        }
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }


}
