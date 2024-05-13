package ch.uzh.ifi.hase.soprafs24.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

@Service
@Transactional
public class ServiceProvider {

    private final LobbyService lobbyService;
    private final PlayerService playerService;

    @Autowired
    public ServiceProvider(LobbyService lobbyService, PlayerService playerService) {
        this.lobbyService = lobbyService;
        this.playerService = playerService;
    }
    public LobbyService getLobbyService() {
        return lobbyService;
    }

    public PlayerService getPlayerService() {
        return playerService;
    }

}
