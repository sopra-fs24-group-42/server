package ch.uzh.ifi.hase.soprafs24.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import ch.uzh.ifi.hase.soprafs24.websocket.dto.TestMessage;

@Service
public class WebsocketService {

    final private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    public WebsocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    //change message and TestMessage to Lobby
    public void broadcastLobby(final String message, final Long lobbyId) {
        TestMessage response = new TestMessage(message);
        String destination = "/topic/lobby/" + Long.toString(lobbyId);
        messagingTemplate.convertAndSend(destination, response);
    }

}
