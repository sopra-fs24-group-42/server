package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically
 * transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g.,
 * UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for
 * creating information (POST).
 */
@Mapper
public interface DTOMapper {

  DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

  @Mapping(source = "username", target = "username")
  Player convertPlayerPostDTOtoEntity(PlayerPostDTO playerPostDTO);

  @Mapping(source = "hostName", target = "hostName")
  @Mapping(source = "numberOfPlayers", target = "numberOfPlayers")
  Lobby convertLobbyPostDTOtoEntity(LobbyPostDTO lobbyPostDTO);

  // change here
  @Mapping(source = "playerId", target = "playerId")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "alive", target = "alive")
  @Mapping(source = "isProtected", target = "isProtected")
  @Mapping(source = "killed", target = "killed")
  @Mapping(source = "ready", target = "ready")
  @Mapping(source = "role", target = "role")
  PlayerDTO convertEntityToPlayerDTO(Player player);

  @Mapping(source = "lobbyId", target = "lobbyId")
  @Mapping(source = "hostName", target = "hostName")
  @Mapping(source = "lobbyCode", target = "lobbyCode")
  // @Mapping(source = "gameState", target = "gameState")
  @Mapping(source = "numberOfPlayers", target = "numberOfPlayers")
  @Mapping(source = "gameSettings", target = "gameSettings")
  LobbyDTO convertEntityToLobbyDTO(Lobby lobby);

}
