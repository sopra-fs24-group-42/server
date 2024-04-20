package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.GameState;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.repository.RepositoryProvider;
import ch.uzh.ifi.hase.soprafs24.utils.GameSettings;
import ch.uzh.ifi.hase.soprafs24.utils.LobbyCodeGenerator;
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

    @Autowired
    public GameService(RepositoryProvider repositoryProvider, ServiceProvider serviceProvider) {
        this.repositoryProvider = repositoryProvider;
        this.serviceProvider = serviceProvider;
    }

    public Player createPlayer(Player newPlayer) {
        newPlayer.setToken(UUID.randomUUID().toString());
        serviceProvider.getPlayerService().checkIfUserExists(newPlayer);
        serviceProvider.getLobbyService().CheckIfLobbyFull(newPlayer.getLobbyCode());

        newPlayer.setIsAlive(Boolean.TRUE);
        newPlayer.setIsProtected(Boolean.FALSE);
        newPlayer.setIsKilled(Boolean.FALSE);
        newPlayer.setIsReady(Boolean.FALSE);

        serviceProvider.getLobbyService().CheckIfLobbyExists(newPlayer);
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

}
