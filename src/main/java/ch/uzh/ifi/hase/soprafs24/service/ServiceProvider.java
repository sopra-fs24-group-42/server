package ch.uzh.ifi.hase.soprafs24.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServiceProvider {

    private final LobbyService lobbyService;

    private final PlayerService playerService;
    private final WebsocketService wsService;

    public ServiceProvider(LobbyService lobbyService, PlayerService playerService, WebsocketService wsService) {
        this.lobbyService = lobbyService;
        this.playerService = playerService;
        this.wsService = wsService;
    }
    public LobbyService getLobbyService() {
        return lobbyService;
    }

    public PlayerService getPlayerService() {
        return playerService;
    }

    public WebsocketService getWsService() {
        return wsService;
    }
}
