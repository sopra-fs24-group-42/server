package ch.uzh.ifi.hase.soprafs24.service;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import ch.uzh.ifi.hase.soprafs24.constant.WinnerSide;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs24.repository.RepositoryProvider;
import ch.uzh.ifi.hase.soprafs24.utils.GameSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ch.qos.logback.classic.Logger;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class LobbyServiceTest {

    @Mock
    private RepositoryProvider repositoryProvider;

    @Mock
    private LobbyRepository lobbyRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Spy
    @InjectMocks
    private LobbyService lobbyService;

    @InjectMocks
    private GameService gameService;

    private Lobby testLobby;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(repositoryProvider.getLobbyRepository()).thenReturn(lobbyRepository);
        when(repositoryProvider.getPlayerRepository()).thenReturn(playerRepository);

        Lobby lobby = new Lobby();
        lobby.setLobbyId(1L);
        lobby.setLobbyCode("HHHHH");
        lobby.setHostName("testPlayer");
        lobby.setMinNumOfPlayers(4);
        lobby.setNumberOfPlayers(4);
        lobby.setGameSettings(new GameSettings());
        this.testLobby = lobby;
    }

    @Test
    void updateGameSettings__success() {
        GameSettings updatedGameSettings = mock(GameSettings.class);
        when(updatedGameSettings.getTotalNumberOfRoles()).thenReturn(5);

        when(lobbyRepository.findByLobbyId(testLobby.getLobbyId())).thenReturn(testLobby);
        lobbyService.updateGameSettings(testLobby.getLobbyId(), updatedGameSettings);

        verify(lobbyRepository, times(1)).findByLobbyId(testLobby.getLobbyId());
        verify(updatedGameSettings, times(1)).getTotalNumberOfRoles();
        assertEquals(5, testLobby.getNumberOfPlayers());
        assertSame(updatedGameSettings, testLobby.getGameSettings());
        verify(lobbyRepository, times(1)).save(testLobby);
    }

    @Test
    void updateGameSettings_NotEnoughRoles() {
        Lobby lobby = new Lobby();
        lobby.setLobbyId(2L);
        lobby.setMinNumOfPlayers(4);

        GameSettings updatedGameSettings = mock(GameSettings.class);

        when(updatedGameSettings.getTotalNumberOfRoles()).thenReturn(lobby.getMinNumOfPlayers() - 1);
        when(lobbyRepository.findByLobbyId(lobby.getLobbyId())).thenReturn(lobby);
        
        Logger logger = (Logger) LoggerFactory.getLogger(LobbyService.class);

        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();

        logger.addAppender(listAppender);

        lobbyService.updateGameSettings(lobby.getLobbyId(), updatedGameSettings);

        List<ILoggingEvent> logsList = listAppender.list;

        boolean found = logsList.stream()
                .anyMatch(event -> {
                    String message = event.getFormattedMessage();
                    return message.contains("Invalid number of roles, it is less than required '" + (lobby.getMinNumOfPlayers() - 1) + " < " + lobby.getMinNumOfPlayers()+ "'. Roles were not updated");
                });
        
        verify(lobbyRepository, never()).save(any(Lobby.class));
        assertTrue(found, "Expected log message not found");
    }

    @Test
    void updateGameSettings_ExceptionThrown() {
        LobbyService lobbyService = new LobbyService(repositoryProvider);

        when(lobbyRepository.findByLobbyId(testLobby.getLobbyId())).thenThrow(new RuntimeException());

        // create logger for LobbyService that provides basic interface
        Logger logger = (Logger) LoggerFactory.getLogger(LobbyService.class);

        // an appender is provided by Logback that stores log events in a list
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();

        logger.addAppender(listAppender);

        lobbyService.updateGameSettings(testLobby.getLobbyId(), new GameSettings());

        // ILoggingEvent is the type of event that Logback uses to represent a log entry
        List<ILoggingEvent> logsList = listAppender.list;

        // verify that the expected log message is present in the list
        boolean found = logsList.stream()
                .anyMatch(event -> event.getMessage().contains("Something went wrong while updating settings"));

        // second argument to assertTrue is a message that will be displayed if the
        // assertion fails
        assertTrue(found, "Expected log message not found");
    }

    @Test
    void reset_Lobby_success() {
        testLobby.setWinnerSide(WinnerSide.WEREWOLVES);

        when(lobbyRepository.findByLobbyId(testLobby.getLobbyId())).thenReturn(testLobby);
        when(repositoryProvider.getLobbyRepository()).thenReturn(lobbyRepository);

        lobbyService.resetLobby(testLobby.getLobbyId());

        ArgumentCaptor<Lobby> lobbyCaptor = ArgumentCaptor.forClass(Lobby.class);
        verify(lobbyRepository).save(lobbyCaptor.capture());
        verify(lobbyRepository).flush();

        Lobby capturedLobby = lobbyCaptor.getValue();
        assertEquals(WinnerSide.NOWINNER, capturedLobby.getWinnerSide());
    }

    @Test
    void givenLobby_lobby_is_full_throws_error() {
        Lobby lobby = new Lobby();
        lobby.setLobbyId(2L);
        lobby.setLobbyCode("LKJ1H");
        lobby.setNumberOfPlayers(4);

        when(lobbyRepository.findByLobbyCode(lobby.getLobbyCode())).thenReturn(lobby);
        List<Player> players = createSamplePlayers(4);

        // using a spy to partially mock the lobbyService
        // why to use spy: wrap an existing instance, while mock only create an instance (mock)
        // spy actially calls for the real implementation  
        doReturn(players).when(lobbyService).getListOfLobbyPlayers(lobby.getLobbyCode());
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            lobbyService.checkIfLobbyFull(lobby.getLobbyCode());
        });

        assertEquals(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE, exception.getStatus());
        assertEquals("The lobby is full. Therefore, the player could not be added!", exception.getReason());
    }

    private List<Player> createSamplePlayers(int count) {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Player player = new Player();
            player.setUsername("testPlayer" + i);
            player.setLobbyCode("HHHHH");
            players.add(player);
        }
        return players;
    }

    @Test
    void setDefaultSettings_not_enough_players_throws_error() {
        testLobby.setNumberOfPlayers(3);

        assertThrows(NullPointerException.class, () -> {
            GameSettings settings = lobbyService.setDefaultSettings(testLobby);
        }, "Lobby needs more Players");
    }
}
