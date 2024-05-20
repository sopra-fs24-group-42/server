package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.GameState;
import ch.uzh.ifi.hase.soprafs24.constant.WinnerSide;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs24.repository.RepositoryProvider;
import ch.uzh.ifi.hase.soprafs24.utils.GameSettings;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@Transactional
public class LobbyServiceIntegrationTest {
  
  @Qualifier("playerRepository")
  @Autowired
  private PlayerRepository playerRepository;

  @Qualifier("lobbyRepository")
  @Autowired
  private LobbyRepository lobbyRepository;
  @Autowired
  private LobbyService lobbyService;

  @Autowired
  private RepositoryProvider repositoryProvider;

  private Lobby lobby;

  @BeforeEach
  public void setup() {

    lobby = new Lobby();
    lobby.setLobbyCode("12KL" + UUID.randomUUID().toString());
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
  }    
  
  @AfterEach
  public void tearDown() {
    playerRepository.deleteAll();
    lobbyRepository.deleteAll();
    lobbyRepository.flush();
    playerRepository.flush();
  }

  @Test
  public void getListOfLobbyPlayers_ShouldReturnPlayersInLobby() {
    createSamplePlayers(4);
    List<Player> players = lobbyService.getListOfLobbyPlayers(lobby.getLobbyCode());
    assertThat(players).hasSize(4);
  }

  private List<Player> createSamplePlayers(int count) {
    List<Player> players = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      Player player = new Player();
      player.setUsername("player" + i);
      player.setLobbyCode(lobby.getLobbyCode());
      player.setLobbyId(lobby.getLobbyId());
      player.setIsAlive(Boolean.TRUE);
      player.setIsKilled(Boolean.FALSE);
      player.setIsSacrificed(Boolean.FALSE);
      player.setIsProtected(Boolean.FALSE);
      player.setIsReady(Boolean.FALSE);
      player.setNumberOfVotes(0);
      player.setNumberOfVillagerWins(0);
      player.setNumberOfWerewolfWins(0);
      player.setNumberOfWins(0);
      player.setToken("1" + i);
      repositoryProvider.getPlayerRepository().save(player);
    }
    return players;
  }

  @Test
  public void checkIfLobbyFull_WhenFull_ShouldThrowException() {
    List<Player> setPlayers = createSamplePlayers(4);
    for (Player player : setPlayers) {
      player.setLobbyCode(lobby.getLobbyCode());
      repositoryProvider.getPlayerRepository().save(player);
    }

    assertThrows(ResponseStatusException.class, () -> {
      lobbyService.checkIfLobbyFull(lobby.getLobbyCode());
    });
  }

  @Test
  public void checkIfLobbyExists_WithNonExistingLobbyCode_ShouldThrowNotFoundException() {
    Player player = new Player();
    player.setUsername("player");
    player.setLobbyCode("not-existing-code");

    ResponseStatusException exception = catchThrowableOfType(
        () -> lobbyService.checkIfLobbyExists(player),
        ResponseStatusException.class);

    assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  public void setDefaultSettings_GivenValidLobby_ShouldReturnGameSettings() {
    GameSettings gameSettings = lobbyService.setDefaultSettings(lobby);
    assertThat(gameSettings).isNotNull();
  }

  @Test
  public void updateGameSettings_WithValidSettings_ShouldUpdateLobby() {
    GameSettings updatedGameSettings = new GameSettings();
    updatedGameSettings.setNumberOfWerewolves(10);
    updatedGameSettings.setNumberOfVillagers(3);
    updatedGameSettings.setNumberOfProtectors(1);
    updatedGameSettings.setNumberOfSeers(1);
    updatedGameSettings.setNumberOfSheriffs(1);
    lobbyService.updateGameSettings(lobby.getLobbyId(), updatedGameSettings);

    Lobby updatedLobby = repositoryProvider.getLobbyRepository().findByLobbyId(lobby.getLobbyId());
    assertThat(updatedLobby.getGameSettings()).isNotNull();
  }

  @Test
  public void resetLobby_ShouldResetLobbyState() {
    lobby.setWinnerSide(WinnerSide.VILLAGERS);
    Lobby savedLobby = repositoryProvider.getLobbyRepository().saveAndFlush(lobby);

    lobbyService.resetLobby(savedLobby.getLobbyId());

    Lobby resettedLobby = repositoryProvider.getLobbyRepository().findByLobbyId(savedLobby.getLobbyId());

    assertThat(resettedLobby).isNotNull();
    assertThat(resettedLobby.getWinnerSide()).isEqualTo(WinnerSide.NOWINNER);
  }

}
