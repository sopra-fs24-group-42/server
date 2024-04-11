package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class PlayerPostDTO {

    private String username;

    private String lobbyCode;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLobbyCode() {
        return lobbyCode;
    }

    public void setLobbyCode(String lobbyCode) {
        this.lobbyCode = lobbyCode;
    }

}
