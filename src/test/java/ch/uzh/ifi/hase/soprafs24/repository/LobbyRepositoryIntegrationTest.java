package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.constant.GameState;
import ch.uzh.ifi.hase.soprafs24.constant.WinnerSide;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.utils.GameSettings;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class LobbyRepositoryIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private LobbyRepository lobbyRepository;

  @Test
  void findByLobbyCode_success() {
    // given
    Lobby lobby = new Lobby();
    lobby.setHostName("testHost");
    lobby.setLobbyCode("G4Hf6");
    lobby.setNumberOfPlayers(7);
    //lobby.setCountNightaction(0);
    lobby.setGameState(GameState.WAITINGROOM);
    lobby.setWinnerSide(WinnerSide.NOWINNER);
    lobby.setGameSettings(new GameSettings());
    lobby.setGameState(GameState.NIGHT);

    entityManager.persist(lobby);
    entityManager.flush();

    // when
    Lobby found = lobbyRepository.findByLobbyCode(lobby.getLobbyCode());

    // then
    assertNotNull(found.getLobbyId());
    assertEquals(found.getHostName(), lobby.getHostName());
    assertEquals(found.getLobbyCode(), lobby.getLobbyCode());
    assertEquals(found.getNumberOfPlayers(), lobby.getNumberOfPlayers());
    assertEquals(found.getGameSettings(), lobby.getGameSettings());
    assertEquals(found.getGameState(), lobby.getGameState());

  }

    @Test
    void findByLobbyId_success() {
      // given
      Lobby lobby = new Lobby();
      lobby.setHostName("testHost");
      lobby.setLobbyCode("G4Hf6");
      lobby.setNumberOfPlayers(7);
      lobby.setGameSettings(new GameSettings());
      lobby.setGameState(GameState.NIGHT);
      lobby.setWinnerSide(WinnerSide.NOWINNER);

      entityManager.persist(lobby);
      entityManager.flush();

      // when
      Lobby found = lobbyRepository.findByLobbyId(lobby.getLobbyId());

      // then
      assertNotNull(found.getLobbyId());
      assertEquals(found.getHostName(), lobby.getHostName());
      assertEquals(found.getLobbyCode(), lobby.getLobbyCode());
      assertEquals(found.getNumberOfPlayers(), lobby.getNumberOfPlayers());
      assertEquals(found.getGameSettings(), lobby.getGameSettings());
      assertEquals(found.getGameState(), lobby.getGameState());

  }

}
