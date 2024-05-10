package ch.uzh.ifi.hase.soprafs24.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.repository.RepositoryProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class WebsocketService {

    private static final Logger log = LoggerFactory.getLogger(WebsocketService.class);
    private final SimpMessagingTemplate messagingTemplate;
    private final RepositoryProvider repositoryProvider;
    
    @Autowired
    public WebsocketService(SimpMessagingTemplate messagingTemplate, RepositoryProvider repositoryProvider) {
        this.messagingTemplate = messagingTemplate;
        this.repositoryProvider = repositoryProvider;
    }

    public void broadcastLobby(final Long lobbyId) {
        try {
            Lobby lobbyToBroadcast = repositoryProvider.getLobbyRepository().findByLobbyId(lobbyId);

            // lobby might be deleted when all the players left it
            if(lobbyToBroadcast == null) {
             log.info("Lobby does not exist with id: '{}'", lobbyId);
             return;
            }

            List<Player> players = repositoryProvider.getPlayerRepository().findByLobbyId(lobbyId);
            Map<String, Player> playersMap = players.stream()
                                                    .collect(Collectors.toMap(Player::getUsername, player -> player));
            lobbyToBroadcast.setPlayerMap(playersMap);

            lobbyToBroadcast.setPlayers(repositoryProvider.getPlayerRepository().findByLobbyId(lobbyId));
            String destination = "/topic/lobby/" + Long.toString(lobbyId);
            messagingTemplate.convertAndSend(destination, lobbyToBroadcast);
        }
        catch(Exception e){
            log.info("Something went wrong while broadcasting the lobby");
        }
    }
}
