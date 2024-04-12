package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.GameState;
import ch.uzh.ifi.hase.soprafs24.events.CreateHostPlayerEvent;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.utils.LobbyCodeGenerator;
import ch.uzh.ifi.hase.soprafs24.repository.RepositoryProvider;
import ch.uzh.ifi.hase.soprafs24.utils.GameSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class LobbyService {
    private final Logger log = LoggerFactory.getLogger(LobbyService.class);

    private final RepositoryProvider repositoryProvider;

    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public LobbyService(RepositoryProvider repositoryProvider,
                        ApplicationEventPublisher eventPublisher) {
        this.repositoryProvider = repositoryProvider;
        this.eventPublisher = eventPublisher;
    }

    public Lobby createLobby(Lobby newLobby) {
        String lobbyCode = LobbyCodeGenerator.generateLobbyCode();
        newLobby.setLobbyCode(lobbyCode);

        newLobby.setGameState(GameState.NIGHT);
        newLobby.setGameSettings(new GameSettings());

        // Create Host Player
        Player hostPlayer = new Player(newLobby.getHostName(), newLobby.getHostName());
        eventPublisher.publishEvent(new CreateHostPlayerEvent(this, hostPlayer));
        newLobby.setPlayers(getListOfLobbyPlayers(lobbyCode));

        newLobby = repositoryProvider.getLobbyRepository().save(newLobby);
        repositoryProvider.getLobbyRepository().flush();

        log.debug("Created Information for Lobby: {}", newLobby);
        return newLobby;
    }

    private List<Player> getListOfLobbyPlayers(String lobbyCode) {
        List<Player> players = repositoryProvider.getPlayerRepository().findByLobbyCode(lobbyCode);
        return players;
    }

}
