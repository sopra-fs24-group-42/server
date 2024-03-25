package ch.uzh.ifi.hase.soprafs24.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.SelectionRequest;

@Controller
public class StompController {

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
           // Implement logic to advance to the next phase
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
       @SendTo("/lobby/{lobbyId}")
       public Lobby broadcastLobbyInfo(@DestinationVariable Long lobbyId, @Payload Lobby lobbyInfo) {
           return lobbyInfo;
       }
 
}
