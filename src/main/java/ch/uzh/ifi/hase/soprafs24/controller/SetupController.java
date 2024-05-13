package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LeaderboardGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyPostDTO;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import ch.uzh.ifi.hase.soprafs24.service.WebsocketService;
import ch.uzh.ifi.hase.soprafs24.service.ServiceProvider;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;


@RestController
public class SetupController {

    private static final Logger logger = LoggerFactory.getLogger(SetupController.class);
    private final GameService gameService;
    private final WebsocketService wsService;
    private final ServiceProvider serviceProvider;

    @Autowired
    SetupController(GameService gameService, WebsocketService wsService,
                    ServiceProvider  serviceProvider) {
        this.gameService = gameService;
        this.wsService = wsService;
        this.serviceProvider = serviceProvider;
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

    @DeleteMapping("/players/{username}")
    @ResponseStatus(HttpStatus.OK)
    public void deletePlayer(@PathVariable("username") String usernameOfPlayerToBeDeleted) {
        Long lobbyIdOfPlayerToBeDeleted = serviceProvider.getPlayerService().getLobbyIdFromPlayerByUsername(usernameOfPlayerToBeDeleted);
        gameService.deletePlayer(usernameOfPlayerToBeDeleted);

        wsService.broadcastLobby(lobbyIdOfPlayerToBeDeleted);
    }

    @GetMapping("/leaderboards/{maxNumberOfTopPlayers}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<LeaderboardGetDTO> getLeaderboards(@PathVariable("maxNumberOfTopPlayers") int maxNumberOfTopPlayers) {

        if (maxNumberOfTopPlayers < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "maxNumberOfTopPlayers should be int greater than zero. Received: " + maxNumberOfTopPlayers);
        }

        List<Player> topPlayers = serviceProvider.getPlayerService().getTopPlayers(maxNumberOfTopPlayers);
        List<LeaderboardGetDTO> Leaderboard = new ArrayList<>();

        for (Player player : topPlayers) {
            Leaderboard.add(DTOMapper.INSTANCE.convertEntityToLeaderboardGetDTO(player));
        }

        return Leaderboard;
    }   
}
