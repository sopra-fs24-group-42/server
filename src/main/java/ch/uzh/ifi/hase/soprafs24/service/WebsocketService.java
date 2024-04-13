package ch.uzh.ifi.hase.soprafs24.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.repository.RepositoryProvider;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.TestMessage;

@Service
public class WebsocketService {

    final private SimpMessagingTemplate messagingTemplate;
    private final RepositoryProvider repositoryProvider;
    
    @Autowired
    public WebsocketService(SimpMessagingTemplate messagingTemplate, RepositoryProvider repositoryProvider) {
        this.messagingTemplate = messagingTemplate;
        this.repositoryProvider = repositoryProvider;
    }

    public void broadcastLobby(final Long lobbyId) {
        Lobby lobbyToBroadcast = repositoryProvider.getLobbyRepository().findByLobbyId(lobbyId);
        lobbyToBroadcast.setPlayers(repositoryProvider.getPlayerRepository().findByLobbyId(lobbyId));     
        String destination = "/topic/lobby/" + Long.toString(lobbyId);
        messagingTemplate.convertAndSend(destination, lobbyToBroadcast);
    }

}
