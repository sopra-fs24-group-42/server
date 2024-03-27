package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerPostDTO;
import ch.uzh.ifi.hase.soprafs24.service.LobbyService;
import ch.uzh.ifi.hase.soprafs24.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
@RestController
public class SetupController {

  private final PlayerService playerService;
  private final LobbyService lobbyService;

  SetupController(PlayerService playerService, LobbyService lobbyService) {
    this.playerService = playerService;
    this.lobbyService = lobbyService;
  }


  @PostMapping("/players")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public void createPlayer() {
    
  }

  @PostMapping("/lobbies")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public void createLobby(@RequestBody PlayerPostDTO userPostDTO) {
    
  }
}
