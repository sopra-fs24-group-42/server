package ch.uzh.ifi.hase.soprafs24.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import ch.uzh.ifi.hase.soprafs24.controller.StompController;
import ch.uzh.ifi.hase.soprafs24.service.WebsocketService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class WebsocketEvents {

    private static final Logger logger = LoggerFactory.getLogger(StompController.class);

    @Autowired
    private WebsocketService wsService;

    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {

        String destination = event.getMessage().getHeaders().get("simpDestination").toString();
        logger.info("New subscription to: " + destination);

        Long lobbyId = getLobbyId(destination);

        wsService.broadcastLobby(lobbyId);
    }

    //extract lobbyId from destinationPath ("/topic/lobby/{lobbyId}"). assumes the lobbyId is given after the last "/" character.
    private static long getLobbyId(String destinationPath) {

        int lastSlashIndex = destinationPath.lastIndexOf('/');
        if (lastSlashIndex == -1 || lastSlashIndex == destinationPath.length() - 1) {
            throw new IllegalArgumentException("the lobbyId could not be extracted from the subscribe destinatio path.");
        }
        return Long.parseLong(destinationPath.substring(lastSlashIndex + 1));
    }
}
