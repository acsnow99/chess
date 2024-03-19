package responses;

import model.GameData;

import java.util.ArrayList;

public record GetGamesResponse(ArrayList<GameData> games) {
}
