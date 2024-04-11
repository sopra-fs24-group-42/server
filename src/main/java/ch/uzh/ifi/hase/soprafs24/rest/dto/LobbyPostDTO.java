package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class LobbyPostDTO {

    private String hostName;

    private int numberOfPlayers;

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public void setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

}
