package serverFacade;

import exceptions.HttpResponseException;
import model.AuthData;
import model.GameData;
import requests.CreateGameRequest;
import responses.CreateGameResponse;
import responses.GetGamesResponse;

import java.util.ArrayList;

public class ServerFacadeGame extends ServerFacade {
    public ServerFacadeGame(String URL) {
        super(URL);
    }

    public ArrayList<GameData> getGames(AuthData authorization) throws HttpResponseException {
        try {
            var response = makeHttpRequest("GET", "/game", null, GetGamesResponse.class, authorization);
            return response.games();
        } catch (Exception exception) {
            throw new HttpResponseException(exception.getMessage());
        }
    }

    public long createGame(AuthData authorization, String gameName) throws HttpResponseException {
        try {
            var request = new CreateGameRequest(gameName);
            var response = makeHttpRequest("POST", "/game", request, CreateGameResponse.class, authorization);
            return response.gameID();
        } catch (Exception exception) {
            throw new HttpResponseException(exception.getMessage());
        }
    }
}
