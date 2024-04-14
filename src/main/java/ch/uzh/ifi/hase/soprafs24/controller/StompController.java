package ch.uzh.ifi.hase.soprafs24.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.repository.RepositoryProvider;
import ch.uzh.ifi.hase.soprafs24.service.WebsocketService;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.SelectionRequest;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.TestMessage;

@Controller
public class StompController {

    private static final Logger logger = LoggerFactory.getLogger(StompController.class);

    @Autowired
    private WebsocketService wsService;
    private final RepositoryProvider repositoryProvider;

    @Autowired
    public StompController(RepositoryProvider repositoryProvider) {
        this.repositoryProvider = repositoryProvider;
    }

    //for testing only
    @MessageMapping("/test")
    @SendTo("/topic/test")
    public SelectionRequest getInfo(final SelectionRequest selectionRequest) {
        logger.info("user: {}, selected: {}", selectionRequest.getUsername(), selectionRequest.getSelection());

        //get player specified in selectioRequest
        Player player = repositoryProvider.getPlayerRepository().findByUsername(selectionRequest.getUsername());

        //broadcast Lobby to /topic/lobby/{lobbyId}
        wsService.broadcastLobby(player.getLobbyId());
        
        //broadcast selectionRequest to /topic/test
        return selectionRequest;
    }

    // Set or change lobby settings
    @MessageMapping("/settings")
    public void updateSettings(final String lobbySettings) {
        //change settings
        //broadcast Lobby
    }
   
    // Start game and distribute roles
    @MessageMapping("/startgame")
    public void startGame(final Long lobbyId) {
        // asign roles
        //broadcast lobby
    }
   
    // Advance to next phase
    @MessageMapping("/ready")
    public void ready(final String username) {
        //implement readycheck
        //set player to ready
        //if all players ready broadcast lobby
        logger.info("The 'ready' method was called by user: {}", username);

    }
  
    // Perform night action
    @MessageMapping("/nightaction")
    public void performNightAction(final SelectionRequest request) {
        // Implement night action logic
        //set player to ready
        //if all ready broadcast lobby
    }
   
    // Vote during voting phase
    @MessageMapping("/voting")
    public void vote(final SelectionRequest request) {
        logger.info("The 'voting' method was called by user: {}", request.getUsername());
        // Implement voting logic
        //set player ready
        //if all ready (voted) broadcast lobby
    }
   
    // Broadcasting lobby information/changes
    @SendTo("/topic/lobby/{lobbyId}")
    public Lobby sendLobbyInfo(@DestinationVariable String lobbyId, Lobby lobby) {
       return lobby;
    }
    
}
