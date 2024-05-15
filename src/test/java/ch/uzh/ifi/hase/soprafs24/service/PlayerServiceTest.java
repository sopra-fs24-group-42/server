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
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
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
        testPlayer.setIsSacrificed(Boolean.FALSE);
        testPlayer.setIsProtected(Boolean.FALSE);
        testPlayer.setIsReady(Boolean.FALSE);
        testPlayer.setNumberOfVotes(0);
        testPlayer.setNumberOfVillagerWins(0);
        testPlayer.setNumberOfWerewolfWins(0);
        testPlayer.setNumberOfWins(0);
        testPlayer.setToken("1");
        Mockito.when(playerRepository.save(Mockito.any())).thenReturn(testPlayer);

        this.testLobby = Mockito.mock(Lobby.class);
        this.gameSettings = Mockito.mock(GameSettings.class);
        testLobby.setLobbyCode("ABCDE");
        testLobby.setLobbyId(2L);
        testLobby.setGameState(GameState.WAITINGROOM);
        Mockito.when(lobbyRepository.save(Mockito.any())).thenReturn(testLobby);

    }

    @Test
    public void testCheckIfUserExists_UserDoesNotExists_ShouldPassWithoutException() {
        // Arrange
        Player playerToBeCreated = this.testPlayer;
        when(playerRepository.findByUsername(anyString())).thenReturn(null);

        // Act & Assert
        assertDoesNotThrow(() -> playerService.checkIfUserExists(playerToBeCreated));
    }

    @Test
    public void testCheckIfUserExists_UserExists_ShouldThrowResponseStatusException() {
        // Arrange
        Player existingPlayer = this.testPlayer;
        when(playerRepository.findByUsername(existingPlayer.getUsername())).thenReturn(existingPlayer);

        // Act & Assert
        ResponseStatusException thrownException = assertThrows(ResponseStatusException.class,
                () -> playerService.checkIfUserExists(existingPlayer));

        // Verify the ResponseStatusException details
        assertEquals(HttpStatus.CONFLICT, thrownException.getStatus());
        assertEquals("The username provided is not unique. Therefore, the player could not be created!",
                thrownException.getReason());
    }

    @Test
    void getLobbyIdFromPlayerByUsername_throw_error_invalid_username() {
        // mock the method findByUsername of the player repo to return null if player is not found
        Mockito.when(playerRepository.findByUsername(Mockito.any())).thenReturn(null);

        assertThrows(NullPointerException.class, () -> {
            playerService.getLobbyIdFromPlayerByUsername("testPlayer2");
        });
    }

    @Test
    void getLobbyIdFromPlayerByUsername_success() {
        Mockito.when(playerRepository.findByUsername(Mockito.any())).thenReturn(testPlayer);
        Long lobbyId = playerService.getLobbyIdFromPlayerByUsername("testPlayer");

        Mockito.verify(playerRepository).findByUsername("testPlayer");
        assertEquals(testPlayer.getLobbyId(), lobbyId);
    }

    @Test
    void assignRoles_success() {
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
    void assignRoles_notEnoughRoles_throwsError() {
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

        assertEquals("Not enough players or roles available for assignment.", exception.getMessage());
    }

    @Test
    void setPlayerReady_success() {
        GameState clientGameState = this.testLobby.getGameState();

        Player testPlayer = mock(Player.class);
        Lobby testLobby = mock(Lobby.class);
        
        when(playerRepository.findByUsername("testPlayer")).thenReturn(testPlayer);
        when(lobbyRepository.findByLobbyCode("ABCDE")).thenReturn(testLobby);
        when(testPlayer.getLobbyCode()).thenReturn("ABCDE");
        

        playerService.setPlayerReady("testPlayer", clientGameState);

        verify(repositoryProvider.getPlayerRepository()).findByUsername("testPlayer");
        verify(repositoryProvider.getLobbyRepository()).findByLobbyCode("ABCDE");
        verify(testPlayer).setIsReady(true);
        verify(playerRepository).save(testPlayer);
    }

    @Test
    void setPlayerReady_DifferentGameState_ShouldNotSetPlayerReady() {
        // Arrange
        GameState clientGameState = GameState.DISCUSSION;
        GameState serverGameState = GameState.VOTING;

        Player readyPlayer = mock(Player.class);
        Lobby lobbyOfReadyPlayer = mock(Lobby.class);

        when(playerRepository.findByUsername("testPlayer")).thenReturn(readyPlayer);
        when(lobbyRepository.findByLobbyCode(anyString())).thenReturn(lobbyOfReadyPlayer);
        when(readyPlayer.getLobbyCode()).thenReturn("ABC123");
        when(lobbyOfReadyPlayer.getGameState()).thenReturn(serverGameState);

        // Act
        playerService.setPlayerReady("testPlayer", clientGameState);

        // Assert
        verify(readyPlayer, never()).setIsReady(anyBoolean());
        verify(playerRepository, never()).save(any(Player.class));
    }

    @Test
    void testAreAllPlayersReady_AllReady() {
        Player player1 = new Player("testPlayer1", "testLobbyCode");
        player1.setIsAlive(true);
        player1.setIsReady(true);

        Player player2 = new Player("testPlayer2", "testLobbyCode");
        player2.setIsAlive(true);
        player2.setIsReady(true);
        List<Player> players = Arrays.asList(player1, player2);

        when(playerRepository.findByLobbyIdAndIsReady(1L, Boolean.TRUE)).thenReturn(players);
        when(lobbyRepository.findByLobbyId(1L)).thenReturn(testLobby);
        when(testLobby.getNumberOfPlayers()).thenReturn(2);

        boolean result = playerService.areAllPlayersReady(1L);

        assertTrue(result, "All players are ready, should return true");
    }

    @Test
    void testAreAllPlayersReady_SomeNotReady() {
        Player player1 = new Player("testPlayer1", "testLobbyCode");
        player1.setIsAlive(true);
        player1.setIsReady(true);

        Player player2 = new Player("testPlayer2", "testLobbyCode");
        player2.setIsAlive(true);
        player2.setIsReady(false);
        List<Player> readyPlayers = Arrays.asList(player1);

        when(playerRepository.findByLobbyIdAndIsReady(1L, Boolean.TRUE)).thenReturn(readyPlayers);
        when(lobbyRepository.findByLobbyId(1L)).thenReturn(testLobby);
        when(testLobby.getNumberOfPlayers()).thenReturn(2);

        boolean result = playerService.areAllPlayersReady(1L);

        assertFalse(result, "Not all players are ready, should return false");
    }

    @Test
    void setPlayersNotReady_AllAlivePlayersSetToNotReady() {
        Long lobbyId = 1L;
        Player player1 = mock(Player.class);
        Player player2 = mock(Player.class);

        List<Player> alivePlayers = Arrays.asList(player1, player2);
        when(playerRepository.findByLobbyIdAndIsAlive(lobbyId, Boolean.TRUE)).thenReturn(alivePlayers);

        playerService.setPlayersNotReady(lobbyId);

        // Check if setIsReady was called on all players with FALSE
        alivePlayers.forEach(player -> verify(player).setIsReady(Boolean.FALSE));

        // Ensure saveAll was called with the list of modified players
        verify(playerRepository).saveAll(alivePlayers);
        
        // Verify order of operations: first set players not ready, then save them
        InOrder inOrder = inOrder(playerRepository);
        inOrder.verify(playerRepository).findByLobbyIdAndIsAlive(lobbyId, Boolean.TRUE);
        inOrder.verify(playerRepository).saveAll(alivePlayers);
   }

    @Test
    void resetIsKilled_success() {
        Player player1 = mock(Player.class);
        Player player2 = mock(Player.class);
        List<Player> players = Arrays.asList(player1, player2);

        when(playerRepository.findByLobbyId(1L)).thenReturn(players);

        playerService.resetIsKilled(1L);

        verify(player1).setIsKilled(false);
        verify(player2).setIsKilled(false);
    }

    @Test
    void resetVotes_success() {
        Player player1 = mock(Player.class);
        Player player2 = mock(Player.class);
        List<Player> players = Arrays.asList(player1, player2);

        when(playerRepository.findByLobbyId(1L)).thenReturn(players);

        playerService.resetVotes(1L);

        verify(player1).setNumberOfVotes(0);
        verify(player2).setNumberOfVotes(0);
    }

    @Test
    void voteForPlayer_success() {
        Mockito.when(repositoryProvider.getPlayerRepository().findByUsername(Mockito.any())).thenReturn(testPlayer);
        Mockito.when(testPlayer.getNumberOfVotes()).thenReturn(0, 1);

        playerService.voteForPlayer(testPlayer.getUsername());

        Mockito.verify(repositoryProvider.getPlayerRepository()).findByUsername(testPlayer.getUsername());

        Mockito.verify(testPlayer).getNumberOfVotes();
        Mockito.verify(testPlayer).setNumberOfVotes(1);
    }

    @Test
    void killPlayer_success() {
        Mockito.when(repositoryProvider.getPlayerRepository().findByUsername(testPlayer.getUsername())).thenReturn(testPlayer);
        playerService.killPlayer(testPlayer.getUsername());

        Mockito.verify(repositoryProvider.getPlayerRepository()).findByUsername(testPlayer.getUsername());

        Mockito.verify(testPlayer).setIsKilled(true);
    }

    @Test
    void sacrificePlayer_success() {
        Mockito.when(repositoryProvider.getPlayerRepository().findByUsername(testPlayer.getUsername())).thenReturn(testPlayer);
        playerService.sacrificePlayer(testPlayer.getUsername());

        Mockito.verify(repositoryProvider.getPlayerRepository()).findByUsername(testPlayer.getUsername());

        Mockito.verify(testPlayer).setIsSacrificed(true);;
    }

    @Test
    void protectPlayer_success() {
        Mockito.when(repositoryProvider.getPlayerRepository().findByUsername(testPlayer.getUsername())).thenReturn(testPlayer);
        playerService.protectPlayer(testPlayer.getUsername());

        Mockito.verify(repositoryProvider.getPlayerRepository()).findByUsername(testPlayer.getUsername());

        Mockito.verify(testPlayer).setIsProtected(true);
    }

    @Test
    void resetPlayersByLobbyId_success() {
        //given
        Player player1 = mock(Player.class);
        Player player2 = mock(Player.class);
        List<Player> players = Arrays.asList(player1, player2);

        when(playerRepository.findByLobbyId(1L)).thenReturn(players);

        //test
        playerService.resetPlayersByLobbyId(1L);

        //assert
        for (Player player : players) {
            verify(player).setRoleName(null);
            verify(player).setIsAlive(true);
            verify(player).setIsProtected(false);
            verify(player).setIsKilled(false);
            verify(player).setIsReady(false);
            verify(player).setNumberOfVotes(0);
        }
        verify(playerRepository).saveAll(players);
        verify(playerRepository).flush();
    }

    @Test
    public void updateLeaderboardWerewolfWin_WerewolfPlayersIncrementWins() {
        // Arrange
        Long lobbyId = 1L;
        Player villager = new Player(); // Assume constructors/setters/getters provided
        villager.setRoleName("Villager");
        villager.setNumberOfWerewolfWins(0);
        villager.setNumberOfWins(3);

        Player werewolf = new Player();
        werewolf.setRoleName("Werewolf");
        werewolf.setNumberOfWerewolfWins(2);
        werewolf.setNumberOfWins(5);

        List<Player> players = Arrays.asList(villager, werewolf);
        
        // Mock findByLobbyId to return the prepared list of players
        when(playerRepository.findByLobbyId(lobbyId)).thenReturn(players);

        // Act
        playerService.updateLeaderboardWerewolfWin(lobbyId);

        // Assert
        verify(playerRepository).findByLobbyId(lobbyId);

        // Verify that the werewolf's win counters are incremented
        assertEquals(3, werewolf.getNumberOfWerewolfWins());
        assertEquals(6, werewolf.getNumberOfWins());

        // Verify that the villager's win counters are not changed
        assertEquals(0, villager.getNumberOfWerewolfWins());
        assertEquals(3, villager.getNumberOfWins());

        // Verify that saveAll was called with the updated list of players
        verify(playerRepository).saveAll(players);
    }

    @Test
    public void updateLeaderboardVillagerWin_NonWerewolfPlayersIncrementWins() {
        Long lobbyId = 1L;
        Player villager = createPlayerWithRole("Villager", 0, 0);
        Player seer = createPlayerWithRole("Seer", 5, 10);
        Player werewolf = createPlayerWithRole("Werewolf", 3, 7);

        List<Player> players = Arrays.asList(villager, seer, werewolf);
        when(playerRepository.findByLobbyId(lobbyId)).thenReturn(players);

        playerService.updateLeaderboardVillagerWin(lobbyId);

        // Check if wins have been updated for non-werewolf players
        verify(villager).setNumberOfVillagerWins(1);
        verify(villager).setNumberOfWins(1);
        verify(seer).setNumberOfVillagerWins(6);
        verify(seer).setNumberOfWins(11);
        verify(werewolf, never()).setNumberOfVillagerWins(anyInt());
        verify(werewolf, never()).setNumberOfWins(anyInt());

        // Verify all players have been saved, regardless of role
        verify(playerRepository).saveAll(players);
        
        // Verify order of operations: first update players, then save them
        InOrder inOrder = inOrder(playerRepository);
        inOrder.verify(playerRepository).findByLobbyId(eq(lobbyId));
        inOrder.verify(playerRepository).saveAll(players);
    }

    private Player createPlayerWithRole(String roleName, int villagerWins, int totalWins) {
        Player player = mock(Player.class);
        when(player.getRoleName()).thenReturn(roleName);
        when(player.getNumberOfVillagerWins()).thenReturn(villagerWins);
        when(player.getNumberOfWins()).thenReturn(totalWins);
        return player;
    }

    @Test
    public void getTopPlayers_ReturnsSortedLimitedList() {
        // Arrange
        int playerLimit = 3;
        List<Player> players = new ArrayList<>();

        Player player1 = new Player();
        player1.setNumberOfWins(4);
        players.add(player1);
        
        Player player2 = new Player();
        player2.setNumberOfWins(7);
        players.add(player2);
        
        Player player3 = new Player();
        player3.setNumberOfWins(5);
        players.add(player3);

        Player player4 = new Player();
        player4.setNumberOfWins(2);
        players.add(player4);

        when(playerRepository.findAll()).thenReturn(players);

        // Act
        List<Player> topPlayers = playerService.getTopPlayers(playerLimit);

        // Assert
        assertEquals(playerLimit, topPlayers.size());
        assertTrue(topPlayers.get(0).getNumberOfWins() >= topPlayers.get(1).getNumberOfWins());
        assertTrue(topPlayers.get(1).getNumberOfWins() >= topPlayers.get(2).getNumberOfWins());
    }
}
