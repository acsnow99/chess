package serverFacade;

import exceptions.HttpResponseException;
import model.AuthData;
import model.GameData;
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
}
