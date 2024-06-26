package ch.uzh.ifi.hase.soprafs24.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import ch.uzh.ifi.hase.soprafs24.service.ServiceProvider;
import ch.uzh.ifi.hase.soprafs24.service.WebsocketService;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.ReadyRequest;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.SelectionRequest;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.StartGameRequest;
import ch.uzh.ifi.hase.soprafs24.utils.GameSettings;

@Controller
public class StompController {

    private static final Logger logger = LoggerFactory.getLogger(StompController.class);
    private final ServiceProvider serviceProvider;
    private final GameService gameService;
    private final WebsocketService wsService;

    @Autowired
    public StompController(ServiceProvider serviceProvider, GameService gameService, WebsocketService wsService) {
        this.serviceProvider = serviceProvider;
        this.gameService = gameService;
        this.wsService = wsService;
    }

    // Set or change lobby settings
    @MessageMapping("/settings/{lobbyId}")
    public void updateSettings(@DestinationVariable Long lobbyId, final GameSettings updatedGameSettings) {
        //change settings
        serviceProvider.getLobbyService().updateGameSettings(lobbyId, updatedGameSettings);
        wsService.broadcastLobby(lobbyId);
    }
   
    // Start game and distribute roles
    @MessageMapping("/startgame")
    public void startGame(final StartGameRequest startGameRequest) {
        Long lobbyId = startGameRequest.getLobbyId();

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

    // Vote during voting phase
    @MessageMapping("/voting")
    public void vote(final SelectionRequest request) {
        logger.info("user {} wants to vote for {}", request.getUsername(), request.getSelection());
        //maybe check if both players are in the same lobby
        
        //vote for selected player
        if(request.getSelection() != null && !request.getSelection().isEmpty()) {
            serviceProvider.getPlayerService().voteForPlayer(request.getSelection());
        }
    }
   
    // Broadcasting lobby information/changes
    @SendTo("/topic/lobby/{lobbyId}")
    public Lobby sendLobbyInfo(@DestinationVariable String lobbyId, Lobby lobby) {
       return lobby;
    }

    @MessageMapping("/{roleName}/nightaction")
    public void performNightAction(@DestinationVariable String roleName, final SelectionRequest request){
        logger.info("{} {} selected {} during NIGHT", roleName, request.getUsername(), request.getSelection());

        switch(roleName) {
            case "Seer":
                break;
            case "Werewolf":
                gameService.werewolfNightAction(request);
                break;
            case "Villager":
                break;
            case "Protector":
                gameService.protectorNightAction(request);
                break;
            case "Sacrifice":
                gameService.sacrificeNightAction(request);
                break;
            default:
                logger.info("{} is not a valid role", roleName);
        }
    }
    
}
