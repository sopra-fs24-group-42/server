package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Player;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

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
    player.setLobbyId(1L);
    player.setLobbyCode("AG8HH");
    player.setIsAlive(Boolean.TRUE);
    player.setIsKilled(Boolean.FALSE);
    player.setIsProtected(Boolean.FALSE);
    player.setIsReady(Boolean.FALSE);
    player.setToken("1");
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
     Player found = playerRepository.findByLobbyCode(player.getLobbyCode());

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

    }
}
