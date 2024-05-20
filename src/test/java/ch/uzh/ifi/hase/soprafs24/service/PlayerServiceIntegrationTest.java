package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.GameState;
import ch.uzh.ifi.hase.soprafs24.constant.WinnerSide;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs24.repository.RepositoryProvider;
import ch.uzh.ifi.hase.soprafs24.utils.GameSettings;

import org.springframework.beans.factory.annotation.Qualifier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@Transactional
public class PlayerServiceIntegrationTest {

    @Qualifier("playerRepository")
    @Autowired
    private PlayerRepository playerRepository;

    @Qualifier("lobbyRepository")
    @Autowired
    private LobbyRepository lobbyRepository;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private RepositoryProvider repositoryProvider;

    private Lobby lobby;

    private Player player;

    @BeforeEach
    public void setUp() {
        lobby = new Lobby();
        lobby.setLobbyCode("12KJ" + UUID.randomUUID().toString());
        lobby.setHostName("hostPlayer");
        lobby.setMinNumOfPlayers(4);
        lobby.setNumberOfPlayers(4);
        GameSettings gameSettings = new GameSettings();
        gameSettings.setNumberOfWerewolves(1);
        gameSettings.setNumberOfVillagers(1);
        gameSettings.setNumberOfProtectors(1);
        gameSettings.setNumberOfSeers(1);
        gameSettings.setNumberOfSheriffs(0);
        gameSettings.setNumberOfMayors(0);
        gameSettings.setNumberOfJesters(0);
        gameSettings.setNumberOfSacrifices(0);
        gameSettings.setNumberOfAmours(0);
        gameSettings.setNumberOfHunters(0);
        gameSettings.setNumberOfSwappers(0);
        lobby.setGameSettings(gameSettings);
        lobby.setGameState(GameState.NIGHT);
        lobby.setWinnerSide(WinnerSide.VILLAGERS);
        lobby = repositoryProvider.getLobbyRepository().save(lobby);
        repositoryProvider.getLobbyRepository().flush();

        player = new Player();
        player.setUsername("testPlayer");
        player.setLobbyCode(lobby.getLobbyCode());
        player.setIsAlive(Boolean.TRUE);
        player.setIsKilled(Boolean.FALSE);
        player.setIsSacrificed(Boolean.FALSE);
        player.setIsProtected(Boolean.FALSE);
        player.setIsReady(Boolean.FALSE);
        player.setNumberOfVotes(0);
        player.setNumberOfVillagerWins(0);
        player.setNumberOfWerewolfWins(0);
        player.setNumberOfWins(0);
        player.setToken("1");
        player.setLobbyId(lobby.getLobbyId());
        player = repositoryProvider.getPlayerRepository().saveAndFlush(player);
    }

    @AfterEach
    public void tearDown() {
        playerRepository.deleteAll();
        lobbyRepository.deleteAll();
        lobbyRepository.flush();
        playerRepository.flush();
    }

    @Test
    public void checkIfUserExists_ShouldThrowConflictIfUserExists() {
        assertThrows(ResponseStatusException.class, () -> {
            playerService.checkIfUserExists(player);
        });
    }

    @Test
    public void getLobbyIdFromPlayerByUsername_ShouldReturnCorrectLobbyId() {
        Long foundLobbyId = playerService.getLobbyIdFromPlayerByUsername(player.getUsername());
        assertEquals(lobby.getLobbyId(), foundLobbyId);
    }

    @Test
    public void assignRoles_ShouldAssignRolesToAllPlayersInLobby() {    
        createSamplePlayers(3);
        playerService.assignRoles(lobby.getLobbyId());

        List<Player> playersWithRoles = repositoryProvider.getPlayerRepository().findByLobbyId(lobby.getLobbyId());
        for (Player playerWithRole : playersWithRoles) {
            assertNotNull(playerWithRole.getRoleName());
        }
    }

    private List<Player> createSamplePlayers(int count) {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < count; i++) {
          Player samplePlayer = new Player();
          samplePlayer.setUsername("player" + i);
          samplePlayer.setIsAlive(Boolean.TRUE);
          samplePlayer.setIsKilled(Boolean.FALSE);
          samplePlayer.setIsSacrificed(Boolean.FALSE);
          samplePlayer.setIsProtected(Boolean.FALSE);
          samplePlayer.setIsReady(Boolean.FALSE);
          samplePlayer.setNumberOfVotes(0);
          samplePlayer.setNumberOfVillagerWins(0);
          samplePlayer.setNumberOfWerewolfWins(0);
          samplePlayer.setNumberOfWins(0);
          samplePlayer.setToken("1" + i);
          samplePlayer.setLobbyId(lobby.getLobbyId());
          samplePlayer.setLobbyCode(lobby.getLobbyCode());
          repositoryProvider.getPlayerRepository().save(samplePlayer);
        }
        return players;
      }
}
