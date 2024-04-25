package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerPostDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class DTOMapperTest {
  @Test
  void testCreatePlayer_fromPlayerPostDTO_toPlayer_success() {
    // create UserPostDTO
    PlayerPostDTO playerPostDTO = new PlayerPostDTO();
    playerPostDTO.setUsername("username");
    playerPostDTO.setLobbyCode("HG5fV");

    // MAP -> Create user
    Player player = DTOMapper.INSTANCE.convertPlayerPostDTOtoEntity(playerPostDTO);

    // check content
    assertEquals(playerPostDTO.getUsername(), player.getUsername());
    assertEquals(playerPostDTO.getLobbyCode(), player.getLobbyCode());

  }

  @Test
  void testCreateLobby_fromLobbyPostDTO_toLobby_success() {
    // create LobbyPostDTO
    LobbyPostDTO lobbyPostDTO = new LobbyPostDTO();
    lobbyPostDTO.setHostName("testPlayer");
    lobbyPostDTO.setNumberOfPlayers(7);

    // MAP -> Create user
    Lobby lobby = DTOMapper.INSTANCE.convertLobbyPostDTOtoEntity(lobbyPostDTO);

    // check content
    assertEquals(lobbyPostDTO.getHostName(), lobby.getHostName());
    assertEquals(lobbyPostDTO.getNumberOfPlayers(), lobby.getNumberOfPlayers());
  }

}
