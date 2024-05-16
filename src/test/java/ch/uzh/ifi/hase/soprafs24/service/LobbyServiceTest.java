package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.constant.GameState;
import ch.uzh.ifi.hase.soprafs24.constant.WinnerSide;
import ch.uzh.ifi.hase.soprafs24.utils.GameSettings;
import ch.uzh.ifi.hase.soprafs24.repository.RepositoryProvider;
import ch.uzh.ifi.hase.soprafs24.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ResponseStatusException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.ArrayList;

public class LobbyServiceTest {

    @Mock
    private RepositoryProvider repositoryProvider;

    @Mock
    private LobbyRepository lobbyRepository;

    @Mock
    private PlayerRepository playerRepository;

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
        lobby.setLobbyCode("HHHHH");
        lobby.setHostName("testPlayer");
        lobby.setMinNumOfPlayers(4);
        lobby.setNumberOfPlayers(4);
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

    // fix reading the logger
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

    // fix the test
    // @Test
    // void updateGameSettings_ExceptionThrown() {
    // Mockito.mock(GameSettings.class);
    // Mockito.when(lobbyRepository.findByLobbyId(testLobby.getLobbyId())).thenThrow(new
    // RuntimeException());
    // assertThrows(RuntimeException.class, () ->
    // lobbyService.updateGameSettings(testLobby.getLobbyId(), new GameSettings()));
    // Mockito.verify(log).info("Something went wrong while updating settings");
    // }

    // test for veryfing how lobby is reset
    @Test
    void reset_Lobby_success() {
        testLobby.setWinnerSide(WinnerSide.WEREWOLVES);

        Mockito.when(lobbyRepository.findByLobbyId(testLobby.getLobbyId())).thenReturn(testLobby);
        Mockito.when(repositoryProvider.getLobbyRepository()).thenReturn(lobbyRepository);

        lobbyService.resetLobby(testLobby.getLobbyId());

        ArgumentCaptor<Lobby> lobbyCaptor = ArgumentCaptor.forClass(Lobby.class);
        Mockito.verify(lobbyRepository).save(lobbyCaptor.capture());
        Mockito.verify(lobbyRepository).flush();

        Lobby capturedLobby = lobbyCaptor.getValue();
        assertEquals(WinnerSide.NOWINNER, capturedLobby.getWinnerSide());
    }

    // test that checks if lobby is full -> throws error
    // fix test
    @Test
    void givenLobby_lobby_is_full_throws_error() {
        List<Player> players = createSamplePlayers(4);
        Mockito.when(repositoryProvider.getPlayerRepository()).thenReturn(playerRepository);

        Mockito.when(lobbyRepository.findByLobbyCode(testLobby.getLobbyCode())).thenReturn(testLobby);
        Mockito.when(lobbyService.getListOfLobbyPlayers(testLobby.getLobbyCode())).thenReturn(players);

        assertThrows(ResponseStatusException.class, () -> lobbyService.checkIfLobbyFull(testLobby.getLobbyCode()),
                "The lobby is full. Therefore, the player could not be added!");
    }

    private List<Player> createSamplePlayers(int count) {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Player player = new Player();
            player.setUsername("testPlayer" + i);
            player.setLobbyCode("HHHHH");
        }
        return players;
    }

    // test to setDefaultSettings -> less that minnumber of players -> throws an
    // error
    @Test
    void setDefaultSettings_not_enough_players_throws_error() {
        testLobby.setNumberOfPlayers(3);

        assertThrows(NullPointerException.class, () -> {
            GameSettings settings = lobbyService.setDefaultSettings(testLobby);
        }, "Lobby needs more Players");
    }
}
