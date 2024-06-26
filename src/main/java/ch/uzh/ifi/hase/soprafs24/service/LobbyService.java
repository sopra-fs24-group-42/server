package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.WinnerSide;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.utils.GameSettings;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.repository.RepositoryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public void checkIfLobbyFull(String lobbyCode){
        Lobby lobby = repositoryProvider.getLobbyRepository().findByLobbyCode(lobbyCode);
        
        List<Player> players = getListOfLobbyPlayers(lobbyCode);
        if (players.size() >= lobby.getNumberOfPlayers()) {
            throw new ResponseStatusException(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE, "The lobby is full. Therefore, the player could not be added!");
        }
    }

    public void checkIfLobbyExists(Player playerToJoinLobby){
        Lobby lobbyByCode = repositoryProvider.getLobbyRepository().findByLobbyCode(playerToJoinLobby.getLobbyCode());

        if (lobbyByCode == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The lobby code provided does not exist. Therefore, the player could not be added!");
        }
    }

    public GameSettings setDefaultSettings(Lobby lobby) {
        if(lobby.getNumberOfPlayers() < lobby.getMinNumOfPlayers()) {
            throw new NullPointerException("Lobby needs more Players");
        }
        GameSettings gameSettings = new GameSettings();

        int numberOfWerewolves = lobby.getNumberOfPlayers() / 3;
        gameSettings.setNumberOfWerewolves(numberOfWerewolves);

        int numberOfSeers = 1;
        gameSettings.setNumberOfSeers(numberOfSeers);

        int numberOfProtectors = 1;
        gameSettings.setNumberOfProtectors(numberOfProtectors);

        int numberOfSacrifices = 1;
        gameSettings.setNumberOfSacrifices(numberOfSacrifices);
        
        int numberOfVillagers = lobby.getNumberOfPlayers() - numberOfWerewolves - numberOfSeers - numberOfProtectors - numberOfSacrifices;
        gameSettings.setNumberOfVillagers(numberOfVillagers);
        return gameSettings;
    }

    public void updateGameSettings(Long lobbyId, GameSettings updatedGameSettings){
        try {
            Lobby lobby = repositoryProvider.getLobbyRepository().findByLobbyId(lobbyId);
            int numberOfUpdatedRoles = updatedGameSettings.getTotalNumberOfRoles();

            // check if there more than 4 players at least
            if(numberOfUpdatedRoles < lobby.getMinNumOfPlayers()){
                log.info("Invalid number of roles, it is less than required '{} < {}'. Roles were not updated", numberOfUpdatedRoles, lobby.getMinNumOfPlayers());
                return;
            }

            // change the number of the players in the game
            log.info("numberOfUpdatedRoles is {} ", numberOfUpdatedRoles);
            lobby.setNumberOfPlayers(numberOfUpdatedRoles);
            lobby.setGameSettings(updatedGameSettings);

            repositoryProvider.getLobbyRepository().save(lobby);
            log.info("New settings are applied to lobby with id {}", lobby.getLobbyId());

        }
        catch (Exception ex){
            log.info("Something went wrong while updating settings");
        }
    }

    @Transactional
    public void resetLobby (Long lobbyId) {
        Lobby lobbyToReset = repositoryProvider.getLobbyRepository().findByLobbyId(lobbyId);

        //lobbyToReset.setCountNightaction(0);
        lobbyToReset.setWinnerSide(WinnerSide.NOWINNER);

        repositoryProvider.getLobbyRepository().save(lobbyToReset);
        repositoryProvider.getLobbyRepository().flush();
    }
}
