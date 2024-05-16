package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LeaderboardGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerPostDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;



public class DTOMapperTest {
  @Test
  void testCreatePlayer_fromPlayerPostDTO_toPlayer_success() {
    // create PlayerPostDTO
    PlayerPostDTO playerPostDTO = new PlayerPostDTO();
    playerPostDTO.setUsername("username");
    playerPostDTO.setLobbyCode("HG5fV");

    // MAP -> Create player
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

    // MAP -> Create lobby
    Lobby lobby = DTOMapper.INSTANCE.convertLobbyPostDTOtoEntity(lobbyPostDTO);

    // check content
    assertEquals(lobbyPostDTO.getHostName(), lobby.getHostName());
    assertEquals(lobbyPostDTO.getNumberOfPlayers(), lobby.getNumberOfPlayers());
  }

  @Test
  public void testGetLeaderboard_fromLeaderboard_toLeaderboardGetDTO_success() {
    // create Player
    Player player = new Player();
    player.setPlayerId(1L);
    player.setUsername("testPlayer");
    player.setNumberOfVillagerWins(1);
    player.setNumberOfWerewolfWins(1);
    player.setNumberOfWins(2);

    // MAP -> Create LeaderboardGetDTO
    LeaderboardGetDTO leaderboardGetDTO = DTOMapper.INSTANCE.convertEntityToLeaderboardGetDTO(player);

    // check content
    assertEquals(player.getUsername(), leaderboardGetDTO.getUsername());
    assertEquals(player.getNumberOfVillagerWins(), leaderboardGetDTO.getNumberOfVillagerWins());
    assertEquals(player.getNumberOfWerewolfWins(), leaderboardGetDTO.getNumberOfWerewolfWins());
    assertEquals(player.getNumberOfWins(), leaderboardGetDTO.getNumberOfWins());
  }

}
