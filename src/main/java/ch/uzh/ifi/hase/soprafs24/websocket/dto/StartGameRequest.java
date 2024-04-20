package ch.uzh.ifi.hase.soprafs24.websocket.dto;

public class StartGameRequest {
    
    private Long lobbyId;

    public Long getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(Long lobbyId) {
        this.lobbyId = lobbyId;
    }
}
