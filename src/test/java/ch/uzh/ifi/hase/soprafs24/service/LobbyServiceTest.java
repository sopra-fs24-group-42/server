package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.constant.GameState;
import ch.uzh.ifi.hase.soprafs24.constant.WinnerSide;
import ch.uzh.ifi.hase.soprafs24.utils.GameSettings;
import ch.uzh.ifi.hase.soprafs24.repository.RepositoryProvider;
import ch.uzh.ifi.hase.soprafs24.repository.LobbyRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class LobbyServiceTest {

    @Mock
    private RepositoryProvider repositoryProvider;

    @Mock
    private LobbyRepository lobbyRepository;

    @InjectMocks
    private LobbyService lobbyService;

    @InjectMocks
    private GameService gameService;

    private Lobby testLobby;

    @Mock
    private Logger log;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(repositoryProvider.getLobbyRepository()).thenReturn(lobbyRepository);
        Lobby lobby = new Lobby();
        lobby.setLobbyId(1L);
        lobby.setHostName("testPlayer");
        lobby.setMinNumOfPlayers(4);
        lobby.setGameSettings(new GameSettings());
        this.testLobby = lobby;
    }

    @Test
    void updateGameSettings__success() {
        GameSettings updatedGameSettings = Mockito.mock(GameSettings.class);
        Mockito.when(updatedGameSettings.getTotalNumberOfRoles()).thenReturn(5);

        Mockito.when(lobbyRepository.findByLobbyId(testLobby.getLobbyId())).thenReturn(testLobby);
        lobbyService.updateGameSettings(testLobby.getLobbyId(), updatedGameSettings);

        Mockito.verify(lobbyRepository, times(1)).findByLobbyId(testLobby.getLobbyId());
        Mockito.verify(updatedGameSettings, times(1)).getTotalNumberOfRoles();
        assertEquals(5, testLobby.getNumberOfPlayers());
        assertSame(updatedGameSettings, testLobby.getGameSettings());
        Mockito.verify(lobbyRepository, times(1)).save(testLobby);
    }

    @Test
    void updateGameSettings_NotEnoughRoles() {
        int minNumOfPlayers = 4;
        GameSettings updatedGameSettings = Mockito.mock(GameSettings.class);
        Mockito.when(updatedGameSettings.getTotalNumberOfRoles()).thenReturn(minNumOfPlayers - 1);

        Mockito.when(lobbyRepository.findByLobbyId(testLobby.getLobbyId())).thenReturn(testLobby);
        lobbyService.updateGameSettings(testLobby.getLobbyId(), updatedGameSettings);

        Mockito.verify(lobbyRepository, never()).save(any(Lobby.class));
        // Mockito.verify(log).info("Invalid number of roles, it is less than reqired
        // '{}' < '{}'. Roles were not updated", minNumOfPlayers - 1, minNumOfPlayers);
    }

    // @Test
    // void updateGameSettings_ExceptionThrown() {
    //     Mockito.mock(GameSettings.class); 
    //     Mockito.when(lobbyRepository.findByLobbyId(testLobby.getLobbyId())).thenThrow(new RuntimeException());
    //     assertThrows(RuntimeException.class, () -> lobbyService.updateGameSettings(testLobby.getLobbyId(), new GameSettings()));
    //     Mockito.verify(log).info("Something went wrong while updating settings");
    // }

}
