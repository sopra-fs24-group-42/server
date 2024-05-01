package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.constant.GameState;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.repository.RepositoryProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;


@Service
@Transactional
public class PlayerService {

    private final Logger log = LoggerFactory.getLogger(PlayerService.class);

    private final RepositoryProvider repositoryProvider;

    @Autowired
    public PlayerService(RepositoryProvider repositoryProvider) {
        this.repositoryProvider = repositoryProvider;
    }

    public void checkIfUserExists(Player playerToBeCreated) {
        Player userByUsername = repositoryProvider.getPlayerRepository().findByUsername(playerToBeCreated.getUsername());

        if (userByUsername != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The username provided is not unique. Therefore, the player could not be created!");
        }
    }

    public Long getLobbyIdFromPlayerByUsername(String username) {
        Player player = repositoryProvider.getPlayerRepository().findByUsername(username);
        return player.getLobbyId();
    }

    public void assignRoles(Long lobbyId) {
        try {
            // Fetch players and roles
            List<Player> players = repositoryProvider.getPlayerRepository().findByLobbyId(lobbyId);
            ArrayList<String> roles = repositoryProvider.getLobbyRepository().findByLobbyId(lobbyId).getGameSettings().RoleList();
    
            // Check if there are enough roles and players
            if (players == null || roles == null || players.size() < roles.size()) {
                throw new IllegalArgumentException("Not enough players or roles available for assignment.");
            }
    
            // Shuffle roles
            Collections.shuffle(roles);
    
            // Assign roles to players
            for (int i = 0; i < roles.size(); i++) {
                players.get(i).setRoleName(roles.get(i));
            }
        } catch (Exception e) {
            // Log the exception and possibly throw it to be handled by a higher-level function
            log.info("An error occurred during role assignment: " + e.getMessage());
            throw e;
        }
    }

    public void setPlayerReady(String username, GameState clientGameState) {
        Player readyPlayer = repositoryProvider.getPlayerRepository().findByUsername(username);
        Lobby lobbyOfReadyPlayer = repositoryProvider.getLobbyRepository().findByLobbyCode(readyPlayer.getLobbyCode());

        //check if client and server are in same gameState
        if (lobbyOfReadyPlayer.getGameState() != clientGameState) {
            log.info("client of {} is not in the same gameState as the server!", username);
            return;
        }

        readyPlayer.setIsReady(true);

        log.info("User {} is set ready in lobby {}", readyPlayer.getUsername(), lobbyOfReadyPlayer.getLobbyId());
    }

    public boolean areAllPlayersReady(Long lobbyId) {
        //if too slow it could be implemented with SQL
        int countNotReady = 0;
        List<Player> players = repositoryProvider.getPlayerRepository().findByLobbyId(lobbyId);

        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getIsReady().equals(Boolean.FALSE)) {countNotReady++;}
        }

        log.info("{} Players are not ready yet", countNotReady);

        return countNotReady == 0;
    }

    public void setPlayersNotReady (Long lobbyId) {
        List<Player> alivePlayers = repositoryProvider.getPlayerRepository().findByLobbyIdAndIsAlive(lobbyId, Boolean.TRUE);

        for (Player player : alivePlayers) {
            player.setIsReady(Boolean.FALSE);
        }

        repositoryProvider.getPlayerRepository().saveAll(alivePlayers);
    }

    public void resetIsKilled (Long lobbyId) {
        List<Player> players = repositoryProvider.getPlayerRepository().findByLobbyId(lobbyId);

        for(Player player : players) {
            player.setIsKilled(Boolean.FALSE);
        }

        repositoryProvider.getPlayerRepository().saveAll(players);
    }

    public void resetVotes(Long lobbyId) {
        List<Player> players = repositoryProvider.getPlayerRepository().findByLobbyId(lobbyId);

        for(Player player : players) {
            player.setNumberOfVotes(0);
        }

        repositoryProvider.getPlayerRepository().saveAll(players);
    }

    public void voteForPlayer(String selectedPlayerName) {
        Player votedPlayer = repositoryProvider.getPlayerRepository().findByUsername(selectedPlayerName);
        votedPlayer.setNumberOfVotes(votedPlayer.getNumberOfVotes() + 1);
    }

    public void killPlayer (String unsername) {
        Player playerToKill = repositoryProvider.getPlayerRepository().findByUsername(unsername);
        
        playerToKill.setIsKilled(true);
    }

    public boolean playersLobbyEqual (String usernameOne, String usernameTwo) {

        return getLobbyIdFromPlayerByUsername(usernameOne).equals(getLobbyIdFromPlayerByUsername(usernameTwo));
    }

    public String getRoleByUsername (String username) {
        Player player = repositoryProvider.getPlayerRepository().findByUsername(username);
        return player.getRoleName();
    }

    public int numberOfPlayersAlive (Long lobbyId) {
        int count = 0;
        List<Player> players = repositoryProvider.getPlayerRepository().findByLobbyId(lobbyId);
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getIsAlive().equals(Boolean.TRUE)) {count++;}
        } 
        return count;
    }

    @Transactional
    public void resetPlayersByLobbyId (Long lobbyId) {
        List<Player> players = repositoryProvider.getPlayerRepository().findByLobbyId(lobbyId);

        for (Player player : players) {
            player.setRoleName(null);
            player.setIsAlive(Boolean.TRUE);
            player.setIsProtected(Boolean.FALSE);
            player.setIsKilled(Boolean.FALSE);
            player.setIsReady(Boolean.FALSE);
            player.setNumberOfVotes(0);
        }

        repositoryProvider.getPlayerRepository().saveAll(players);
        repositoryProvider.getPlayerRepository().flush();
    }
}
