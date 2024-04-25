package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.GameState;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the UserResource REST resource.
 *
 * @see GameService
 */
@WebAppConfiguration
@SpringBootTest
public class GameServiceIntegrationTest {

  @Qualifier("playerRepository")
  @Autowired
  private PlayerRepository playerRepository;

  @Qualifier("lobbyRepository")
  @Autowired
  private LobbyRepository lobbyRepository;

  @Autowired
  private GameService gameService;

  @BeforeEach
  public void setup() {

      playerRepository.deleteAll();
      lobbyRepository.deleteAll();
  }

  @Test
  void createPlayer_validInputs_success() {
    // given
    assertNull(playerRepository.findByUsername("testPlayer"));

    Lobby testLobby = new Lobby();
    testLobby.setHostName("testHost");
    testLobby.setNumberOfPlayers(7);
    Lobby createdLobby = gameService.createLobby(testLobby);

    Player testPlayer = new Player();
    testPlayer.setUsername("testPlayer");
    testPlayer.setLobbyCode(createdLobby.getLobbyCode());

    // when
    Player createdPlayer = gameService.createPlayer(testPlayer);

    // then
    assertEquals(testPlayer.getPlayerId(), createdPlayer.getPlayerId());
    assertEquals(testPlayer.getUsername(), createdPlayer.getUsername());
    assertNotNull(testPlayer.getToken());
    assertEquals(testPlayer.getLobbyCode(), createdPlayer.getLobbyCode());
    assertEquals(testPlayer.getLobbyId(), createdPlayer.getLobbyId());
    assertEquals(testPlayer.getIsKilled(), createdPlayer.getIsKilled());
    assertEquals(testPlayer.getIsAlive(), createdPlayer.getIsAlive());
    assertEquals(testPlayer.getIsProtected(), createdPlayer.getIsProtected());
    assertEquals(testPlayer.getIsReady(), createdPlayer.getIsReady());

  }

  @Test
  void createLobby_validInputs_success() {
    // given
    assertNull(playerRepository.findByUsername("testUsername"));

    Lobby testLobby = new Lobby();
    testLobby.setLobbyId(1L);
    testLobby.setHostName("testPlayer");
    testLobby.setLobbyCode("AG8HH");
    testLobby.setGameState(GameState.NIGHT);
    testLobby.setNumberOfPlayers(7);

    // when
    Lobby createdLobby = gameService.createLobby(testLobby);

    // then
    assertEquals(testLobby.getLobbyId(), createdLobby.getLobbyId());
    assertEquals(testLobby.getHostName(), createdLobby.getHostName());
    assertEquals(testLobby.getLobbyCode(), createdLobby.getLobbyCode());
    assertEquals(testLobby.getGameState(), createdLobby.getGameState());
    assertEquals(testLobby.getNumberOfPlayers(), createdLobby.getNumberOfPlayers());

  }

  @Test
  void createPlayer_duplicateUsername_throwsException() {
    assertNull(playerRepository.findByUsername("testPlayer"));
    assertNull(playerRepository.findByUsername("testHost"));

    Lobby testLobby = new Lobby();
    testLobby.setHostName("testHost");
    testLobby.setNumberOfPlayers(7);
    Lobby createdLobby = gameService.createLobby(testLobby);

    Player testPlayer = new Player();
    testPlayer.setUsername("testPlayer");
    testPlayer.setLobbyCode(createdLobby.getLobbyCode());
    Player createdPlayer = gameService.createPlayer(testPlayer);

    // attempt to create second user with same username
    Player testPlayer2 = new Player();
    testPlayer2.setUsername("testPlayer");
    testPlayer2.setLobbyCode(createdLobby.getLobbyCode());

    // check that an error is thrown
    assertThrows(ResponseStatusException.class, () -> gameService.createPlayer(testPlayer2));

  }

  @Test
  void createPlayer_InvalidLobbyCode_throwsException() {
    assertNull(playerRepository.findByUsername("testPlayer"));

    Player testPlayer = new Player();
    testPlayer.setUsername("testPlayer");
    testPlayer.setLobbyCode("testCode");

    assertThrows(ResponseStatusException.class, () -> gameService.createPlayer(testPlayer));
  }

}
