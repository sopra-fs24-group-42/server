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
    private Boolean isReady;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private String lobbyCode;

    @Column(nullable = false)
    private Long lobbyId;

    @Transient   // field is non - persistent
    private Role role;
    //@Column(nullable = true)
   // private String roleName;

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

    public Boolean getIsReady() {
        return isReady;
    }

    public void setIsReady(Boolean isReady) {
        this.isReady = isReady;
    }
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
      //  this.roleName = role.getRoleName();
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
}
