package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.GameState;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs24.repository.LobbyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

  @Mock
  private PlayerRepository playerRepository;
  @Mock
  private LobbyRepository lobbyRepository;
  @InjectMocks
  private GameService gameService;
  private Player testPlayer;
  private Lobby testLobby;

  @BeforeEach
  public void setupPlayer() {
    MockitoAnnotations.openMocks(this);

    // given
    this.testPlayer = new Player();
    testPlayer.setPlayerId(1L);
    testPlayer.setUsername("testPlayer");
    testPlayer.setLobbyId(2L);
    testPlayer.setLobbyCode("AG8HH");
    testPlayer.setIsAlive(Boolean.TRUE);
    testPlayer.setIsKilled(Boolean.FALSE);
    testPlayer.setIsProtected(Boolean.FALSE);
    testPlayer.setIsReady(Boolean.FALSE);
    // what to do with the token
    // testPlayer.setToken();

    // when -> any object is being save in the userRepository -> return the dummy
    // testUser
    Mockito.when(playerRepository.save(Mockito.any())).thenReturn(testPlayer);
  }

   // assertEquals(UserStatus.ONLINE, createdUser.getStatus());

  @BeforeEach
  public void setupLobby() {
    MockitoAnnotations.openMocks(this);
    this.testLobby = new Lobby();
    testLobby.setLobbyId(1L);
    testLobby.setHostName("testPlayer");
    testLobby.setLobbyCode("AG8HH");
    testLobby.setGameState(GameState.NIGHT);
    testLobby.setNumberOfPlayers(7);

    Mockito.when(lobbyRepository.save(Mockito.any())).thenReturn(testLobby);

  }

  @Test
  public void createPlayer_validInputs_success() {
    // when -> any object is being save in the userRepository -> return the dummy
    Player createdPlayer = gameService.createPlayer(testPlayer);

    // then
    Mockito.verify(playerRepository, Mockito.times(1)).save(Mockito.any());

    // maybe add other fields
    assertEquals(testPlayer.getPlayerId(), createdPlayer.getPlayerId());
    assertEquals(testPlayer.getUsername(), createdPlayer.getUsername());
    assertEquals(testPlayer.getLobbyCode(), createdPlayer.getLobbyCode());
    assertEquals(testPlayer.getLobbyId(), createdPlayer.getLobbyId());
    assertNotNull(createdPlayer.getToken());
    assertEquals(testPlayer.getIsKilled(), createdPlayer.getIsKilled());
    assertEquals(testPlayer.getIsAlive(), createdPlayer.getIsAlive());
    assertEquals(testPlayer.getIsProtected(), createdPlayer.getIsProtected());
    assertEquals(testPlayer.getIsReady(), createdPlayer.getIsReady());

  }

  @Test
  public void createUser_duplicateUsername_throwsException() {
    // given -> a first user has already been created
    gameService.createPlayer(testPlayer);

    // when -> setup additional mocks for UserRepository
    Mockito.when(playerRepository.findByUsername(Mockito.any())).thenReturn(testPlayer);
    Mockito.when(playerRepository.findByUsername(Mockito.any())).thenReturn(null);

    // then -> attempt to create second user with same user -> check that an error
    // is thrown
    assertThrows(ResponseStatusException.class, () -> gameService.createPlayer(testPlayer));
  }

  @Test
  public void createPlayer_InvalidLobbyCode_throwsException() {
    // given -> a first user has already been created
    gameService.createPlayer(testPlayer);

    // when -> setup additional mocks for UserRepository
    Mockito.when(playerRepository.findByUsername(Mockito.any())).thenReturn(testPlayer);
    Mockito.when(playerRepository.findByUsername(Mockito.any())).thenReturn(testPlayer);

    // then -> attempt to create second user with same user -> check that an error
    // is thrown
    assertThrows(ResponseStatusException.class, () -> gameService.createPlayer(testPlayer));
  }

  @Test
  public void createLobby_validInputs_success() {
      // when -> any object is being save in the userRepository -> return the dummy
      Lobby createdLobby = gameService.createLobby(testLobby);

      // then
      Mockito.verify(lobbyRepository, Mockito.times(1)).save(Mockito.any());

      // maybe add other fields
      assertEquals(testLobby.getLobbyId(), createdLobby.getLobbyId());
      assertEquals(testLobby.getHostName(), createdLobby.getHostName());
      assertEquals(testLobby.getLobbyCode(), createdLobby.getLobbyCode());
      assertEquals(testLobby.getGameState(), createdLobby.getGameState());
      assertEquals(testLobby.getNumberOfPlayers(), createdLobby.getNumberOfPlayers());

  }

}
