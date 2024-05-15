package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class LeaderboardGetDTO {

    private int position;

    private String username;

    private int numberOfVillagerWins;

    private int numberOfWerewolfWins;

    private int numberOfWins;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
