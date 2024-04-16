package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Role;
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
  public void findByUsername_success() {
    // given
    Player player = new Player();
    player.setPlayerId(1L);
    player.setUsername("testPlayer");
    player.setLobbyId(2L);
    player.setLobbyCode("AG8HH");
    player.setIsAlive(Boolean.TRUE);
    player.setIsKilled(Boolean.FALSE);
    player.setIsProtected(Boolean.FALSE);
    player.setIsReady(Boolean.FALSE);
    player.setToken("1");
    player.setRole(new Role());
    //player.setRoleName("1");

    entityManager.persist(player);
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
    assertEquals(found.getToken(), player.getToken());
    assertEquals(found.getRole(), player.getRole());
  }


    // redo
    @Test
    public void findByLobbyCode_throws_error() {
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
     player.setToken("1");

     entityManager.persist(player);
     entityManager.flush();

     // return the list of players
     // for loop ?
     List<Player> founds = playerRepository.findByLobbyCode(player.getLobbyCode());

     // then
     assertNotNull(founds.get(1).getPlayerId());
     assertEquals(founds.get(1).getUsername(), player.getUsername());
     assertEquals(founds.get(1).getLobbyId(), player.getLobbyId());
     assertEquals(founds.get(1).getLobbyCode(), player.getLobbyCode());
     assertEquals(founds.get(1).getToken(), player.getToken());
     assertEquals(founds.get(1).getIsAlive(), player.getIsAlive());
     assertEquals(founds.get(1).getIsProtected(), player.getIsProtected());
     assertEquals(founds.get(1).getIsKilled(), player.getIsKilled());
     assertEquals(founds.get(1).getIsReady(), player.getIsReady());
     assertEquals(founds.get(1).getToken(), player.getToken());

    }
}
