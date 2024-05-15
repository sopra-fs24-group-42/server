package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.constant.WinnerSide;
import ch.uzh.ifi.hase.soprafs24.constant.GameState;
import ch.uzh.ifi.hase.soprafs24.utils.GameSettings;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyPostDTO;
import ch.uzh.ifi.hase.soprafs24.service.ServiceProvider;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import ch.uzh.ifi.hase.soprafs24.service.PlayerService;
import ch.uzh.ifi.hase.soprafs24.service.LobbyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;
import ch.uzh.ifi.hase.soprafs24.service.WebsocketService;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List; 
import java.util.ArrayList; 

@WebMvcTest(SetupController.class)
class SetupControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ServiceProvider serviceProvider;

  @MockBean
  private GameService gameService;

  @MockBean
  private PlayerService playerService;

  @MockBean
  private LobbyService lobbyService;

  @MockBean
  private WebsocketService wsService;

  @Test
  void createPlayer_validInput_playerCreated() throws Exception {
    Player player = new Player();
    player.setPlayerId(1L);
    player.setUsername("testUsername");
    player.setToken("1");
    player.setLobbyCode("AH1PL");
    player.setNumberOfVotes(0);

    PlayerPostDTO playerPostDTO = new PlayerPostDTO();
    playerPostDTO.setUsername("testUsername");
    playerPostDTO.setLobbyCode("AH1PL");

    given(gameService.createPlayer(Mockito.any())).willReturn(player);

    MockHttpServletRequestBuilder postRequest = post("/players")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(playerPostDTO));

    mockMvc.perform(postRequest)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.playerId", is(player.getPlayerId().intValue())))
        .andExpect(jsonPath("$.username", is(player.getUsername())))
        .andExpect(jsonPath("$.lobbyCode", is(player.getLobbyCode())));
  }

  @Test
  void createLobby_validInput_lobbyCreated() throws Exception {
    Lobby lobby = new Lobby();
    lobby.setLobbyId(2L);
    lobby.setHostName("testHost");
    lobby.setNumberOfPlayers(7);
    lobby.setGameSettings(new GameSettings());
    lobby.setGameState(GameState.NIGHT);
    lobby.setWinnerSide(WinnerSide.NOWINNER);

    LobbyPostDTO lobbyPostDTO = new LobbyPostDTO();
    lobbyPostDTO.setHostName("testHost");
    lobbyPostDTO.setNumberOfPlayers(7);

    given(gameService.createLobby(Mockito.any())).willReturn(lobby);

    MockHttpServletRequestBuilder postRequest = post("/lobbies")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(lobbyPostDTO));

    mockMvc.perform(postRequest)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.lobbyId", is(lobby.getLobbyId().intValue())))
        .andExpect(jsonPath("$.hostName", is(lobby.getHostName())))
        .andExpect(jsonPath("$.numberOfPlayers", is(lobby.getNumberOfPlayers())));
  }

  @Test
  void deletePlayer_validId_PlayerDeleted() throws Exception {
    Player player = new Player();
    player.setPlayerId(1L);
    player.setUsername("testUsername");
    player.setToken("1");
    player.setLobbyCode("AH1PL");
    player.setNumberOfVotes(0);
    
    Mockito.when(serviceProvider.getPlayerService()).thenReturn(playerService);
    Mockito.when(serviceProvider.getPlayerService().getLobbyIdFromPlayerByUsername(any())).thenReturn(player.getLobbyId());
    Mockito.doNothing().when(gameService).deletePlayer(any());

    MockHttpServletRequestBuilder deleteRequest = delete("/players/{username}", player.getUsername());

    mockMvc.perform(deleteRequest)
        .andExpect(status().isOk());
      
    Mockito.verify(playerService).getLobbyIdFromPlayerByUsername(player.getUsername());
    Mockito.verify(gameService).deletePlayer(player.getUsername());
    Mockito.verify(wsService).broadcastLobby(player.getLobbyId());
  }

  @Test
  void getLeaderboard_noPlayers_throws_error() throws Exception {
    int invalidMaxNumberOfTopPlayers = -1;

    mockMvc.perform(get("/leaderboards/{maxNumberOfTopPlayers}", invalidMaxNumberOfTopPlayers))
           .andExpect(status().isBadRequest())
           .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
           .andExpect(result -> assertEquals("maxNumberOfTopPlayers should be int greater than zero. Received: " + invalidMaxNumberOfTopPlayers,
           ((ResponseStatusException) result.getResolvedException()).getReason()));
  }

  @Test
  void givenPlayers_whenGetLeaderboard_successful() throws Exception {
      int maxNumberOfTopPlayers = 5;

      //given
      // create sample players in the game and sample leaderboard players
      List<Player> players = createSamplePlayers(maxNumberOfTopPlayers);

      Mockito.when(serviceProvider.getPlayerService()).thenReturn(playerService);
      // mock the service method that retrives top players in the game
      Mockito.when(playerService.getTopPlayers(maxNumberOfTopPlayers)).thenReturn(players);
      
      // when
      MockHttpServletRequestBuilder getRequest = get("/leaderboards/{maxNumberOfTopPlayers}", maxNumberOfTopPlayers)
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON);

      mockMvc.perform(getRequest).andExpect(status().isOk())
          .andExpect(jsonPath("$.length()", is(maxNumberOfTopPlayers)))
          .andExpect(jsonPath("$[0].position", is(1)))
          .andExpect(jsonPath("$[0].username", is(players.get(0).getUsername())))
          .andExpect(jsonPath("$[0].numberOfVillagerWins", is(players.get(0).getNumberOfVillagerWins())))
          .andExpect(jsonPath("$[0].numberOfWerewolfWins", is(players.get(0).getNumberOfWerewolfWins())))
          .andExpect(jsonPath("$[0].numberOfWins", is(players.get(0).getNumberOfWins()))); 
  }

  private List<Player> createSamplePlayers(int count) {
    List<Player> players = new ArrayList<>();
    for (int i = 0; i < count; i++) {
        Player player = new Player();
        player.setPlayerId(Long.valueOf(i));
        player.setUsername("Player" + i);
        player.setNumberOfVillagerWins(i);
        player.setNumberOfWerewolfWins(i);
        player.setNumberOfWins(i*2);
        players.add(player);
    }
    return players;
  }

  private String asJsonString(final Object object) {
    try {
      return new ObjectMapper().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format("The request body could not be created.%s", e.toString()));
    }
  }
}