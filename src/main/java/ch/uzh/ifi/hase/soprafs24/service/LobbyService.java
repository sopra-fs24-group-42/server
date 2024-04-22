package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.utils.GameSettings;
import ch.uzh.ifi.hase.soprafs24.utils.Role;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.repository.RepositoryProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class LobbyService {
    private final Logger log = LoggerFactory.getLogger(LobbyService.class);

    private final RepositoryProvider repositoryProvider;


    @Autowired
    public LobbyService(RepositoryProvider repositoryProvider) {
        this.repositoryProvider = repositoryProvider;
    }

    public List<Player> getListOfLobbyPlayers(String lobbyCode) {
        return repositoryProvider.getPlayerRepository().findByLobbyCode(lobbyCode);
    }

    public void CheckIfLobbyFull(String lobbyCode){
        Lobby lobby = repositoryProvider.getLobbyRepository().findByLobbyCode(lobbyCode);

        List<Player> players = getListOfLobbyPlayers(lobbyCode);
        if (players.size() >= lobby.getNumberOfPlayers()) {
            throw new ResponseStatusException(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE, "The lobby is full. Therefore, the player could not be added!");
        }
    }

    public void CheckIfLobbyExists(Player playerToJoinLobby){
        Lobby lobbyByCode = repositoryProvider.getLobbyRepository().findByLobbyCode(playerToJoinLobby.getLobbyCode());

        if (lobbyByCode == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The lobby code provided does not exist. Therefore, the player could not be added!");
        }
    }

    public GameSettings setDefaultSettings(int numberOfPlayers) {
        if(numberOfPlayers < 3) {
            throw new NullPointerException("Lobby needs more Players");
        }
        GameSettings gameSettings = new GameSettings();
        int numberOfWerewolves = (int)(numberOfPlayers/3); 
        gameSettings.setNumberOfWerewolves(numberOfWerewolves);
        //-1 for Seer. Potential Bug if initializasion of GameSettings changes 
        gameSettings.setNumberOfVillagers(numberOfPlayers - numberOfWerewolves - 1);
        return gameSettings;
    }

    public void resetNightactionCount(Long lobbyId) {
        Lobby lobbyToReset = repositoryProvider.getLobbyRepository().findByLobbyId(lobbyId);
        lobbyToReset.setCountNightaction(0);
    }

    public void incrementCountNightaction(Long lobbyId) {
        Lobby lobby = repositoryProvider.getLobbyRepository().findByLobbyId(lobbyId);
        lobby.setCountNightaction(lobby.getCountNightaction() + 1);
    }

}
