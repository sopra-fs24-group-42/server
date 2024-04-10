package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PlayerService {

  private final Logger log = LoggerFactory.getLogger(PlayerService.class);

  private final PlayerRepository playerRepository;

  @Autowired
  public PlayerService(@Qualifier("playerRepository") PlayerRepository playerRepository) {
    this.playerRepository = playerRepository;
  }

  public List<Player> getUsers() {
    return this.playerRepository.findAll();
  }

  public Player createUser(Player newPlayer) {
    newPlayer.setToken(UUID.randomUUID().toString());
    checkIfUserExists(newPlayer);

    newPlayer.setAlive(Boolean.TRUE);
    newPlayer.setIsProtected(Boolean.FALSE);
    newPlayer.setKilled(Boolean.FALSE);
    newPlayer.setReady(Boolean.FALSE);

    // set the lobbyId ?

    newPlayer = playerRepository.save(newPlayer);
    playerRepository.flush();

    log.debug("Created Information for User: {}", newPlayer);
    return newPlayer;
  }

  private void checkIfUserExists(Player userToBeCreated) {
    Player userByUsername = playerRepository.findByUsername(userToBeCreated.getUsername());

    if (userByUsername != null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The username provided is not unique. Therefore, the user could not be created!");
    }
  }
}
