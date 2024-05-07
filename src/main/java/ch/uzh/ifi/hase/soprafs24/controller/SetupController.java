package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyPostDTO;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import ch.uzh.ifi.hase.soprafs24.service.WebsocketService;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class SetupController {

    private static final Logger logger = LoggerFactory.getLogger(SetupController.class);
    private final GameService gameService;
    private final WebsocketService wsService;

    SetupController(GameService gameService, WebsocketService wsService) {
        this.gameService = gameService;
        this.wsService = wsService;
    }

    @PostMapping("/players")
    @ResponseStatus(HttpStatus.CREATED)
    public PlayerDTO createPlayer(@RequestBody PlayerPostDTO playerPostDTO) {
        // convert API user to internal representation
        Player playerInput = DTOMapper.INSTANCE.convertPlayerPostDTOtoEntity(playerPostDTO);

        // create user
        Player createdPlayer = gameService.createPlayer(playerInput);

        // convert internal representation of user back to API
        return DTOMapper.INSTANCE.convertEntityToPlayerDTO(createdPlayer);
    }

    @PostMapping("/lobbies")
    @ResponseStatus(HttpStatus.CREATED)
    public LobbyDTO createLobby(@RequestBody LobbyPostDTO lobbyPostDTO) {
        // convert API lobby to internal representation
        Lobby lobbyInput = DTOMapper.INSTANCE.convertLobbyPostDTOtoEntity(lobbyPostDTO);

        // create lobby
        Lobby createdLobby = gameService.createLobby(lobbyInput);

        logger.info("created Lobby with lobbyId: {}", createdLobby.getLobbyId());

        // convert internal representation of user back to API
        return DTOMapper.INSTANCE.convertEntityToLobbyDTO(createdLobby);
    }

    @DeleteMapping("/players/{playerId}")
    @ResponseStatus(HttpStatus.GONE)
    public void deletePlayer(@PathVariable Long playerId) {
        gameService.deletePlayer(playerId);
        wsService.broadcastLobby(playerId);
        logger.info("deleted player with playerId: {}, new player can enter the lobby", playerId);
    }

}
