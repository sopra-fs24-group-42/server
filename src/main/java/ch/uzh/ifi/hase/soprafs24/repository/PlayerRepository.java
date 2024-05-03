package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("playerRepository")
public interface PlayerRepository extends JpaRepository<Player, Long> {
    Player findByUsername(String username);
    List<Player> findByLobbyCode(String lobbyCode);
    List<Player> findByLobbyId(Long lobbyId);
    List<Player> findByLobbyIdAndIsKilled(Long lobbyId, Boolean isKilled);
    List<Player> findByLobbyIdAndIsAlive(Long lobbyId, Boolean isAlive);
    List<Player> findByLobbyIdAndIsSacrificed(Long lobbyId, Boolean.TRUE);
    void deleteByPlayerId(Long playerId);
}
