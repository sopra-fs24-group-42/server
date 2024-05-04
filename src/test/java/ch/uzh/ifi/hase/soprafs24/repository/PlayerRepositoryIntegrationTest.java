package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.utils.Role;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class PlayerRepositoryIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private PlayerRepository playerRepository;

  @Test
  void findByUsername_success() {
    // given
    Player player = new Player();
    player.setPlayerId(1L);
    player.setUsername("testPlayer");
    player.setLobbyId(2L);
    player.setLobbyCode("AG8HH");
    player.setIsAlive(Boolean.TRUE);
    player.setIsKilled(Boolean.FALSE);
    player.setIsProtected(Boolean.FALSE);
    player.setIsSacrificed(Boolean.FALSE);
    player.setIsReady(Boolean.FALSE);
    player.setToken("1");
    player.setRoleName("Seer");
    player.setNumberOfVotes(0);

    entityManager.merge(player);
    entityManager.flush();

    // when
    Player found = playerRepository.findByUsername(player.getUsername());

    // then
    assertNotNull(found.getPlayerId());
    assertEquals(found.getUsername(), player.getUsername());
    assertEquals(found.getLobbyId(), player.getLobbyId());
    assertEquals(found.getLobbyCode(), player.getLobbyCode());
    assertEquals(found.getToken(), player.getToken());
    assertEquals(found.getIsAlive(), player.getIsAlive());
    assertEquals(found.getIsProtected(), player.getIsProtected());
    assertEquals(found.getIsKilled(), player.getIsKilled());
    assertEquals(found.getIsReady(), player.getIsReady());
    assertEquals(found.getIsSacrificed(), player.getIsSacrificed());
    assertEquals(found.getToken(), player.getToken());
    assertEquals(found.getRoleName(), player.getRoleName());
  }

    @Test
    void findByLobbyCode_success() {
     // given
     Player player = new Player();
     player.setPlayerId(1L);
     player.setUsername("testPlayer");
     player.setLobbyId(1L);
     player.setLobbyCode("AG8HH");
     player.setIsAlive(Boolean.TRUE);
     player.setIsKilled(Boolean.FALSE);
     player.setIsProtected(Boolean.FALSE);
     player.setIsSacrificed(Boolean.FALSE);
     player.setIsReady(Boolean.FALSE);
     player.setToken("1");
     player.setRoleName("Seer");
     player.setNumberOfVotes(0);

     entityManager.merge(player);
     entityManager.flush();

     // return the list of players
     List<Player> founds = playerRepository.findByLobbyCode(player.getLobbyCode());

     // then
     assertNotNull(founds.get(0).getPlayerId());
     assertEquals(founds.get(0).getUsername(), player.getUsername());
     assertEquals(founds.get(0).getLobbyId(), player.getLobbyId());
     assertEquals(founds.get(0).getLobbyCode(), player.getLobbyCode());
     assertEquals(founds.get(0).getToken(), player.getToken());
     assertEquals(founds.get(0).getIsAlive(), player.getIsAlive());
     assertEquals(founds.get(0).getIsProtected(), player.getIsProtected());
     assertEquals(founds.get(0).getIsKilled(), player.getIsKilled());
     assertEquals(founds.get(0).getIsSacrificed(), player.getIsSacrificed());
     assertEquals(founds.get(0).getIsReady(), player.getIsReady());
     assertEquals(founds.get(0).getToken(), player.getToken());
     assertEquals(founds.get(0).getRoleName(), player.getRoleName());

    }

    @Test
    void findByLobbyIdAndIsKilled_empty_list_success() {
      // given
      Player player = new Player();
      player.setPlayerId(1L);
      player.setUsername("testPlayer");
      player.setLobbyId(1L);
      player.setLobbyCode("AG8HH");
      player.setIsAlive(Boolean.TRUE);
      player.setIsKilled(Boolean.FALSE);
      player.setIsProtected(Boolean.FALSE);
      player.setIsReady(Boolean.FALSE);
      player.setIsSacrificed(Boolean.FALSE);
      player.setToken("1");
      player.setRoleName("Seer");
      player.setNumberOfVotes(0);

      entityManager.merge(player);
      entityManager.flush();

      // return the list of players
      List<Player> founds = playerRepository.findByLobbyIdAndIsKilled(player.getLobbyId(), Boolean.TRUE);

      // assert the list is empty
    }

    @Test
    void findByLobbyIdAndIsAlive_success() {
    }

    @Test
    void deleteByPlayerId_success() {

    }
}
