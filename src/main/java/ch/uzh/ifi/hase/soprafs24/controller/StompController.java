package ch.uzh.ifi.hase.soprafs24.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.SelectionRequest;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.TestMessage;

@Controller
public class StompController {

    private static final Logger logger = LoggerFactory.getLogger(StompController.class);

    //for testing
    @MessageMapping("/test")
    @SendTo("/topic/test")
    public TestMessage getInfo(final SelectionRequest selectionRequest){
        return new TestMessage(HtmlUtils.htmlEscape(selectionRequest.getSelection()));
    }

    // Set or change lobby settings
    @MessageMapping("/settings")
    public void updateSettings(@Payload String lobbySettings) {
        //will return Lobby
    }
   
    // Start game and distribute roles
    @MessageMapping("/startgame")
    public void startGame(@Payload Long lobbyId) {
        // return List Players
    }
   
    // Advance to next phase
    @MessageMapping("/ready")
    public void ready(@Payload String username) {
        //implement readycheck
        logger.info("The 'ready' method was called by user: {}", username);
        System.out.println(username);
        //messagingTemplate.convertAndSend("/topic/test", username);
    }
  
    // Perform night action
    @MessageMapping("/nightaction")
    public void performNightAction(@Payload SelectionRequest request) {
        // Implement night action logic
    }
   
    // Vote during voting phase
    @MessageMapping("/voting")
    public void vote(@Payload SelectionRequest request) {
        // Implement voting logic
    }
   
    // Broadcasting lobby information/changes
    @MessageMapping("/lobby/{lobbyId}")
    public Lobby broadcastLobbyInfo(@DestinationVariable Long lobbyId, @Payload Lobby lobbyInfo) {
       return lobbyInfo;
    }
    
    /*
    @MessageMapping("/*")
    public void handleUnrecognizedDestination(SimpMessageHeaderAccessor headerAccessor) {
    String destination = headerAccessor.getDestination();
    throw new IllegalArgumentException("Unrecognized destination: " + destination);
    }
    */
}
