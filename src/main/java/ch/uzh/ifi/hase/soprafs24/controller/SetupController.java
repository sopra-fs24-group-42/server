package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyPostDTO;
import ch.uzh.ifi.hase.soprafs24.service.LobbyService;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class SetupController {

    private final PlayerService playerService;
    private final LobbyService lobbyService;

    SetupController(PlayerService playerService, LobbyService lobbyService) {
        this.playerService = playerService;
        this.lobbyService = lobbyService;
    }

    @PostMapping("/players")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public PlayerDTO createPlayer(@RequestBody PlayerPostDTO playerPostDTO) {
        // convert API user to internal representation
        Player playerInput = DTOMapper.INSTANCE.convertPlayerPostDTOtoEntity(playerPostDTO);

        // create user
        Player createdPlayer = playerService.createPlayer(playerInput);

        // convert internal representation of user back to API
        return DTOMapper.INSTANCE.convertEntityToPlayerDTO(createdPlayer);
    }

    @PostMapping("/lobbies")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public LobbyDTO createLobby(@RequestBody LobbyPostDTO lobbyPostDTO) {
        // convert API lobby to internal representation
        Lobby lobbyInput = DTOMapper.INSTANCE.convertLobbyPostDTOtoEntity(lobbyPostDTO);

        // create lobby
        Lobby createdLobby = lobbyService.createLobby(lobbyInput);

        // convert internal representation of user back to API
        return DTOMapper.INSTANCE.convertEntityToLobbyDTO(createdLobby);
    }
}
