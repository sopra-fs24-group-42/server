package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.GameState;
import ch.uzh.ifi.hase.soprafs24.constant.WinnerSide;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.repository.RepositoryProvider;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs24.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs24.service.ServiceProvider;
import ch.uzh.ifi.hase.soprafs24.utils.roles.Protector;
import ch.uzh.ifi.hase.soprafs24.utils.roles.Sacrifice;
import ch.uzh.ifi.hase.soprafs24.utils.roles.Werewolf;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.SelectionRequest;
import ch.uzh.ifi.hase.soprafs24.service.LobbyService;
import ch.uzh.ifi.hase.soprafs24.service.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.ArrayList;
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

  @Mock
  private LobbyService lobbyService;

  @Mock
  private PlayerService playerService;

  @Mock
  private Werewolf werewolf;

  @Mock
  private Sacrifice sacrifice;

  @Mock
  private Protector protector;

  @InjectMocks
  private GameService gameService;
  
  @Captor 
  private ArgumentCaptor<Lobby> lobbyArgumentCaptor;

  private Player testPlayer;

  private Lobby testLobby;

  // @Captor private ArgumentCaptor<Player> playerArgumentCaptor;

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
  void createPlayer_duplicateUsername_throwsException() {
    doThrow(new ResponseStatusException(
      HttpStatus.CONFLICT, 
      "The username provided is not unique. Therefore, the player could not be created!")).when(playerService).checkIfUserExists(any(Player.class));

    assertThrows(ResponseStatusException.class, () -> gameService.createPlayer(testPlayer));

    verify(playerService).checkIfUserExists(testPlayer);
    verify(serviceProvider, never()).getLobbyService();
}

  @Test
  void createPlayer_InvalidLobbyCode_throwsException() {
    Player newPlayer = new Player("testPlayer2", "GFDST");
    Mockito.when(lobbyRepository.findByLobbyCode(Mockito.any())).thenReturn(null);
    doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "The lobby code provided does not exist. Therefore, the player could not be added!"))
    .when(lobbyService).checkIfLobbyExists(any(Player.class));
    assertThrows(ResponseStatusException.class, () -> gameService.createPlayer(newPlayer));
    verify(lobbyService).checkIfLobbyExists(newPlayer);
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

  @Test
  public void test_delete_host_player_success() {
    String hostUsername = "hostPlayer";
    String newHostUsername = "testPlayer";
    
    Player hostPlayer = new Player(); 
    hostPlayer.setUsername(hostUsername);
    hostPlayer.setLobbyId(2L);
    hostPlayer.setLobbyCode("NBHFY");

    Player newPlayerHostToBe = new Player();
    newPlayerHostToBe.setUsername(newHostUsername);
    newPlayerHostToBe.setLobbyId(2L);
    newPlayerHostToBe.setLobbyCode("NBHFY");

    Lobby lobby = new Lobby();
    lobby.setLobbyId(2L);
    lobby.setLobbyCode("NBHFY");
    lobby.setHostName(hostUsername);
    lobby.setPlayers(Arrays.asList(hostPlayer, newPlayerHostToBe));

    Mockito.when(playerRepository.findByUsername(hostUsername)).thenReturn(hostPlayer);
    Mockito.when(lobbyRepository.findByLobbyId(anyLong())).thenReturn(lobby);
    Mockito.when(lobbyService.getListOfLobbyPlayers(anyString())).thenReturn(Arrays.asList(newPlayerHostToBe));

    gameService.deletePlayer(hostPlayer.getUsername());
    List<Player> playersAfterDeletion = lobbyService.getListOfLobbyPlayers(lobby.getLobbyCode());
    assertFalse(playersAfterDeletion.contains(hostPlayer));

    ArgumentCaptor<Lobby> lobbyArgumentCaptor = ArgumentCaptor.forClass(Lobby.class);
    verify(repositoryProvider.getLobbyRepository()).save(lobbyArgumentCaptor.capture());
    Lobby savedLobby = lobbyArgumentCaptor.getValue();

    assertEquals(newHostUsername, savedLobby.getHostName());
  }

  @Test
  public void goToNextPhase_ShouldNotProceedIfPlayersAreNotReady() {
    Long lobbyId = 1L;
    
    when(playerService.areAllPlayersReady(lobbyId)).thenReturn(false);
    
    Logger logger = (Logger) LoggerFactory.getLogger(GameService.class);

    ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    listAppender.start();

    logger.addAppender(listAppender);

    gameService.goToNextPhase(lobbyId);

    List<ILoggingEvent> logsList = listAppender.list;

    boolean found = logsList.stream()
            .anyMatch(event -> event.getMessage().contains("Not all Players are ready yet to go to next Phase"));
    
    assertTrue(found, "Expected log message not found");
  }

  @Test
  public void goToNextPhase_ChangeFromWaitingroomToPrenight() {
    Long lobbyId = 1L;
    Lobby lobby = mock(Lobby.class);
    Player hostPlayer = mock(Player.class);
    hostPlayer.setIsReady(Boolean.TRUE);
    hostPlayer.setUsername("toaster");

    when(playerService.areAllPlayersReady(lobbyId)).thenReturn(true);
    when(lobby.getGameState()).thenReturn(GameState.WAITINGROOM);
    when(lobbyRepository.findByLobbyId(Mockito.anyLong())).thenReturn(lobby);
    when(playerRepository.findByUsername("toaster")).thenReturn(hostPlayer);
    when(lobby.getHostName()).thenReturn("toaster");

    gameService.goToNextPhase(lobbyId);

    verify(lobby).setGameState(GameState.PRENIGHT);
    assertEquals(Boolean.FALSE, hostPlayer.getIsReady());
    verify(lobbyRepository).save(lobby);
  }

  @Test
  public void goToNextPhase_ChangeFromPrenightToNight() {
    Long lobbyId = 1L;
    Lobby lobby = mock(Lobby.class);

    when(playerService.areAllPlayersReady(lobbyId)).thenReturn(true);
    when(lobby.getGameState()).thenReturn(GameState.PRENIGHT);
    when(lobbyRepository.findByLobbyId(Mockito.anyLong())).thenReturn(lobby);

    gameService.goToNextPhase(lobbyId);

    verify(lobby).setGameState(GameState.NIGHT);
    verify(lobbyRepository).save(lobby);
  }

  @Test
  public void goToNextPhase_ChangeFromNightToRevealnight_HostAlive() {
    Long lobbyId = 1L;
    Lobby lobby = mock(Lobby.class);
    Player hostPlayer = mock(Player.class);
    hostPlayer.setIsAlive(Boolean.TRUE);
    hostPlayer.setUsername("toaster");

    when(playerService.areAllPlayersReady(lobbyId)).thenReturn(true);
    when(lobby.getGameState()).thenReturn(GameState.NIGHT);
    when(lobbyRepository.findByLobbyId(Mockito.anyLong())).thenReturn(lobby);
    when(playerRepository.findByUsername("toaster")).thenReturn(hostPlayer);
    when(lobby.getHostName()).thenReturn("toaster");

    gameService.goToNextPhase(lobbyId);

    verify(lobby).setGameState(GameState.REVEALNIGHT);
    verify(lobbyRepository).save(lobby);
  }

  @Test
  public void goToNextPhase_ChangeFromNightToRevealnight_HostDead() {
    //TODO
  }

  @Test
  public void goToNextPhase_CaseRevealnightGameNotEnded_GoToDiscussion() {
    Long lobbyId = 1L;
    Lobby lobby = mock(Lobby.class);

    when(playerService.areAllPlayersReady(lobbyId)).thenReturn(true);
    when(lobby.getGameState()).thenReturn(GameState.REVEALNIGHT);
    when(lobbyRepository.findByLobbyId(Mockito.anyLong())).thenReturn(lobby);

    gameService.goToNextPhase(lobbyId);

    verify(lobby).setGameState(GameState.DISCUSSION);
    verify(lobbyRepository).save(lobby);
  }

  @Test
  public void goToNextPhase_CaseRevealnightGameEnded_GoToEndgame() {
    //TODO
  }

  @Test
  public void goToNextPhase_ChangeFromDiscussionToVoting() {
    Long lobbyId = 1L;
    Lobby lobby = mock(Lobby.class);

    when(playerService.areAllPlayersReady(lobbyId)).thenReturn(true);
    when(lobby.getGameState()).thenReturn(GameState.DISCUSSION);
    when(lobbyRepository.findByLobbyId(Mockito.anyLong())).thenReturn(lobby);

    gameService.goToNextPhase(lobbyId);

    verify(lobby).setGameState(GameState.VOTING);
    verify(lobbyRepository).save(lobby);
  }

  @Test
  public void goToNextPhase_ChangeFromVotingToRevealvoting_HostAlive() {
    Long lobbyId = 1L;
    Lobby lobby = mock(Lobby.class);
    Player hostPlayer = mock(Player.class);
    hostPlayer.setIsAlive(Boolean.TRUE);
    hostPlayer.setUsername("toaster");

    when(playerService.areAllPlayersReady(lobbyId)).thenReturn(true);
    when(lobby.getGameState()).thenReturn(GameState.VOTING);
    when(lobbyRepository.findByLobbyId(Mockito.anyLong())).thenReturn(lobby);
    when(playerRepository.findByUsername("toaster")).thenReturn(hostPlayer);
    when(lobby.getHostName()).thenReturn("toaster");

    gameService.goToNextPhase(lobbyId);

    verify(lobby).setGameState(GameState.REVEALVOTING);
    verify(lobbyRepository).save(lobby);
  }

  @Test
  public void goToNextPhase_ChangeFromVotingToRevealvoting_HostDead() {
    //TODO
  }

  @Test
  public void goToNextPhase_CaseRevealvotingGameNotEnded_GoToPrenight() {
    Long lobbyId = 1L;
    Lobby lobby = mock(Lobby.class);
    Player hostPlayer = mock(Player.class);

    when(playerService.areAllPlayersReady(lobbyId)).thenReturn(true);
    when(lobby.getGameState()).thenReturn(GameState.REVEALVOTING);
    when(lobbyRepository.findByLobbyId(Mockito.anyLong())).thenReturn(lobby);
    when(playerRepository.findByUsername("toaster")).thenReturn(hostPlayer);
    when(lobby.getHostName()).thenReturn("toaster");

    gameService.goToNextPhase(lobbyId);

    verify(lobby).setGameState(GameState.PRENIGHT);
    verify(lobbyRepository).save(lobby);
  }

  @Test
  public void goToNextPhase_CaseRevealvotingGameEnded_GoToEndgame() {
    //TODO
  }

  @Test
  public void goToNextPhase_CaseEndgameWerewolvesWin_GoToWaitingroom() {
    Long lobbyId = 1L;
    Lobby lobby = mock(Lobby.class);

    when(playerService.areAllPlayersReady(lobbyId)).thenReturn(true);
    when(lobby.getGameState()).thenReturn(GameState.ENDGAME);
    when(lobbyRepository.findByLobbyId(Mockito.anyLong())).thenReturn(lobby);
    when(lobby.getWinnerSide()).thenReturn(WinnerSide.WEREWOLVES);

    gameService.goToNextPhase(lobbyId);

    verify(lobby).setGameState(GameState.WAITINGROOM);
    verify(lobbyRepository).save(lobby);
  }

  @Test
  public void goToNextPhase_CaseEndgameVillagerWin_GoToWaitingroom() {
    Long lobbyId = 1L;
    Lobby lobby = mock(Lobby.class);

    when(playerService.areAllPlayersReady(lobbyId)).thenReturn(true);
    when(lobby.getGameState()).thenReturn(GameState.ENDGAME);
    when(lobbyRepository.findByLobbyId(Mockito.anyLong())).thenReturn(lobby);
    when(lobby.getWinnerSide()).thenReturn(WinnerSide.VILLAGERS);

    gameService.goToNextPhase(lobbyId);

    verify(lobby).setGameState(GameState.WAITINGROOM);
    verify(lobbyRepository).save(lobby);
  }

  @Test
  public void goToNextPhase_CaseEndgameNoWinner_GoToWaitingroom() {
    Long lobbyId = 1L;
    Lobby lobby = mock(Lobby.class);

    when(playerService.areAllPlayersReady(lobbyId)).thenReturn(true);
    when(lobby.getGameState()).thenReturn(GameState.ENDGAME);
    when(lobbyRepository.findByLobbyId(Mockito.anyLong())).thenReturn(lobby);
    when(lobby.getWinnerSide()).thenReturn(WinnerSide.NOWINNER);

    gameService.goToNextPhase(lobbyId);

    verify(lobby).setGameState(GameState.WAITINGROOM);
    verify(lobbyRepository).save(lobby);
  }

  @Test
  public void werewolfNightAction_ValidForActionCorrectRole_DoNightAction() {
    String werewolfUsername = "player1";
    String selectionUsername = "player2";
    String roleName = "Werewolf";

    SelectionRequest selectionRequest = new SelectionRequest();
    selectionRequest.setUsername(werewolfUsername);
    selectionRequest.setSelection(selectionUsername);

    when(this.werewolf.getSelection()).thenReturn(selectionUsername);
    when(this.werewolf.getRoleName()).thenReturn(roleName);
    when(this.werewolf.getUsername()).thenReturn(werewolfUsername);

    Player werewolfPlayer = new Player();
    werewolfPlayer.setUsername(werewolfUsername);
    werewolfPlayer.setRoleName(roleName);

    Player selectedPlayer = new Player();
    selectedPlayer.setUsername(selectionUsername);

    when(playerRepository.findByUsername(werewolfUsername)).thenReturn(werewolfPlayer);
    when(playerRepository.findByUsername(selectionUsername)).thenReturn(selectedPlayer);
    when(playerService.playersLobbyEqual(werewolfUsername, selectionUsername)).thenReturn(true);

    gameService.werewolfNightAction(selectionRequest);

    verify(this.werewolf).setUsername("player1");
    verify(this.werewolf).setSelection("player2");
    verify(this.werewolf, times(1)).doNightAction();
  }

  @Test
  public void werewolfNightAction_NoSelection_ShouldNotTriggerNightAction() {
    String werewolfUsername = "player1";
    String selectionUsername = "";
    String roleName = "Werewolf";

    SelectionRequest selectionRequest = new SelectionRequest();
    selectionRequest.setUsername(werewolfUsername);
    selectionRequest.setSelection(selectionUsername);

    when(this.werewolf.getSelection()).thenReturn(selectionUsername);
    when(this.werewolf.getRoleName()).thenReturn(roleName);
    when(this.werewolf.getUsername()).thenReturn(werewolfUsername);

    Player werewolfPlayer = new Player();
    werewolfPlayer.setUsername(werewolfUsername);
    werewolfPlayer.setRoleName(roleName);

    Player selectedPlayer = new Player();
    selectedPlayer.setUsername(selectionUsername);

    when(playerRepository.findByUsername(werewolfUsername)).thenReturn(werewolfPlayer);
    when(playerRepository.findByUsername(selectionUsername)).thenReturn(selectedPlayer);
    when(playerService.playersLobbyEqual(werewolfUsername, selectionUsername)).thenReturn(true);

    // Logger logger = (Logger) LoggerFactory.getLogger(LobbyService.class);
    // ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    // listAppender.start();
    // logger.addAppender(listAppender);
    // List<ILoggingEvent> logsList = listAppender.list;
    // boolean found = logsList.stream()
    //             .anyMatch(event -> event.getMessage().contains("Player " + werewolfUsername + " does not select anyone"));

    gameService.werewolfNightAction(selectionRequest);

    verify(this.werewolf).setUsername("player1");
    verify(this.werewolf).setSelection("");
    verify(this.werewolf, never()).doNightAction();
    //assertTrue(found, "Expected log message not found");
  }

  @Test
  public void werewolfNightAction_IncorrectRole_ShouldNotTriggerNightAction() {
    String werewolfUsername = "player1";
    String selectionUsername = "player2";
    String playerRoleName = "Villager";
    String werewolfRoleName = "Werewolf";

    SelectionRequest selectionRequest = new SelectionRequest();
    selectionRequest.setUsername(werewolfUsername);
    selectionRequest.setSelection(selectionUsername);

    when(this.werewolf.getSelection()).thenReturn(selectionUsername);
    when(this.werewolf.getRoleName()).thenReturn(werewolfRoleName);
    when(this.werewolf.getUsername()).thenReturn(werewolfUsername);

    Player werewolfPlayer = new Player();
    werewolfPlayer.setUsername(werewolfUsername);
    werewolfPlayer.setRoleName(playerRoleName);

    Player selectedPlayer = new Player();
    selectedPlayer.setUsername(selectionUsername);

    when(playerRepository.findByUsername(werewolfUsername)).thenReturn(werewolfPlayer);
    when(playerRepository.findByUsername(selectionUsername)).thenReturn(selectedPlayer);
    when(playerService.playersLobbyEqual(werewolfUsername, selectionUsername)).thenReturn(true);

    // Logger logger = (Logger) LoggerFactory.getLogger(LobbyService.class);
    // ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    // listAppender.start();
    // logger.addAppender(listAppender);
    // List<ILoggingEvent> logsList = listAppender.list;
    // boolean found = logsList.stream()
    //             .anyMatch(event -> event.getMessage().contains("Player " + werewolfUsername + " does not select anyone"));

    gameService.werewolfNightAction(selectionRequest);

    verify(this.werewolf).setUsername("player1");
    verify(this.werewolf).setSelection("player2");
    verify(this.werewolf, never()).doNightAction();
    //assertTrue(found, "Expected log message not found");
  }

  @Test
  public void protectorNightAction_ValidForActionCorrectRole_DoNightAction() {
    String protectorUsername = "player1";
    String selectionUsername = "player2";
    String roleName = "Protector";

    SelectionRequest selectionRequest = new SelectionRequest();
    selectionRequest.setUsername(protectorUsername);
    selectionRequest.setSelection(selectionUsername);

    when(this.protector.getSelection()).thenReturn(selectionUsername);
    when(this.protector.getRoleName()).thenReturn(roleName);
    when(this.protector.getUsername()).thenReturn(protectorUsername);

    Player protectorPlayer = new Player();
    protectorPlayer.setUsername(protectorUsername);
    protectorPlayer.setRoleName(roleName);

    Player selectedPlayer = new Player();
    selectedPlayer.setUsername(selectionUsername);

    when(playerRepository.findByUsername(protectorUsername)).thenReturn(protectorPlayer);
    when(playerRepository.findByUsername(selectionUsername)).thenReturn(selectedPlayer);
    when(playerService.playersLobbyEqual(protectorUsername, selectionUsername)).thenReturn(true);

    gameService.protectorNightAction(selectionRequest);

    verify(this.protector).setUsername("player1");
    verify(this.protector).setSelection("player2");
    verify(this.protector, times(1)).doNightAction();
  }

  @Test
  public void protectorNightAction_NoSelection_ShouldNotTriggerNightAction() {
    String protectorUsername = "player1";
    String selectionUsername = "";
    String roleName = "Protector";

    SelectionRequest selectionRequest = new SelectionRequest();
    selectionRequest.setUsername(protectorUsername);
    selectionRequest.setSelection(selectionUsername);

    when(this.protector.getSelection()).thenReturn(selectionUsername);
    when(this.protector.getRoleName()).thenReturn(roleName);
    when(this.protector.getUsername()).thenReturn(protectorUsername);

    Player protectorPlayer = new Player();
    protectorPlayer.setUsername(protectorUsername);
    protectorPlayer.setRoleName(roleName);

    Player selectedPlayer = new Player();
    selectedPlayer.setUsername(selectionUsername);

    when(playerRepository.findByUsername(protectorUsername)).thenReturn(protectorPlayer);
    when(playerRepository.findByUsername(selectionUsername)).thenReturn(selectedPlayer);
    when(playerService.playersLobbyEqual(protectorUsername, selectionUsername)).thenReturn(true);

    // Logger logger = (Logger) LoggerFactory.getLogger(LobbyService.class);
    // ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    // listAppender.start();
    // logger.addAppender(listAppender);
    // List<ILoggingEvent> logsList = listAppender.list;
    // boolean found = logsList.stream()
    //             .anyMatch(event -> event.getMessage().contains("Player " + werewolfUsername + " does not select anyone"));

    gameService.protectorNightAction(selectionRequest);

    verify(this.protector).setUsername("player1");
    verify(this.protector).setSelection("");
    verify(this.protector, never()).doNightAction();
    //assertTrue(found, "Expected log message not found");
  }

  @Test
  public void protectorNightAction_IncorrectRole_ShouldNotTriggerNightAction() {
    String protectorUsername = "player1";
    String selectionUsername = "player2";
    String playerRoleName = "Villager";
    String protectorRoleName = "Protector";

    SelectionRequest selectionRequest = new SelectionRequest();
    selectionRequest.setUsername(protectorUsername);
    selectionRequest.setSelection(selectionUsername);

    when(this.protector.getSelection()).thenReturn(selectionUsername);
    when(this.protector.getRoleName()).thenReturn(protectorRoleName);
    when(this.protector.getUsername()).thenReturn(protectorUsername);

    Player protectorPlayer = new Player();
    protectorPlayer.setUsername(protectorUsername);
    protectorPlayer.setRoleName(playerRoleName);

    Player selectedPlayer = new Player();
    selectedPlayer.setUsername(selectionUsername);

    when(playerRepository.findByUsername(protectorUsername)).thenReturn(protectorPlayer);
    when(playerRepository.findByUsername(selectionUsername)).thenReturn(selectedPlayer);
    when(playerService.playersLobbyEqual(protectorUsername, selectionUsername)).thenReturn(true);

    // Logger logger = (Logger) LoggerFactory.getLogger(LobbyService.class);
    // ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    // listAppender.start();
    // logger.addAppender(listAppender);
    // List<ILoggingEvent> logsList = listAppender.list;
    // boolean found = logsList.stream()
    //             .anyMatch(event -> event.getMessage().contains("Player " + werewolfUsername + " does not select anyone"));

    gameService.protectorNightAction(selectionRequest);

    verify(this.protector).setUsername("player1");
    verify(this.protector).setSelection("player2");
    verify(this.protector, never()).doNightAction();
    //assertTrue(found, "Expected log message not found");
  }

  @Test
  public void sacrificeNightAction_ValidForActionCorrectRole_DoNightAction() {
    String sacrificeUsername = "player1";
    String selectionUsername = "player2";
    String roleName = "Sacrifice";

    SelectionRequest selectionRequest = new SelectionRequest();
    selectionRequest.setUsername(sacrificeUsername);
    selectionRequest.setSelection(selectionUsername);

    when(this.sacrifice.getSelection()).thenReturn(selectionUsername);
    when(this.sacrifice.getRoleName()).thenReturn(roleName);
    when(this.sacrifice.getUsername()).thenReturn(sacrificeUsername);

    Player sacrificePlayer = new Player();
    sacrificePlayer.setUsername(sacrificeUsername);
    sacrificePlayer.setRoleName(roleName);

    Player selectedPlayer = new Player();
    selectedPlayer.setUsername(selectionUsername);

    when(playerRepository.findByUsername(sacrificeUsername)).thenReturn(sacrificePlayer);
    when(playerRepository.findByUsername(selectionUsername)).thenReturn(selectedPlayer);
    when(playerService.playersLobbyEqual(sacrificeUsername, selectionUsername)).thenReturn(true);

    gameService.sacrificeNightAction(selectionRequest);

    verify(this.sacrifice).setUsername("player1");
    verify(this.sacrifice).setSelection("player2");
    verify(this.sacrifice, times(1)).doNightAction();
  }

  @Test
  public void sacrificeNightAction_NoSelection_ShouldNotTriggerNightAction() {
    String sacrificeUsername = "player1";
    String selectionUsername = "";
    String roleName = "Sacrifice";

    SelectionRequest selectionRequest = new SelectionRequest();
    selectionRequest.setUsername(sacrificeUsername);
    selectionRequest.setSelection(selectionUsername);

    when(this.sacrifice.getSelection()).thenReturn(selectionUsername);
    when(this.sacrifice.getRoleName()).thenReturn(roleName);
    when(this.sacrifice.getUsername()).thenReturn(sacrificeUsername);

    Player sacrificePlayer = new Player();
    sacrificePlayer.setUsername(sacrificeUsername);
    sacrificePlayer.setRoleName(roleName);

    Player selectedPlayer = new Player();
    selectedPlayer.setUsername(selectionUsername);

    when(playerRepository.findByUsername(sacrificeUsername)).thenReturn(sacrificePlayer);
    when(playerRepository.findByUsername(selectionUsername)).thenReturn(selectedPlayer);
    when(playerService.playersLobbyEqual(sacrificeUsername, selectionUsername)).thenReturn(true);

    // Logger logger = (Logger) LoggerFactory.getLogger(LobbyService.class);
    // ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    // listAppender.start();
    // logger.addAppender(listAppender);
    // List<ILoggingEvent> logsList = listAppender.list;
    // boolean found = logsList.stream()
    //             .anyMatch(event -> event.getMessage().contains("Player " + werewolfUsername + " does not select anyone"));

    gameService.sacrificeNightAction(selectionRequest);

    verify(this.sacrifice).setUsername("player1");
    verify(this.sacrifice).setSelection("");
    verify(this.sacrifice, never()).doNightAction();
    //assertTrue(found, "Expected log message not found");
  }

  @Test
  public void sacrificeNightAction_IncorrectRole_ShouldNotTriggerNightAction() {
    String sacrificeUsername = "player1";
    String selectionUsername = "player2";
    String playerRoleName = "Villager";
    String sacrificeRoleName = "Sacrifice";

    SelectionRequest selectionRequest = new SelectionRequest();
    selectionRequest.setUsername(sacrificeUsername);
    selectionRequest.setSelection(selectionUsername);

    when(this.sacrifice.getSelection()).thenReturn(selectionUsername);
    when(this.sacrifice.getRoleName()).thenReturn(sacrificeRoleName);
    when(this.sacrifice.getUsername()).thenReturn(sacrificeUsername);

    Player sacrificePlayer = new Player();
    sacrificePlayer.setUsername(sacrificeUsername);
    sacrificePlayer.setRoleName(playerRoleName);

    Player selectedPlayer = new Player();
    selectedPlayer.setUsername(selectionUsername);

    when(playerRepository.findByUsername(sacrificeUsername)).thenReturn(sacrificePlayer);
    when(playerRepository.findByUsername(selectionUsername)).thenReturn(selectedPlayer);
    when(playerService.playersLobbyEqual(sacrificeUsername, selectionUsername)).thenReturn(true);

    // Logger logger = (Logger) LoggerFactory.getLogger(LobbyService.class);
    // ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    // listAppender.start();
    // logger.addAppender(listAppender);
    // List<ILoggingEvent> logsList = listAppender.list;
    // boolean found = logsList.stream()
    //             .anyMatch(event -> event.getMessage().contains("Player " + werewolfUsername + " does not select anyone"));

    gameService.sacrificeNightAction(selectionRequest);

    verify(this.sacrifice).setUsername("player1");
    verify(this.sacrifice).setSelection("player2");
    verify(this.sacrifice, never()).doNightAction();
    //assertTrue(found, "Expected log message not found");
  }
}
