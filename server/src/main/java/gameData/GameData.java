package gameData;

import java.util.Objects;

public class GameData {

    private int gameID;
    private String whiteUserName;
    private String blackUserName;
    private String gameName;

    public GameData(int gameID, String whiteUserName, String blackUserName, String gameName) {
        this.gameID = gameID;
        this.whiteUserName = whiteUserName;
        this.blackUserName = blackUserName;
        this.gameName = gameName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameData gameData = (GameData) o;
        return gameID == gameData.gameID && Objects.equals(whiteUserName, gameData.whiteUserName) && Objects.equals(blackUserName, gameData.blackUserName) && Objects.equals(gameName, gameData.gameName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameID, whiteUserName, blackUserName, gameName);
    }

    @Override
    public String toString() {
        return "GameData{" +
                "gameID=" + gameID +
                ", whiteUserName='" + whiteUserName + '\'' +
                ", blackUserName='" + blackUserName + '\'' +
                ", gameName='" + gameName + '\'' +
                '}';
    }
}
