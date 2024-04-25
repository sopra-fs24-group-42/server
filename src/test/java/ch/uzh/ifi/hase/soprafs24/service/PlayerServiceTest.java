package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import  ch.uzh.ifi.hase.soprafs24.constant.GameState;
import ch.uzh.ifi.hase.soprafs24.utils.GameSettings;
import ch.uzh.ifi.hase.soprafs24.repository.RepositoryProvider;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs24.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs24.service.ServiceProvider;
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

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

public class PlayerServiceTest {

    @Mock
    private RepositoryProvider repositoryProvider;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private LobbyRepository lobbyRepository;

    @InjectMocks
    private PlayerService playerService;

    private Player testPlayer;

    private Lobby testLobby;

    private GameSettings gameSettings;

    @BeforeEach
    public void setupPlayer() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(repositoryProvider.getPlayerRepository()).thenReturn(playerRepository);
        Mockito.when(repositoryProvider.getLobbyRepository()).thenReturn(lobbyRepository);

        this.testPlayer = Mockito.mock(Player.class);
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

        this.testLobby = Mockito.mock(Lobby.class);
        this.gameSettings = Mockito.mock(GameSettings.class);
        testLobby.setLobbyCode("ABCDE");
        testLobby.setLobbyId(2L);
        Mockito.when(lobbyRepository.save(Mockito.any())).thenReturn(testLobby);

    }

    @Test
    public void getLobbyIdFromPlayerByUsername_throw_error_invalid_username() {
        // mock the method findByUsername of the player repo to return null if player is not found
        Mockito.when(playerRepository.findByUsername(Mockito.any())).thenReturn(null);

        assertThrows(NullPointerException.class, () -> {
            playerService.getLobbyIdFromPlayerByUsername("testPlayer2");
        });
    }

    @Test
    public void getLobbyIdFromPlayerByUsername_success() {
        Mockito.when(playerRepository.findByUsername(Mockito.any())).thenReturn(testPlayer);
        Long lobbyId = playerService.getLobbyIdFromPlayerByUsername("testPlayer");

        Mockito.verify(playerRepository).findByUsername("testPlayer");
        assertEquals(testPlayer.getLobbyId(), lobbyId);
    }

    @Test
    public void assignRoles_success() {
        Long lobbyId = 2L;
        Player player1 = new Player("testPlayer1", "testLobbyCode");
        Player player2 = new Player("testPlayer2", "testLobbyCode");
        Player player3 = new Player("testPlayer3", "testLobbyCode");
        List<Player> players = Arrays.asList(player1, player2, player3);
        when(playerRepository.findByLobbyId(lobbyId)).thenReturn(players);

        // Set up the Lobby and GameSettings mock
        Lobby testLobby = new Lobby();
        GameSettings gameSettings = mock(GameSettings.class);

        ArrayList<String> roleList = new ArrayList<>(Arrays.asList("Werewolf", "Seer", "Villager"));
        when(gameSettings.RoleList()).thenReturn(roleList);

        testLobby.setGameSettings(gameSettings);
        when(lobbyRepository.findByLobbyId(lobbyId)).thenReturn(testLobby);

        // Execute the method under test
        playerService.assignRoles(lobbyId);

        // Collect the roles from players
        List<String> assignedRoles = players.stream().map(Player::getRoleName).collect(Collectors.toList());

        // Check that all expected roles are assigned
        assertTrue(assignedRoles.containsAll(roleList));
    }


    @Test
    public void assignRoles_notEnoughRoles_throwsError() {
        Long lobbyId = 2L;
        Player player1 = new Player("testPlayer1", "testLobbyCode");
        Player player2 = new Player("testPlayer2", "testLobbyCode");
        List<Player> players = Arrays.asList(player1, player2);
        when(playerRepository.findByLobbyId(lobbyId)).thenReturn(players);

        // Set up the Lobby and GameSettings mock
        Lobby testLobby = new Lobby();
        GameSettings gameSettings = mock(GameSettings.class);

        // Set up fewer roles than players
        ArrayList<String> roleList = new ArrayList<>(Arrays.asList("Werewolf", "Seer", "Villager"));  // Only two roles for three players
        when(gameSettings.RoleList()).thenReturn(roleList);

        testLobby.setGameSettings(gameSettings);
        when(lobbyRepository.findByLobbyId(lobbyId)).thenReturn(testLobby);

        // Expect an IllegalArgumentException to be thrown
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            playerService.assignRoles(lobbyId);
        });

        // Verify the message of the exception
        assertEquals("Not enough players or roles available for assignment.", exception.getMessage());
    }

//    @Test
//    public void setPlayerReady_success() {
//        Mockito.when(playerRepository.findByUsername(Mockito.any())).thenReturn(testPlayer);
//        Mockito.when(lobbyRepository.findByLobbyCode(Mockito.any())).thenReturn(testLobby);
//        Mockito.when(testLobby.getGameState()).thenReturn(GameState.NIGHT);
//
//        ArrayList<String> roleList = new ArrayList<>(Arrays.asList("Werewolf", "Seer", "Villager"));
//        when(gameSettings.RoleList()).thenReturn(roleList);
//        testLobby.setGameSettings(gameSettings);
//
//        Mockito.when(testPlayer.getIsReady()).thenReturn(false, true);
//
//        playerService.setPlayerReady(testPlayer.getUsername(), GameState.NIGHT);
//
//        Mockito.verify(testPlayer).setIsReady(true);
//        assertTrue(testPlayer.getIsReady());
//    }

    @Test
    public void voteForPlayer_success() {
        Mockito.when(repositoryProvider.getPlayerRepository().findByUsername(Mockito.any())).thenReturn(testPlayer);
        Mockito.when(testPlayer.getNumberOfVotes()).thenReturn(0, 1);

        playerService.voteForPlayer(testPlayer.getUsername());

        Mockito.verify(repositoryProvider.getPlayerRepository()).findByUsername(testPlayer.getUsername());

        Mockito.verify(testPlayer).getNumberOfVotes();
        Mockito.verify(testPlayer).setNumberOfVotes(1);
    }

    @Test
    public void killPlayer_success() {
        Mockito.when(repositoryProvider.getPlayerRepository().findByUsername(testPlayer.getUsername())).thenReturn(testPlayer);
        playerService.killPlayer(testPlayer.getUsername());

        Mockito.verify(repositoryProvider.getPlayerRepository()).findByUsername(testPlayer.getUsername());

        Mockito.verify(testPlayer).setIsKilled(true);
    }

}
