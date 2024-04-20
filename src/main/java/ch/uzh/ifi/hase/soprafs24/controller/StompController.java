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
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import ch.uzh.ifi.hase.soprafs24.service.ServiceProvider;
import ch.uzh.ifi.hase.soprafs24.service.WebsocketService;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.ReadyRequest;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.SelectionRequest;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.StartGameRequest;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.TestMessage;

@Controller
public class StompController {

    private static final Logger logger = LoggerFactory.getLogger(StompController.class);
    private final ServiceProvider serviceProvider;
    private final GameService gameService;

    @Autowired
    private WebsocketService wsService;

    @Autowired
    public StompController(ServiceProvider serviceProvider, GameService gameService) {
        this.serviceProvider = serviceProvider;
        this.gameService = gameService;
    }

    //for testing only
    @MessageMapping("/test")
    @SendTo("/topic/test")
    public SelectionRequest getInfo(final SelectionRequest selectionRequest) {
        logger.info("user: {}, selected: {}", selectionRequest.getUsername(), selectionRequest.getSelection());

        Long lobbyId = serviceProvider.getPlayerService().getLobbyIdFromPlayerByUsername(selectionRequest.getUsername());

        //Long lobbyId = playerService.getLobbyIdFromPlayerByUsername(selectionRequest.getUsername());

        //broadcast Lobby to /topic/lobby/{lobbyId}
        wsService.broadcastLobby(lobbyId);
        
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
    public void startGame(final StartGameRequest startGameRequest) {
        Long lobbyId = Long.valueOf(startGameRequest.getLobbyId());

        logger.info("lobby {} wants to start the game", lobbyId);
        // asign roles
        serviceProvider.getPlayerService().assignRoles(lobbyId);
        //broadcast lobby
        wsService.broadcastLobby(lobbyId);
    }
   
    // Advance to next phase
    @MessageMapping("/ready")
    public void ready(final ReadyRequest readyRequest) {
        //implement readycheck
        logger.info("User {} is in {} phase and ready!", readyRequest.getUsername(), readyRequest.getGameState());
        
        //set player to ready
        serviceProvider.getPlayerService().setPlayerReady(readyRequest.getUsername(), readyRequest.getGameState());

        //go to next phase
        Long lobbyId = serviceProvider.getPlayerService().getLobbyIdFromPlayerByUsername(readyRequest.getUsername());
        gameService.goToNextPhase(lobbyId);

        //just give updates to everyone
        wsService.broadcastLobby(lobbyId);
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
