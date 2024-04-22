package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.GameState;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.repository.RepositoryProvider;
import ch.uzh.ifi.hase.soprafs24.utils.GameSettings;
import ch.uzh.ifi.hase.soprafs24.utils.LobbyCodeGenerator;
import ch.uzh.ifi.hase.soprafs24.utils.roles.Werewolf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.List;


@Service
@Transactional
public class GameService {

    private final Logger log = LoggerFactory.getLogger(GameService.class);

    private final RepositoryProvider repositoryProvider;
    private final ServiceProvider serviceProvider;
    private final Werewolf werewolf;

    @Autowired
    public GameService(RepositoryProvider repositoryProvider, ServiceProvider serviceProvider, Werewolf werewolf) {
        this.repositoryProvider = repositoryProvider;
        this.serviceProvider = serviceProvider;
        this.werewolf = werewolf;
    }

    public Player createPlayer(Player newPlayer) {
        newPlayer.setToken(UUID.randomUUID().toString());
        serviceProvider.getPlayerService().checkIfUserExists(newPlayer); // check if the same username is entered
        serviceProvider.getLobbyService().CheckIfLobbyExists(newPlayer); // check if the lobby code is correct

        // lobby exists and username is not taken
        // check if the lobby is full
        serviceProvider.getLobbyService().CheckIfLobbyFull(newPlayer.getLobbyCode());

        // set the fields for a new player
        newPlayer.setIsAlive(Boolean.TRUE);
        newPlayer.setIsProtected(Boolean.FALSE);
        newPlayer.setIsKilled(Boolean.FALSE);
        newPlayer.setIsReady(Boolean.FALSE);

        // get the lobby id by the lobby code
        newPlayer.setLobbyId(repositoryProvider.getLobbyRepository().findByLobbyCode(newPlayer.getLobbyCode()).getLobbyId());

        newPlayer = repositoryProvider.getPlayerRepository().save(newPlayer);
        repositoryProvider.getPlayerRepository().flush();

        log.debug("Created Information for User: {}", newPlayer);
        return newPlayer;
    }

    public Lobby createLobby(Lobby newLobby) {
        String lobbyCode = LobbyCodeGenerator.generateLobbyCode();

        newLobby.setLobbyCode(lobbyCode);
        newLobby.setGameState(GameState.WAITINGROOM);
        newLobby.setGameSettings(serviceProvider.getLobbyService().setDefaultSettings(newLobby.getNumberOfPlayers()));

        newLobby = repositoryProvider.getLobbyRepository().save(newLobby);
        repositoryProvider.getLobbyRepository().flush();

        // Trigger the Creation of Host Player
        Player hostPlayer = new Player(newLobby.getHostName(), newLobby.getLobbyCode());
        Player createdPlayer = createPlayer(hostPlayer);

        List<Player> players = serviceProvider.getLobbyService().getListOfLobbyPlayers(lobbyCode);
        newLobby.setPlayers(players);

        log.debug("Created Information for Lobby: {}", newLobby);
        return newLobby;
    }

    public void goToNextPhase (Long lobbyId) {
        if(!serviceProvider.getPlayerService().areAllPlayersReady(lobbyId)) {
            log.info("Not all Players are ready yet to go to next Phase");
        } else {
            Lobby lobby = repositoryProvider.getLobbyRepository().findByLobbyId(lobbyId);
            switch (lobby.getGameState()) {
                case WAITINGROOM:
                    lobby.setGameState(GameState.NIGHT);
                    break;
                case NIGHT:
                    lobby.setGameState(GameState.REVEALNIGHT);
                    break;
                case REVEALNIGHT:
                    lobby.setGameState(GameState.DISCUSSION);
                    break;
                case DISCUSSION:
                    lobby.setGameState(GameState.VOTING);
                    break;
                case VOTING:
                    lobby.setGameState(GameState.REVEALVOTING);
                    break;
                case REVEALVOTING:
                    lobby.setGameState(GameState.NIGHT);
                    break;
                default:
                    break;
            }
            log.info("lobby {} is now in phase {}", lobby.getLobbyId(), lobby.getGameState());

            //reset all players to isReady = false
            serviceProvider.getPlayerService().setPlayersNotReady(lobbyId);
        }
    }

    public void werewolfNightAction(String selection) {
        werewolf.setSelection(selection);
        werewolf.doNightAction();
    }

}
