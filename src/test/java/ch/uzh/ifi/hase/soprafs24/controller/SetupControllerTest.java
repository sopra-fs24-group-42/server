package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.constant.WinnerSide;
import ch.uzh.ifi.hase.soprafs24.constant.GameState;
import ch.uzh.ifi.hase.soprafs24.utils.GameSettings;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyPostDTO;
import ch.uzh.ifi.hase.soprafs24.service.ServiceProvider;
import ch.uzh.ifi.hase.soprafs24.service.LobbyService;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import ch.uzh.ifi.hase.soprafs24.repository.RepositoryProvider;
import ch.uzh.ifi.hase.soprafs24.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs24.utils.LobbyCodeGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SetupController.class)
class SetupControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ServiceProvider serviceProvider;

  @MockBean
  private GameService gameService;

//  @Test
//  void createPlayer_validInput_playerCreated() throws Exception {
//    Player player = new Player();
//    player.setPlayerId(1L);
//    player.setUsername("testUsername");
//    player.setToken("1");
//    player.setLobbyCode("AH1PL");
//
//    PlayerPostDTO playerPostDTO = new PlayerPostDTO();
//    playerPostDTO.setUsername("testUsername");
//    playerPostDTO.setLobbyCode("AH1PL");
//
//    given(gameService.createPlayer(Mockito.any())).willReturn(player);
//
//    MockHttpServletRequestBuilder postRequest = post("/players")
//        .contentType(MediaType.APPLICATION_JSON)
//        .content(asJsonString(playerPostDTO));
//
//    mockMvc.perform(postRequest)
//        .andExpect(status().isCreated())
//        .andExpect(jsonPath("$.playerId", is(player.getPlayerId().intValue())))
//        .andExpect(jsonPath("$.username", is(player.getUsername())))
//        .andExpect(jsonPath("$.lobbyCode", is(player.getLobbyCode())));
//  }
//
//    @Test
//    void createLobby_validInput_lobbyCreated() throws Exception {
//      Lobby lobby = new Lobby();
//      lobby.setLobbyId(2L);
//      lobby.setHostName("testHost");
//      lobby.setNumberOfPlayers(7);
//      lobby.setGameSettings(new GameSettings());
//      lobby.setGameState(GameState.NIGHT);
//      //lobby.setCountNightaction(0);
//      lobby.setWinnerSide(WinnerSide.NOWINNER);
//
//      LobbyPostDTO lobbyPostDTO = new LobbyPostDTO();
//      lobbyPostDTO.setHostName("testHost");
//      lobbyPostDTO.setNumberOfPlayers(7);
//
//      given(gameService.createLobby(Mockito.any())).willReturn(lobby);
//
//      MockHttpServletRequestBuilder postRequest = post("/lobbies")
//              .contentType(MediaType.APPLICATION_JSON)
//              .content(asJsonString(lobbyPostDTO));
//
//      mockMvc.perform(postRequest)
//              .andExpect(status().isCreated())
//              .andExpect(jsonPath("$.lobbyId", is(lobby.getLobbyId().intValue())))
//              .andExpect(jsonPath("$.hostName", is(lobby.getHostName())))
//              .andExpect(jsonPath("$.numberOfPlayers", is(lobby.getNumberOfPlayers())));
//    }

    private String asJsonString(final Object object) {
    try {
      return new ObjectMapper().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format("The request body could not be created.%s", e.toString()));
    }
  }
}