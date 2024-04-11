package ch.uzh.ifi.hase.soprafs24.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RepositoryProvider {

    private PlayerRepository playerRepository;
    private LobbyRepository lobbyRepository;

    @Autowired
    public RepositoryProvider(@Qualifier("playerRepository") PlayerRepository playerRepository,
                              @Qualifier("lobbyRepository") LobbyRepository lobbyRepository) {
        this.playerRepository = playerRepository;
        this.lobbyRepository = lobbyRepository;
    }

    public PlayerRepository getPlayerRepository() {
        return playerRepository;
    }

    public LobbyRepository getLobbyRepository() {
        return lobbyRepository;
    }

}
