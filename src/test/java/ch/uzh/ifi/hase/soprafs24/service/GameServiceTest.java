package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.GameState;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.repository.RepositoryProvider;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs24.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs24.service.ServiceProvider;
import ch.uzh.ifi.hase.soprafs24.service.LobbyService;
import ch.uzh.ifi.hase.soprafs24.service.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

  @Mock
  private PlayerRepository playerRepository;

  @Mock
  private LobbyRepository lobbyRepository;

  @Mock
  private ServiceProvider serviceProvider;

  @Mock
  private RepositoryProvider repositoryProvider;

  @Mock
  private LobbyService lobbyService;

  @Mock
  private PlayerService playerService;

  @InjectMocks
  private GameService gameService;

  private Player testPlayer;
  private Lobby testLobby;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    Mockito.when(repositoryProvider.getLobbyRepository()).thenReturn(lobbyRepository);
    Mockito.when(repositoryProvider.getPlayerRepository()).thenReturn(playerRepository);

    this.testLobby = new Lobby();
    testLobby.setLobbyId(2L);
    testLobby.setHostName("hostPlayer");
    testLobby.setLobbyCode("ABCDE");
    testLobby.setGameState(GameState.NIGHT);
    testLobby.setNumberOfPlayers(7);
    Mockito.when(lobbyRepository.save(Mockito.any())).thenReturn(testLobby);
    Mockito.when(lobbyRepository.findByLobbyCode(Mockito.anyString())).thenReturn(testLobby);

    this.testPlayer = new Player();
    testPlayer.setPlayerId(1L);
    testPlayer.setUsername("testPlayer");
    testPlayer.setLobbyId(2L);
    testPlayer.setLobbyCode("ABCDE");
    testPlayer.setIsAlive(Boolean.TRUE);
    testPlayer.setIsKilled(Boolean.FALSE);
    testPlayer.setIsProtected(Boolean.FALSE);
    testPlayer.setIsReady(Boolean.FALSE);
    testPlayer.setToken("1");
    Mockito.when(playerRepository.save(Mockito.any())).thenReturn(testPlayer);

    Mockito.when(serviceProvider.getLobbyService()).thenReturn(lobbyService);
    Mockito.when(serviceProvider.getPlayerService()).thenReturn(playerService);
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
  public void createPlayer_duplicateUsername_throwsException() {
    // Mockito.when(playerRepository.findByUsername("testPlayer")).thenReturn(testPlayer);

    doThrow(new ResponseStatusException(HttpStatus.CONFLICT, "The username provided is not unique. Therefore, the player could not be created!"))
            .when(playerService).checkIfUserExists(any(Player.class));

    assertThrows(ResponseStatusException.class, () -> gameService.createPlayer(testPlayer));

    // Mockito.verify(playerRepository).findByUsername("testPlayer");
    verify(playerService).checkIfUserExists(testPlayer);
    verify(serviceProvider, never()).getLobbyService(); //??
  }

  @Test
  public void createPlayer_InvalidLobbyCode_throwsException() {
    Player newPlayer = new Player("testPlayer2", "GFDST");

      // when -> setup additional mocks for UserRepository
    Mockito.when(lobbyRepository.findByLobbyCode(Mockito.any())).thenReturn(null);

    doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "The lobby code provided does not exist. Therefore, the player could not be added!"))
            .when(lobbyService).CheckIfLobbyExists(any(Player.class));

    // then -> attempt to create second user with same user -> check that an error
    // is thrown
    assertThrows(ResponseStatusException.class, () -> gameService.createPlayer(newPlayer));
    verify(lobbyService).CheckIfLobbyExists(newPlayer);

  }

  @Test
  public void createLobby_validInputs_success() {
      // when -> any object is being save in the userRepository -> return the dummy
      Lobby createdLobby = gameService.createLobby(this.testLobby);

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
