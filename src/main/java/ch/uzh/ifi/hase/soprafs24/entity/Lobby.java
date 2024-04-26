package ch.uzh.ifi.hase.soprafs24.entity;
import ch.uzh.ifi.hase.soprafs24.utils.GameSettings;

import ch.uzh.ifi.hase.soprafs24.constant.GameState;
import ch.uzh.ifi.hase.soprafs24.constant.WinnerSide;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "LOBBY")
public class Lobby implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long lobbyId;

    @Column(nullable = false)
    private String hostName;

    @Column(nullable = false, unique = true)
    private String lobbyCode;

    @Transient
    private List<Player> players;

    @Transient  // This map is not persisted in the database
    private Map<String, Player> playerMap;

    @Column(nullable = false)
    private GameState gameState;

    @Column(nullable = false)
    private WinnerSide winnerSide;

    @Column(nullable = false)
    private int numberOfPlayers;

    @Column(nullable = false)
    private int countNightaction;

    @Embedded
    private GameSettings gameSettings;

    public Long getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(Long lobbyId) {
        this.lobbyId = lobbyId;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getLobbyCode() {
        return lobbyCode;
    }

    public void setLobbyCode(String lobbyCode) {
        this.lobbyCode = lobbyCode;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public WinnerSide getWinnerSide() {
        return winnerSide;
    }

    public void setWinnerSide(WinnerSide winnerSide) {
        this.winnerSide = winnerSide;
    }

    public GameSettings getGameSettings() {
        return gameSettings;
    }

    public void setGameSettings(GameSettings gameSettings) {
        this.gameSettings = gameSettings;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public void setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    public Map<String, Player> getPlayerMap() {
        return playerMap;
    }

    public void setPlayerMap(Map<String, Player> playerMap) {
        this.playerMap = playerMap;
    }

    public int getCountNightaction() {
        return countNightaction;
    }

    public void setCountNightaction(int countNightaction) {
        this.countNightaction = countNightaction;
    }
}