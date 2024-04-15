package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.repository.RepositoryProvider;
import ch.uzh.ifi.hase.soprafs24.events.CreateHostPlayerEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.context.event.EventListener;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PlayerService {

    private final Logger log = LoggerFactory.getLogger(PlayerService.class);

    private final RepositoryProvider repositoryProvider;

    @Autowired
    public PlayerService(RepositoryProvider repositoryProvider) {
        this.repositoryProvider = repositoryProvider;
    }

    @EventListener
    public void onPlayerCreationEvent(CreateHostPlayerEvent event) {
        Player player = event.getPlayer();
        createPlayer(player);
    }

    public Player createPlayer(Player newPlayer) {
        newPlayer.setToken(UUID.randomUUID().toString());
        checkIfUserExists(newPlayer);

        newPlayer.setIsAlive(Boolean.TRUE);
        newPlayer.setIsProtected(Boolean.FALSE);
        newPlayer.setIsKilled(Boolean.FALSE);
        newPlayer.setIsReady(Boolean.FALSE);

        CheckIfLobbyExists(newPlayer);
        newPlayer.setLobbyId(repositoryProvider.getLobbyRepository().findByLobbyCode(newPlayer.getLobbyCode()).getLobbyId());

        newPlayer = repositoryProvider.getPlayerRepository().save(newPlayer);
        repositoryProvider.getPlayerRepository().flush();

        log.debug("Created Information for User: {}", newPlayer);
        return newPlayer;
    }

    private void checkIfUserExists(Player playerToBeCreated) {
        Player userByUsername = repositoryProvider.getPlayerRepository().findByUsername(playerToBeCreated.getUsername());

        if (userByUsername != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The username provided is not unique. Therefore, the player could not be created!");
        }
    }

    private void CheckIfLobbyExists(Player playerToJoinLobby){
        Lobby lobbyByCode = repositoryProvider.getLobbyRepository().findByLobbyCode(playerToJoinLobby.getLobbyCode());

        if (lobbyByCode == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The lobby code provided does not exist. Therefore, the player could not be added!");
        }
    }

    public Long getLobbyIdFromPlayerByUsername(String username) {
        Player player = repositoryProvider.getPlayerRepository().findByUsername(username);
        return player.getLobbyId();
    }
}
