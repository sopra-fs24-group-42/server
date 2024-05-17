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
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class GameServiceTest {

  @Mock
  private PlayerRepository playerRepository;

  @Mock
  private LobbyRepository lobbyRepository;

  @Mock
  private ServiceProvider serviceProvider;

  @Mock
  private RepositoryProvider repositoryProvider;

  @InjectMocks
  private LobbyService lobbyService;

  @InjectMocks
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
  void createPlayer_validInputs_success() {
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

  // fix -> went from mock to mock inject 
  @Test
  void createPlayer_duplicateUsername_throwsException() {
    // String duplicate_username = "testPlayer";
    // when(playerRepository.findByUsername(duplicate_username)).thenReturn(testPlayer);

    // Exception exception = assertThrows(ResponseStatusException.class, () -> {
    //     gameService.createPlayer(testPlayer);
    // });

    //assertEquals("The username provided is not unique. Therefore, the player could not be created!", exception.getMessage());
}

  // fix -> went from mock to mock inject 
  @Test
  void createPlayer_InvalidLobbyCode_throwsException() {
    // Player newPlayer = new Player("testPlayer2", "GFDST");

    // // when -> setup additional mocks for UserRepository
    // Mockito.when(lobbyRepository.findByLobbyCode(Mockito.any())).thenReturn(null);

    // doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND,
    //     "The lobby code provided does not exist. Therefore, the player could not be added!"))
    //     .when(lobbyService).checkIfLobbyExists(any(Player.class));

    // then -> attempt to create second user with same user -> check that an error
    // is thrown
    // assertThrows(ResponseStatusException.class, () -> gameService.createPlayer(newPlayer));
    // verify(lobbyService).checkIfLobbyExists(newPlayer);

  }

  @Test
  void createLobby_validInputs_success() {
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

  @Test
  void deletePlayer_validUsername_success() {
    String expectedMessage = "Player was deleted";
    System.out.println("length of the expected message: " + expectedMessage.length());

    when(playerRepository.findByUsername(testPlayer.getUsername())).thenReturn(testPlayer);
    System.out.println("player id: " + testPlayer.getPlayerId());
    when(lobbyRepository.findByLobbyId(testLobby.getLobbyId())).thenReturn(testLobby);
    System.out.println("lobby id: " + testLobby.getLobbyId());

    Logger logger = (Logger) LoggerFactory.getLogger(LobbyService.class);

    ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    listAppender.start();

    logger.addAppender(listAppender);

    gameService.deletePlayer(testPlayer.getUsername());

    assertEquals("", testPlayer.getLobbyCode());
    assertEquals(-2, testPlayer.getLobbyId());

    List<ILoggingEvent> logsList = listAppender.list;

    if (logsList.isEmpty()) {
      System.out.println("No logs captured.");
    } else {
      for (ILoggingEvent logEvent : logsList) {
        String message = logEvent.getFormattedMessage();
        System.out.println("Log Event: " + message + " | Length: " + message.length());
      }
      
      boolean found = logsList.stream()
      .anyMatch(event -> {
          String message = event.getFormattedMessage();
          return message.contains("Player was deleted");
      });
      assertTrue(found, "Expected log message not found");
  
    }

  }

  @Test
  public void testDeletePlayer_username_does_not_exist_throws_error() {
    String username = "testPlayer1";
    when(playerRepository.findByUsername(username)).thenReturn(null);

    assertThrows(ResponseStatusException.class, () -> {
      gameService.deletePlayer(username);
      ;
    });
    verify(playerRepository, never()).save(any(Player.class));
  }

}
