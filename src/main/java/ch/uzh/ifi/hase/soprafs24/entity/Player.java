package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "PLAYER")
public class Player implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long playerId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private Boolean isAlive;

    @Column(nullable = false)
    private Boolean isProtected;

    @Column(nullable = false)
    private Boolean isKilled;

    @Column(nullable = false)
    private Boolean isSacrificed;

    @Column(nullable = false)
    private Boolean isReady;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private String lobbyCode;

    @Column(nullable = false)
    private Long lobbyId;

    @Column(nullable = true)
    private String roleName;

    @Column(nullable = false)
    private int numberOfVotes;

    @Column(nullable = false)
    private int numberOfVillagerWins;

    @Column(nullable = false)
    private int numberOfWerewolfWins;

    @Column(nullable = false)
    private int numberOfWins;

    public Player() {}

    public Player(String username, String lobbyCode) {
        this.username = username;
        this.lobbyCode = lobbyCode;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getIsAlive() {
        return isAlive;
    }

    public void setIsAlive(Boolean isAlive) {
        this.isAlive = isAlive;
    }

    public Boolean getIsProtected() {
        return isProtected;
    }

    public void setIsProtected(Boolean isProtected) {
        this.isProtected = isProtected;
    }

    public Boolean getIsKilled() {
        return isKilled;
    }

    public void setIsKilled(Boolean isKilled) {
        this.isKilled = isKilled;
    }

    public Boolean getIsSacrificed() {
        return isSacrificed;
    }

    public void setIsSacrificed(Boolean isSacrificed) {
        this.isSacrificed = isSacrificed;
    }

    public Boolean getIsReady() {
        return isReady;
    }

    public void setIsReady(Boolean isReady) {
        this.isReady = isReady;
    }

    public String getLobbyCode() {
        return lobbyCode;
    }

    public void setLobbyCode(String lobbyCode) {
        this.lobbyCode = lobbyCode;
    }

    public Long getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(Long lobbyId) {
        this.lobbyId = lobbyId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public int getNumberOfVotes() {
        return numberOfVotes;
    }

    public void setNumberOfVotes(int numberOfVotes) {
        this.numberOfVotes = numberOfVotes;
    }

    public int getNumberOfVillagerWins() {
        return numberOfVillagerWins;
    }

    public void setNumberOfVillagerWins(int numberOfVillagerWins) {
        this.numberOfVillagerWins = numberOfVillagerWins;
    }

    public int getNumberOfWerewolfWins() {
        return numberOfWerewolfWins;
    }

    public void setNumberOfWerewolfWins(int numberOfWerewolfWins) {
        this.numberOfWerewolfWins = numberOfWerewolfWins;
    }

    public int getNumberOfWins() {
        return numberOfWins;
    }

    public void setNumberOfWins(int numberOfWins) {
        this.numberOfWins = numberOfWins;
    }
}
