package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerDeleteDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DTOMapper {

    DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "lobbyCode", target = "lobbyCode")
    Player convertPlayerPostDTOtoEntity(PlayerPostDTO playerPostDTO);

    @Mapping(source = "username", target = "username")
    Player convertPlayerDeleteDTOtoEntity(PlayerDeleteDTO playerDeleteDTO);

    @Mapping(source = "hostName", target = "hostName")
    @Mapping(source = "numberOfPlayers", target = "numberOfPlayers")
    Lobby convertLobbyPostDTOtoEntity(LobbyPostDTO lobbyPostDTO);

    @Mapping(source = "playerId", target = "playerId")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "isAlive", target = "isAlive")
    @Mapping(source = "isProtected", target = "isProtected")
    @Mapping(source = "isKilled", target = "isKilled")
    @Mapping(source = "isReady", target = "isReady")
    @Mapping(source = "roleName", target = "roleName")
    @Mapping(source = "lobbyCode", target = "lobbyCode")
    @Mapping(source = "lobbyId", target = "lobbyId")
    PlayerDTO convertEntityToPlayerDTO(Player player);

    @Mapping(source = "lobbyId", target = "lobbyId")
    @Mapping(source = "hostName", target = "hostName")
    @Mapping(source = "lobbyCode", target = "lobbyCode")
    @Mapping(source = "gameState", target = "gameState")
    @Mapping(source = "numberOfPlayers", target = "numberOfPlayers")
    @Mapping(source = "gameSettings", target = "gameSettings")
    @Mapping(source = "players", target = "players")
    LobbyDTO convertEntityToLobbyDTO(Lobby lobby);

}
